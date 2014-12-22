package edu.columbia.eecs6893_2014.rjb.chess;

import org.apache.mahout.math.RandomAccessSparseVector;

import com.google.common.collect.ImmutableList;

import edu.columbia.eecs6893_2014.rjb.classifier.Vectorizer;
import edu.columbia.eecs6893_2014.rjb.classifier.Vectorizer.Vector;

/**
 * A class which turns instances of {@link ChessGame} into {@link Vector}s.
 */
public abstract class ChessGameVectorizer extends Vectorizer<ChessGame>
		implements PgnParser.ChessGameConverter<Vector> {

	private final ImmutableList<String> categories =
			ImmutableList.of("white", "black", "tie");

	@Override
	public Vector convert(ChessGame game) {
		return vectorize(game);
	}

	@Override
	public Vector vectorize(ChessGame game) {
		return new Vector(getCategory(game), getVector(game));
	}

	private String getCategory(ChessGame game) {
		return game.winner != null
				? game.winner.toString().toLowerCase() : "tie";
	}

	@Override
	public ImmutableList<String> categories() {
		return categories;
	}

	/**
	 * Turns a game into a vector. The features of the vector should
	 * match those returned by {@link #features()}.
	 */
	protected abstract RandomAccessSparseVector getVector(ChessGame game);

	protected static ImmutableChessboard getBoard(ChessGame game, int turnsFromLast) {
		int calculatedTurn = game.boardStates.size() - turnsFromLast;
		int actualTurn = Math.max(calculatedTurn, 1);
		return game.boardStates.get(actualTurn - 1);
	}
}
