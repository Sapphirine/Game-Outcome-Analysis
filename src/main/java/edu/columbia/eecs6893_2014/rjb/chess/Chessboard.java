package edu.columbia.eecs6893_2014.rjb.chess;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import javax.annotation.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

/**
 * Represents a mutable chessboard state.
 */
public class Chessboard {
	// All of the pieces on this board.
	// Note that it is mutable, as are the pieces therein.
	protected final Set<ChessPiece> pieces;

	/**
	 * Constructs a chessboard with all pieces in their starting positions.
	 */
	public Chessboard() {
		HashSet<ChessPiece> pieces = new HashSet<ChessPiece>();
		pieces.add(new ChessPiece(ChessPieceType.ROOK, ChessPlayer.BLACK, 7, 0));
		pieces.add(new ChessPiece(ChessPieceType.KNIGHT, ChessPlayer.BLACK, 7, 1));
		pieces.add(new ChessPiece(ChessPieceType.BISHOP, ChessPlayer.BLACK, 7, 2));
		pieces.add(new ChessPiece(ChessPieceType.QUEEN, ChessPlayer.BLACK, 7, 3));
		pieces.add(new ChessPiece(ChessPieceType.KING, ChessPlayer.BLACK, 7, 4));
		pieces.add(new ChessPiece(ChessPieceType.BISHOP, ChessPlayer.BLACK, 7, 5));
		pieces.add(new ChessPiece(ChessPieceType.KNIGHT, ChessPlayer.BLACK, 7, 6));
		pieces.add(new ChessPiece(ChessPieceType.ROOK, ChessPlayer.BLACK, 7, 7));
		pieces.add(new ChessPiece(ChessPieceType.PAWN, ChessPlayer.BLACK, 6, 0));
		pieces.add(new ChessPiece(ChessPieceType.PAWN, ChessPlayer.BLACK, 6, 1));
		pieces.add(new ChessPiece(ChessPieceType.PAWN, ChessPlayer.BLACK, 6, 2));
		pieces.add(new ChessPiece(ChessPieceType.PAWN, ChessPlayer.BLACK, 6, 3));
		pieces.add(new ChessPiece(ChessPieceType.PAWN, ChessPlayer.BLACK, 6, 4));
		pieces.add(new ChessPiece(ChessPieceType.PAWN, ChessPlayer.BLACK, 6, 5));
		pieces.add(new ChessPiece(ChessPieceType.PAWN, ChessPlayer.BLACK, 6, 6));
		pieces.add(new ChessPiece(ChessPieceType.PAWN, ChessPlayer.BLACK, 6, 7));
		pieces.add(new ChessPiece(ChessPieceType.PAWN, ChessPlayer.WHITE, 1, 0));
		pieces.add(new ChessPiece(ChessPieceType.PAWN, ChessPlayer.WHITE, 1, 1));
		pieces.add(new ChessPiece(ChessPieceType.PAWN, ChessPlayer.WHITE, 1, 2));
		pieces.add(new ChessPiece(ChessPieceType.PAWN, ChessPlayer.WHITE, 1, 3));
		pieces.add(new ChessPiece(ChessPieceType.PAWN, ChessPlayer.WHITE, 1, 4));
		pieces.add(new ChessPiece(ChessPieceType.PAWN, ChessPlayer.WHITE, 1, 5));
		pieces.add(new ChessPiece(ChessPieceType.PAWN, ChessPlayer.WHITE, 1, 6));
		pieces.add(new ChessPiece(ChessPieceType.PAWN, ChessPlayer.WHITE, 1, 7));
		pieces.add(new ChessPiece(ChessPieceType.ROOK, ChessPlayer.WHITE, 0, 0));
		pieces.add(new ChessPiece(ChessPieceType.KNIGHT, ChessPlayer.WHITE, 0, 1));
		pieces.add(new ChessPiece(ChessPieceType.BISHOP, ChessPlayer.WHITE, 0, 2));
		pieces.add(new ChessPiece(ChessPieceType.QUEEN, ChessPlayer.WHITE, 0, 3));
		pieces.add(new ChessPiece(ChessPieceType.KING, ChessPlayer.WHITE, 0 , 4));
		pieces.add(new ChessPiece(ChessPieceType.BISHOP, ChessPlayer.WHITE, 0 , 5));
		pieces.add(new ChessPiece(ChessPieceType.KNIGHT, ChessPlayer.WHITE, 0 , 6));
		pieces.add(new ChessPiece(ChessPieceType.ROOK, ChessPlayer.WHITE, 0 , 7));
		this.pieces = pieces;
	}

	/**
	 * Constructs a chessboard with a specific set of pieces.
	 */
	Chessboard(Set<ChessPiece> pieces) {
		this.pieces = checkNotNull(pieces, "pieces");
	}

	public Set<? extends ChessPiece> getPieces() {
		return pieces;
	}

	/**
	 * Returns the piece at the given coordinates. Returns null if there is
	 * no piece at the coordinates, or the coordinates are invalid.
	 */
	@Nullable public ChessPiece getPieceAtCoordinates(ChessboardCoordinates coordinates) {
		for (ChessPiece piece : pieces) {
			if (piece.getPosition().equals(coordinates)) {
				return piece;
			}
		}
		return null;
	}

	/**
	 * This method takes in a parsed SAN move (see
	 * {@link ChessMove#parseSanMove(String, ChessPlayer, Chessboard)})
	 * and returns the {@link ChessPiece} on this board that the move refers to.
	 */
	public ChessPiece getMovingPiece(ChessPieceType type, ChessPlayer player,
			ChessboardCoordinates destination, @Nullable Integer optionalRank,
			@Nullable Integer optionalFile, boolean isCapture) {

		// Generate a naive list of pieces which could move to
		// the specified destination:
		List<ChessPiece> possiblePieces;
		switch (type) {
			case KING:
				possiblePieces = getPossibleKings(player, destination);
				break;
			case QUEEN:
				possiblePieces = getPossibleQueens(player, destination);
				break;
			case ROOK:
				possiblePieces = getPossibleRooks(player, destination);
				break;
			case BISHOP:
				possiblePieces = getPossibleBishops(player, destination);
				break;
			case KNIGHT:
				possiblePieces = getPossibleKnights(player, destination);
				break;
			case PAWN:
				possiblePieces = getPossiblePawns(player, destination, isCapture);
				break;
			default:
				throw new IllegalArgumentException("unexpected type: " + type);
		}

		// Filter out pieces using the optional rank and optional file:
		possiblePieces = Lists.newArrayList(possiblePieces);
		ListIterator<ChessPiece> iterator = possiblePieces.listIterator();
		while (iterator.hasNext()) {
			ChessPiece next = iterator.next();
			ChessboardCoordinates position = next.getPosition();
			if (optionalRank != null && optionalRank != position.rank) {
				iterator.remove();
			} else if (optionalFile != null && optionalFile != position.file) {
				iterator.remove();
			} else {
				// Do nothing, the piece fits the optional constraints.
			}
		}

		// Normally, when multiple pieces with the correct owner and type can move
		// to the destination tile, the optional rank or file is used to disambiguate.
		// (That is what we handled in the check of code right above this one.)
		// However, an optional rank or file is not the only way to disambiguate.
		// If a player moving a piece would put their own king in check, then that
		// move is not valid.
		if (possiblePieces.size() > 1) {
			iterator = possiblePieces.listIterator();
			while (iterator.hasNext()) {
				ChessPiece next = iterator.next();

				// Save the current board state.
				ChessPiece captured = null;
				ChessboardCoordinates nextOriginalPosition = next.getPosition();
				if (isCapture) {
					captured = getPieceAtCoordinates(destination);
				}

				// Temporarily change the board state.
				if (isCapture) {
					pieces.remove(captured);
				}
				next.setPosition(destination);

				// Test whether the king would be in check.
				boolean kingIsInCheck = isKingInCheck(player);

				// Roll back to the original board state.
				next.setPosition(nextOriginalPosition);
				if (isCapture) {
					pieces.add(captured);
				}

				// If the king was going to be in check,
				// this move is not actually valid.
				if (kingIsInCheck) {
					iterator.remove();
				}
			}
		}

		// Do some final sanity checks and return the moving piece:
		checkState(possiblePieces.size() != 0, "no possible pieces found");
		checkState(possiblePieces.size() == 1, "more than one possible piece found");
		checkState(possiblePieces.get(0).getType() == type, "found piece of wrong type");
		checkState(possiblePieces.get(0).owner == player, "found piece with wrong owner");
		return possiblePieces.get(0);
	}

	/**
	 * Returns true of the specified player's king is in check.
	 */
	public boolean isKingInCheck(ChessPlayer kingOwner) {
		ChessPiece king = null;
		for (ChessPiece piece : pieces) {
			if (piece.getType() == ChessPieceType.KING && piece.owner == kingOwner) {
				king = piece;
			}
		}
		checkNotNull(king, "king should not be null");
		ChessPlayer otherPlayer = (king.owner == ChessPlayer.BLACK)
				? ChessPlayer.WHITE
				: ChessPlayer.BLACK;
		return isLocationThreatenedBy(king.getPosition(), otherPlayer);
	}

	/**
	 * Returns true of the specified location is threatened by the other player.
	 */
	public boolean isLocationThreatenedBy(ChessboardCoordinates location, ChessPlayer player) {
		List<ChessPiece> possiblePieces = ImmutableList.<ChessPiece>builder()
				.addAll(getPossibleKings(player, location))
				.addAll(getPossibleQueens(player, location))
				.addAll(getPossibleRooks(player, location))
				.addAll(getPossibleBishops(player, location))
				.addAll(getPossibleKnights(player, location))
				.addAll(getPossiblePawns(player, location, /* isCapture */ true))
				.build();
		return !possiblePieces.isEmpty();
	}

	/**
	 * Returns all the kings owned by the specified player that can reach the specified tile.
	 */
	private List<ChessPiece> getPossibleKings(ChessPlayer player, ChessboardCoordinates destination) {
		List<ChessboardCoordinates> possiblePositions = ImmutableList.of(
				new ChessboardCoordinates(destination.rank + 1, destination.file - 1),
				new ChessboardCoordinates(destination.rank + 1, destination.file),
				new ChessboardCoordinates(destination.rank + 1, destination.file + 1),
				new ChessboardCoordinates(destination.rank, destination.file - 1),
				new ChessboardCoordinates(destination.rank, destination.file + 1),
				new ChessboardCoordinates(destination.rank - 1, destination.file - 1),
				new ChessboardCoordinates(destination.rank - 1, destination.file),
				new ChessboardCoordinates(destination.rank - 1, destination.file + 1));
		return filterPieces(getAllPieces(possiblePositions), ChessPieceType.KING, player);
	}

	/**
	 * Returns all the queens owned by the specified player that can reach the specified tile.
	 */
	private List<ChessPiece> getPossibleQueens(ChessPlayer player, ChessboardCoordinates destination) {
		List<ChessPiece> possiblePieces = ImmutableList.<ChessPiece>builder()
				.addAll(getClosestSraightPieces(destination))
				.addAll(getClosestDiagonalPieces(destination))
				.build();
		return filterPieces(possiblePieces, ChessPieceType.QUEEN, player);
	}

	/**
	 * Returns all the rooks owned by the specified player that can reach the specified tile.
	 */
	private List<ChessPiece> getPossibleRooks(ChessPlayer player, ChessboardCoordinates destination) {
		List<ChessPiece> possiblePieces = ImmutableList.<ChessPiece>builder()
				.addAll(getClosestSraightPieces(destination))
				.build();
		return filterPieces(possiblePieces, ChessPieceType.ROOK, player);
	}

	/**
	 * Returns all the bishops owned by the specified player that can reach the specified tile.
	 */
	private List<ChessPiece> getPossibleBishops(ChessPlayer player,
			ChessboardCoordinates destination) {
		List<ChessPiece> possiblePieces = ImmutableList.<ChessPiece>builder()
				.addAll(getClosestDiagonalPieces(destination))
				.build();
		return filterPieces(possiblePieces, ChessPieceType.BISHOP, player);
	}

	/**
	 * Returns all the knights owned by the specified player that can reach the specified tile.
	 */
	private List<ChessPiece> getPossibleKnights(ChessPlayer player,
			ChessboardCoordinates destination) {
		List<ChessboardCoordinates> possiblePositions = ImmutableList.of(
				new ChessboardCoordinates(destination.rank - 2, destination.file - 1),
				new ChessboardCoordinates(destination.rank - 2, destination.file + 1),
				new ChessboardCoordinates(destination.rank + 2, destination.file - 1),
				new ChessboardCoordinates(destination.rank + 2, destination.file + 1),
				new ChessboardCoordinates(destination.rank - 1, destination.file - 2),
				new ChessboardCoordinates(destination.rank + 1, destination.file - 2),
				new ChessboardCoordinates(destination.rank - 1, destination.file + 2),
				new ChessboardCoordinates(destination.rank + 1, destination.file + 2));
		return filterPieces(getAllPieces(possiblePositions), ChessPieceType.KNIGHT, player);
	}

	/**
	 * Returns all the pawns owned by the specified player that can reach the specified tile.
	 */
	private List<ChessPiece> getPossiblePawns(ChessPlayer player,
			ChessboardCoordinates destination, boolean isCapture) {
		// Pawns can only move forward; determine which way is forward for this player.
		int oneRankBackwards = (player == ChessPlayer.BLACK) ? 1 : -1;
		ImmutableList.Builder<ChessboardCoordinates> possiblePositions = ImmutableList.builder();
		if (isCapture) {
			// For captures, pawns move diagonally.
			possiblePositions.add(new ChessboardCoordinates(
					destination.rank + oneRankBackwards, destination.file + 1));
			possiblePositions.add(new ChessboardCoordinates(
					destination.rank + oneRankBackwards, destination.file - 1));
			return filterPieces(getAllPieces(possiblePositions.build()), ChessPieceType.PAWN, player);
		} else {
			// For non-captures, pawns move straight.
			possiblePositions.add(new ChessboardCoordinates(
					destination.rank + oneRankBackwards, destination.file));
			possiblePositions.add(new ChessboardCoordinates(
					destination.rank + 2 * oneRankBackwards, destination.file));
			List<ChessPiece> possiblePieces = Lists.newArrayList(
					filterPieces(getAllPieces(possiblePositions.build()), ChessPieceType.PAWN, player));

			// Handle the case where one pawn is right in front of another,
			// by removing the piece further back.
			if (possiblePieces.size() > 1) {
				checkState(possiblePieces.size() == 2,
						"should only be possible to have two pawns here");
				if (possiblePieces.get(0).getPosition().rank ==
						destination.rank + 2 * oneRankBackwards) {
					possiblePieces.remove(0);
				} else {
					possiblePieces.remove(1);
				}
			}

			return ImmutableList.copyOf(possiblePieces);
		}

	}

	/**
	 * Returns the closest pieces to a tile, moving straight.
	 */
	private List<ChessPiece> getClosestSraightPieces(ChessboardCoordinates position) {
		return ImmutableList.<ChessPiece>builder()
				.addAll(getClosestPieceInDirection(position, -1, 0))
				.addAll(getClosestPieceInDirection(position, 1, 0))
				.addAll(getClosestPieceInDirection(position, 0, -1))
				.addAll(getClosestPieceInDirection(position, 0, 1))
				.build();
	}

	/**
	 * Returns the closest pieces to a tile, moving diagonally.
	 */
	private List<ChessPiece> getClosestDiagonalPieces(ChessboardCoordinates position) {
		return ImmutableList.<ChessPiece>builder()
				.addAll(getClosestPieceInDirection(position, -1, -1))
				.addAll(getClosestPieceInDirection(position, 1, -1))
				.addAll(getClosestPieceInDirection(position, -1, 1))
				.addAll(getClosestPieceInDirection(position, 1, 1))
				.build();
	}

	/**
	 * Returns the closest piece in a particular direction, or an empty list if there is none.
	 *<p>
	 * The direction is specified by a rank offset and a file offset.
	 * This allows both straight and diagonal directions to be specified.
	 */
	private List<ChessPiece> getClosestPieceInDirection(ChessboardCoordinates position,
			int rankOffset, int fileOffset) {
		int currentRank = position.rank;
		int currentFile = position.file;
		while (true) {
			currentRank += rankOffset;
			currentFile += fileOffset;
			if (currentRank < 0 || currentRank > 7 || currentFile < 0 || currentFile > 7) {
				break;
			}
			ChessPiece piece = getPieceAtCoordinates(new ChessboardCoordinates(currentRank, currentFile));
			if (piece != null) {
				return ImmutableList.of(piece);
			}
		}
		return ImmutableList.of();
	}

	/**
	 * Returns all of the pieces at the specified positions.
	 */
	private List<ChessPiece> getAllPieces(List<ChessboardCoordinates> positions) {
		ImmutableList.Builder<ChessPiece> builder = ImmutableList.<ChessPiece>builder();
		for (ChessboardCoordinates position : positions) {
			ChessPiece piece = getPieceAtCoordinates(position);
			if (piece != null) {
				builder.add(piece);
			}

		}
		return builder.build();
	}

	/**
	 * Filters out all pieces not matching the specified type and player.
	 */
	private List<ChessPiece> filterPieces(List<ChessPiece> pieces, ChessPieceType type,
			ChessPlayer player) {
		ImmutableList.Builder<ChessPiece> builder = ImmutableList.<ChessPiece>builder();
		for (ChessPiece piece : pieces) {
			checkNotNull(piece, "piece");
			if (piece.getType() == type && piece.owner == player) {
				builder.add(piece);
			}
		}
		return builder.build();
	}

	/**
	 * Applies a {@link ChessMove} to this board. Specifically, it removes any captured
	 * pieces, moves the moving piece to its new tile, and promotes pawns if necessary. 
	 */
	public void acceptMove(ChessMove move) {
		if (move.isCapture) {
			ChessPiece capturedPiece = getPieceAtCoordinates(move.destination);
			if (capturedPiece == null) {
				// Handle en passant.
				checkState(move.movingPiece.getType() == ChessPieceType.PAWN,
						"only pawns can perform en passant");
				int oneRankBackwards = (move.movingPiece.owner == ChessPlayer.BLACK) ? 1 : -1;
				capturedPiece = getPieceAtCoordinates(new ChessboardCoordinates(
						move.destination.rank + oneRankBackwards, move.destination.file));
				if (capturedPiece == null) {
					throw new IllegalStateException("error while handling en passant");
				}
			}
			pieces.remove(capturedPiece);
		}

		move.movingPiece.setPosition(move.destination);

		if (move.promotion != null) {
			move.movingPiece.setType(move.promotion);
		}
	}
	
	/**
	 * Returns a deeply immutable version of this chessboard.
	 */
	public ImmutableChessboard asImmutable() {
		ImmutableSet.Builder<ImmutableChessPiece> piecesBuilder = ImmutableSet.builder();
		for (ChessPiece mutablePiece : pieces) {
			piecesBuilder.add(mutablePiece.asImmutable());
		}
		return new ImmutableChessboard(piecesBuilder.build());
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
