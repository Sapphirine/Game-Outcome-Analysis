package edu.columbia.eecs6893_2014.rjb.chess;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

/**
 * Immutable version of {@link Chessboard}.
 */
public class ImmutableChessboard extends Chessboard {

	/**
	 * No public constructor, use {@link Chessboard#asImmutable()}.
	 */
	protected ImmutableChessboard(ImmutableSet<ImmutableChessPiece> pieces) {
		super(ImmutableSet.<ChessPiece>copyOf(pieces)); // Copy required because of generics.
	}

	/**
	 * Prints this board, useful for debugging.
	 *<p>
	 * White pieces are uppercase, black pieces are lowercase.
	 */
	public void printSelf() {
		String[][] board = new String[8][8];
		for (ChessPiece piece : pieces) {
			int rank = piece.getPosition().rank;
			int file = piece.getPosition().file;
			String name = piece.getType().abbreviation;
			ChessPlayer owner = piece.owner;

			if (owner == ChessPlayer.BLACK) {
				name = name.toLowerCase();
			}
			board[rank][file] = name;
		}
		StringBuilder builder = new StringBuilder();
		for (int i = 7; i >= 0; i--) {
			builder.append("_________________\n");
			for (int j = 0; j < 8; j++) {
				builder.append("|");
				if (board[i][j] == null) {
					builder.append(" ");
				} else {
					builder.append(board[i][j]);
				}

			}
			builder.append("|");
			builder.append("\n");
		}
		builder.append("_________________");
		System.out.println(builder.toString());
	}

	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (getClass() != other.getClass()) {
			return false;
		}
		final ImmutableChessboard that = (ImmutableChessboard) other;
		return Objects.equal(this.pieces, that.pieces);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.pieces);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("pieces", pieces)
				.toString();
	}
}
