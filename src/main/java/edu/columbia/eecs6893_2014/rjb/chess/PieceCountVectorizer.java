package edu.columbia.eecs6893_2014.rjb.chess;

import org.apache.mahout.math.RandomAccessSparseVector;

import com.google.common.collect.ImmutableList;

import edu.columbia.eecs6893_2014.rjb.classifier.Vectorizer.Vector;

/**
 * A class which turns instances of {@link ChessGame} into {@link Vector}s,
 * with the features being the piece counts for both players. See {@link #features()}.
 */
public class PieceCountVectorizer extends ChessGameVectorizer {
	private final int turnsFromLast;

	/**
	 * Constructs a {@link PieceCountVectorizer} that will use the specified game turn.
	 */
	public PieceCountVectorizer(int turnsFromLast) {
		this.turnsFromLast = turnsFromLast;
	}

	/**
	 * Returns a vector containing the piece counts for both players.
	 */
	@Override
	protected RandomAccessSparseVector getVector(ChessGame game) {
		ImmutableChessboard board = getBoard(game, turnsFromLast);

		RandomAccessSparseVector vector = new RandomAccessSparseVector(12, 12);
		int[] white = {0,0,0,0,0,0}; // {K / Q / R / B / N / P}
		int[] black = {0,0,0,0,0,0}; // {K / Q / R / B / N / P}

		for (ChessPiece piece : board.pieces) {
			int[] toInc = (piece.owner == ChessPlayer.BLACK) ? black : white;
			switch (piece.getType()) {
				case KING:
					toInc[0]++;
					break;
				case QUEEN:
					toInc[1]++;
					break;
				case ROOK:
					toInc[2]++;
					break;
				case BISHOP:
					toInc[3]++;
					break;
				case KNIGHT:
					toInc[4]++;
					break;
				case PAWN:
					toInc[5]++;
					break;
				default:
					throw new RuntimeException("unexpected type");
			}
		}

		vector.set(0, white[0]);
		vector.set(1, white[1]);
		vector.set(2, white[2]);
		vector.set(3, white[3]);
		vector.set(4, white[4]);
		vector.set(5, white[5]);
		vector.set(6, black[0]);
		vector.set(7, black[1]);
		vector.set(8, black[2]);
		vector.set(9, black[3]);
		vector.set(10, black[4]);
		vector.set(11, black[5]);

		return vector;
	}

	@Override
	public ImmutableList<String> features() {
		ImmutableList.Builder<String> features = ImmutableList.builder();
		features.add("whiteKings");
		features.add("whiteQueens");
		features.add("whiteRooks");
		features.add("whiteBishops");
		features.add("whiteKnights");
		features.add("whitePawns");
		features.add("blackKings");
		features.add("blackQueens");
		features.add("blackRooks");
		features.add("blackBishops");
		features.add("blackKnights");
		features.add("blackPawns");
		return features.build();
	}
}
