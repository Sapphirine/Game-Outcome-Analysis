package edu.columbia.eecs6893_2014.rjb.chess;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import javax.annotation.Nullable;

/**
 * Represents an entire chess game: all board states, the winner, other metadata.
 */
public class ChessGame {
	public final ImmutableList<ImmutableChessboard> boardStates;
	public final ImmutableMap<String, String> metadata;
	@Nullable public final ChessPlayer winner; // Null indicates a tie.

	/**
	 * No public constructor; use the builder.
	 */
	private ChessGame(Builder builder) {
		this.boardStates = builder.boardStatesBuilder.build();
		this.metadata = builder.metadataBuilder.build();
		this.winner = builder.winner;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (getClass() != other.getClass()) {
			return false;
		}
		final ChessGame that = (ChessGame) other;
		return Objects.equal(this.boardStates, that.boardStates)
				&& Objects.equal(this.metadata, that.metadata)
				&& Objects.equal(this.winner, that.winner);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.boardStates, this.metadata, this.winner);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("boardStates", boardStates)
				.add("metadata", metadata)
				.add("winner", winner)
				.toString();
	}

	/**
	 * Builder for {@link ChessGame}.
	 */
	public static class Builder {
		private final Chessboard currentBoard;
		private final ImmutableList.Builder<ImmutableChessboard> boardStatesBuilder;
		private final ImmutableMap.Builder<String, String> metadataBuilder;
		private ChessPlayer winner;
		private ChessPlayer nextToMove;

		public Builder() {
			this.currentBoard = new Chessboard();
			this.boardStatesBuilder = ImmutableList.builder();
			this.metadataBuilder = ImmutableMap.builder();
			this.winner = null;
			this.nextToMove = ChessPlayer.WHITE; // White always goes first.
		}
		
		public ChessGame build() {
			return new ChessGame(this);
		}

		/**
		 * Parses a standard algebraic notation (SAN) chess move, and
		 * applies it to the current board.
		 *<p>
		 * Even though we know which player should move next, we take it as
		 * an argument here as an extra precaution.
		 */
		public void addMove(String sanMove, ChessPlayer player) {
			checkArgument(player.equals(nextToMove), "unexpected player");
			nextToMove = (nextToMove.equals(ChessPlayer.WHITE))
					? ChessPlayer.BLACK : ChessPlayer.WHITE;

			// We allow for a list here, since castling involves two pieces moving.
			ImmutableCollection<ChessMove> moves =
					ChessMove.parseSanMove(sanMove, player, currentBoard);
			for (ChessMove move : moves) {
				currentBoard.acceptMove(move);
			}

			boardStatesBuilder.add(currentBoard.asImmutable());
		}

		/**
		 * Adds a metadata key/value pair.
		 */
		public void addMetadata(String key, String value) {
			checkNotNull(key, "key");
			checkNotNull(value, "value");
			metadataBuilder.put(key, value);
		}

		public void setWinner(@Nullable ChessPlayer winner) {
			this.winner = winner;
		}
	}
}
