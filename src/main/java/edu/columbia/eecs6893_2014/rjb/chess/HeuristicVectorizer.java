package edu.columbia.eecs6893_2014.rjb.chess;

import org.apache.mahout.math.RandomAccessSparseVector;

import com.google.common.collect.ImmutableList;

import edu.columbia.eecs6893_2014.rjb.classifier.Vectorizer.Vector;

/**
 * A class which turns instances of {@link ChessGame} into {@link Vector}s,
 * with the features being various chess-specific heuristics. See {@link #features()}.
 */
public class HeuristicVectorizer extends ChessGameVectorizer {
	private final int turnsFromLast;

	/**
	 * Constructs a {@link HeuristicVectorizer} that will use the specified game turn.
	 */
	public HeuristicVectorizer(int turnsFromLast) {
		this.turnsFromLast = turnsFromLast;
	}

	/**
	 * Returns a vector containing various chess-specific heuristics.
	 */
	@Override
	protected RandomAccessSparseVector getVector(ChessGame game) {
		ImmutableChessboard board = getBoard(game, turnsFromLast);

		RandomAccessSparseVector vector = new RandomAccessSparseVector(6, 6);

		// Calculate total piece values for each player:
		int[] pieceValues = {0,0}; // {white, black}
		for (ChessPiece piece : board.pieces) {
			int index = (piece.owner == ChessPlayer.WHITE) ? 0 : 1;
			switch (piece.getType()) {
				case KING:
					// If this were a minimax algorithm, we'd want the king to
					// have an infinite value, but in this case we're using its
					// "combat value" since we only look at the current state.
					pieceValues[index] += 4;
					break;
				case QUEEN:
					pieceValues[index] += 9;
					break;
				case ROOK:
					pieceValues[index] += 5;
					break;
				case BISHOP:
					pieceValues[index] += 3;
					break;
				case KNIGHT:
					pieceValues[index] += 3;
					break;
				case PAWN:
					pieceValues[index] += 1;
					break;
				default:
					throw new RuntimeException("unexpected type");
			}
		}

		// Calculate total threatened squares for each player:
		int whiteThreatenedSquares = 0;
		int blackThreatenedSquares = 0;
		for (int rank = 0; rank < 8; rank++) {
			for (int file = 0; file < 8; file++) {
				ChessboardCoordinates square = new ChessboardCoordinates(rank, file); 
				if (isLocationThreatenedOrOwnedBy(board, square, ChessPlayer.WHITE)) {
					whiteThreatenedSquares += 1;
				}
				if (isLocationThreatenedOrOwnedBy(board, square, ChessPlayer.BLACK)) {
					blackThreatenedSquares += 1;
				}
			}
		}

		vector.set(0, pieceValues[0]);
		vector.set(1, pieceValues[1]);
		vector.set(2, board.isKingInCheck(ChessPlayer.WHITE) ? 1 : 0);
		vector.set(3, board.isKingInCheck(ChessPlayer.BLACK) ? 1 : 0);
		vector.set(4, whiteThreatenedSquares);
		vector.set(5, blackThreatenedSquares);

		return vector;
	}

	private boolean isLocationThreatenedOrOwnedBy(Chessboard board,
			ChessboardCoordinates location, ChessPlayer player) {
		ChessPiece piece = board.getPieceAtCoordinates(location);
		if (piece != null && piece.owner.equals(player)) {
			return true;
		}
		return board.isLocationThreatenedBy(location, player);
	}

	@Override
	public ImmutableList<String> features() {
		ImmutableList.Builder<String> features = ImmutableList.builder();
		features.add("whitePieceValue");
		features.add("blackPieceValue");
		features.add("whiteInCheck");
		features.add("blackInCheck");
		features.add("whiteThreatenedSquares");
		features.add("blackThreatenedSquares");
		return features.build();
	}
}
