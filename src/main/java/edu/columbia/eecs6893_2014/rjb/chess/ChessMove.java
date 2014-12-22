package edu.columbia.eecs6893_2014.rjb.chess;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;

/**
 * Represents a move in chess. Also contains helper logic for parsing moves.
 *<p>
 * Note that this represents specifically one piece moving from one tile to another.
 * Castling is represented as two {@link ChessMove}s.
 */
public class ChessMove {
	final ChessPiece movingPiece; // The piece that is being moved.
	final ChessboardCoordinates destination; // Where the piece is being moved to.
	final boolean isChecking; // Whether the move puts the opponent in check.
	final boolean isCheckmate; // Whether the move puts the opponent in checkmate.
	@Nullable final ChessPieceType promotion; // If applicable, the new type of the piece moving.
	final boolean isCapture; // Whether this move captures a piece.

	public ChessMove(ChessPiece movingPiece, ChessboardCoordinates destination, boolean isChecking,
			boolean isCheckmate, @Nullable ChessPieceType promotion, boolean isCapture) {
		this.movingPiece = checkNotNull(movingPiece, "movingPiece");
		this.destination = checkNotNull(destination, "destination");
		this.isChecking = isChecking;
		this.isCheckmate = isCheckmate;
		this.promotion = promotion;
		this.isCapture = isCapture;
	}

	/**
	 * Parses a standard algebraic notation (SAN) move, as detailed in the PGN specification.
	 *<p>
	 * This returns a collection of moves because castling results in two moves.
	 */
	public static ImmutableCollection<ChessMove> parseSanMove(String sanMove, ChessPlayer player,
			Chessboard currentBoard) {
		StringBuffer sanMoveBuffer = new StringBuffer(sanMove);
		if (isCastling(sanMoveBuffer)) {
			return handleCastling(sanMoveBuffer, player, currentBoard);
		} else {
			return handleNormalMove(sanMoveBuffer, player, currentBoard);
		}
	}

	/**
	 * Checks for the "O" indicating castling.
	 */
	private static boolean isCastling(StringBuffer sanMoveBuffer) {
		return sanMoveBuffer.charAt(0) == 'O';
	}

	/**
	 * Returns the {@link ChessMove}s of the king and rook for a castling move.
	 */
	private static ImmutableCollection<ChessMove> handleCastling(StringBuffer sanMoveBuffer,
			ChessPlayer player, Chessboard currentBoard) {
		boolean isChecking = handleIsChecking(sanMoveBuffer);
		boolean isCheckmate = handleIsCheckmate(sanMoveBuffer);
		checkState(handlePromotion(sanMoveBuffer, ChessPieceType.KING) == null,
				"castling doesn't promote any pieces");
		checkState(!handleIsCapture(sanMoveBuffer), "pieces cannot be captured by castling");

		int rank = (player == ChessPlayer.BLACK) ? 7 : 0;
		ChessboardCoordinates kingStart, kingEnd, rookStart, rookEnd;
		if (sanMoveBuffer.toString().equals("O-O-O")) {
			kingStart = new ChessboardCoordinates(rank, 4);
			kingEnd = new ChessboardCoordinates(rank, 2);
			rookStart = new ChessboardCoordinates(rank, 0);
			rookEnd = new ChessboardCoordinates(rank, 3);
		} else if (sanMoveBuffer.toString().equals("O-O")) {
			kingStart = new ChessboardCoordinates(rank, 4);
			kingEnd = new ChessboardCoordinates(rank, 6);
			rookStart = new ChessboardCoordinates(rank, 7);
			rookEnd = new ChessboardCoordinates(rank, 5);
		} else {
			throw new IllegalStateException("unexpected content in sanMoveBuffer: " + sanMoveBuffer);
		}

		ChessPiece king = checkNotNull(currentBoard.getPieceAtCoordinates(kingStart),
				"king not found");
		checkState(king.getType() == ChessPieceType.KING,
				"piece at expected king location is not a king");
		ChessPiece rook = checkNotNull(currentBoard.getPieceAtCoordinates(rookStart),
				"rook not found");
		checkState(rook.getType() == ChessPieceType.ROOK,
				"piece at expected rook location is not a rook");

		return ImmutableList.<ChessMove> of(
				// Note that a king can't directly put another king in check.
				new ChessMove(king, kingEnd, /* isChecking */ false, /* isCheckmate */ false,
						/* promotion */ null, /* isCapture */ false),
				new ChessMove(rook, rookEnd, isChecking, isCheckmate,
						/* promotion */ null, /* isCapture */ false));
	}

	/**
	 * Returns the {@link ChessMove} for a non-castling move.
	 */
	private static ImmutableCollection<ChessMove> handleNormalMove(StringBuffer sanMoveBuffer,
			ChessPlayer player, Chessboard currentBoard) {
		ChessPieceType type = handleType(sanMoveBuffer);
		boolean isChecking = handleIsChecking(sanMoveBuffer);
		boolean isCheckmate = handleIsCheckmate(sanMoveBuffer);
		ChessPieceType promotion = handlePromotion(sanMoveBuffer, type);
		boolean isCapture = handleIsCapture(sanMoveBuffer);
		ChessboardCoordinates destination = handleDestination(sanMoveBuffer);

		// After the handlers above, the only thing left in sanMoveBuffer
		// is an optional rank and an optional file for disambiguating
		// which piece is moving.
		Integer optionalRank = null; // 0-indexed
		Integer optionalFile = null; // 0-indexed
		if (sanMoveBuffer.length() == 0) {
			// Do nothing, no disambiguating rank or file provided.
		} else if (sanMoveBuffer.length() == 1) {
			char character = sanMoveBuffer.charAt(0);
			if (ChessboardCoordinates.RANK_DIGITS.indexOf(character) >= 0) {
				optionalRank = ChessboardCoordinates.RANK_DIGITS.indexOf(character);
			} else if (ChessboardCoordinates.FILE_LETTERS.indexOf(character) >= 0) {
				optionalFile = ChessboardCoordinates.FILE_LETTERS.indexOf(character);
			} else {
				throw new IllegalStateException("invalid contents in buffer: " + sanMoveBuffer);
			}
		} else if (sanMoveBuffer.length() == 2) {
			// Note that this case should never happen, since providing only a
			// disambiguating rank or file should be sufficient, but we handle it anyway.
			optionalRank = ChessboardCoordinates.RANK_DIGITS.indexOf(sanMoveBuffer.charAt(1));
			optionalFile = ChessboardCoordinates.FILE_LETTERS.indexOf(sanMoveBuffer.charAt(0));
			checkState(optionalRank >= 0, "invalid rank in sanMoveBuffer: " + sanMoveBuffer);
			checkState(optionalFile >= 0, "invalid file in sanMoveBuffer: " + sanMoveBuffer);
		} else {
			throw new IllegalStateException("too many characters left in sanMoveBuffer");
		}

		ChessPiece piece = currentBoard.getMovingPiece(type, player, destination, optionalRank,
				optionalFile, isCapture);

		return ImmutableList.<ChessMove>of(new ChessMove(
				piece, destination, isChecking, isCheckmate, promotion, isCapture));
	}

	/**
	 * Checks for the "[KQRBN]" indicating the piece type, and strips it.
	 */
	private static ChessPieceType handleType(StringBuffer sanMoveBuffer) {
		// Moves start out with an abbreviation indicating the type of piece being moved.
		switch (sanMoveBuffer.charAt(0)) {
			case 'K':
				sanMoveBuffer.deleteCharAt(0);
				return ChessPieceType.KING;
			case 'Q':
				sanMoveBuffer.deleteCharAt(0);
				return ChessPieceType.QUEEN;
			case 'R':
				sanMoveBuffer.deleteCharAt(0);
				return ChessPieceType.ROOK;
			case 'B':
				sanMoveBuffer.deleteCharAt(0);
				return ChessPieceType.BISHOP;
			case 'N':
				sanMoveBuffer.deleteCharAt(0);
				return ChessPieceType.KNIGHT;
			default:
				// Pawn moves do not include the abbreviation.
				return ChessPieceType.PAWN;
		}
	}

	/**
	 * Checks for the "+" indicating checking, and strips it.
	 */
	private static boolean handleIsChecking(StringBuffer sanMoveBuffer) {
		// "+" at the end of the token indicates a checking move.
		int plusIndex = sanMoveBuffer.indexOf("+");
		if (plusIndex < 0) {
			return false;
		} else {
			checkState(plusIndex == (sanMoveBuffer.length() - 1),
					"\"+\" should be the last character");
			sanMoveBuffer.deleteCharAt(plusIndex);
			return true;
		}
	}

	/**
	 * Checks for the "#" indicating checkmate, and strips it.
	 */
	private static boolean handleIsCheckmate(StringBuffer sanMoveBuffer) {
		// "#" at the end of the token indicates a checkmate.
		int hashIndex = sanMoveBuffer.indexOf("#");
		if (hashIndex < 0) {
			return false;
		} else {
			checkState(hashIndex == (sanMoveBuffer.length() - 1),
					"\"#\" should be the last character");
			sanMoveBuffer.deleteCharAt(hashIndex);
			return true;
		}
	}

	/**
	 * Checks for the "=[QRBN]" indicating promotion, and strips it.
	 */
	@Nullable static private ChessPieceType handlePromotion(StringBuffer sanMoveBuffer,
			ChessPieceType currentType) {
		ChessPieceType promotion = null;
		int equalsIndex = sanMoveBuffer.indexOf("=");
		if (equalsIndex < 0) {
			return null;
		} else {
			checkState(currentType == ChessPieceType.PAWN, "only pawns can be promoted");
			checkState(equalsIndex == (sanMoveBuffer.length() - 2),
					"\"=\" should be second-to-last character");
			switch (sanMoveBuffer.charAt(sanMoveBuffer.length() - 1)) {
				case 'Q':
					promotion = ChessPieceType.QUEEN;
					break;
				case 'R':
					promotion = ChessPieceType.ROOK;
					break;
				case 'B':
					promotion = ChessPieceType.BISHOP;
					break;
				case 'N':
					promotion = ChessPieceType.KNIGHT;
					break;
				default:
					throw new IllegalStateException("promotion type invalid");
			}
			sanMoveBuffer.delete(sanMoveBuffer.length() - 2, sanMoveBuffer.length());
			return promotion;
		}
	}

	/**
	 * Checks for the "x" indicating capture, and strips it.
	 */
	private static boolean handleIsCapture(StringBuffer sanMoveBuffer) {
		// "x" right before the destination coordinates represents a capture.
		int xIndex = sanMoveBuffer.indexOf("x");
		if (xIndex < 0) {
			return false;
		} else {
			checkState(xIndex == (sanMoveBuffer.length() - 3),
					"\"x\" should be followed by only two characters");
			sanMoveBuffer.deleteCharAt(xIndex);
			return true;
		}
	}

	/**
	 * Checks for the "[a-h][1-8]" indicating the destination, and strips it.
	 */
	private static ChessboardCoordinates handleDestination(StringBuffer sanMoveBuffer) {
		// Moves contain the destination in standard (file, rank) form, for example: e6
		String file = sanMoveBuffer.substring(sanMoveBuffer.length() - 2,
				sanMoveBuffer.length() - 1);
		String rank = sanMoveBuffer.substring(sanMoveBuffer.length() - 1,
				sanMoveBuffer.length());
		ChessboardCoordinates destination = new ChessboardCoordinates(rank, file);
		sanMoveBuffer.delete(sanMoveBuffer.length() - 2, sanMoveBuffer.length());
		return destination;
	}
	
	@Override
	public boolean equals(Object other) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public int hashCode() {
		throw new RuntimeException("not implemented");
	}

	@Override
	public String toString() {
		throw new RuntimeException("not implemented");
	}
}
