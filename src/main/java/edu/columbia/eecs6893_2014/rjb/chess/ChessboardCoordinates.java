package edu.columbia.eecs6893_2014.rjb.chess;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

/**
 * Represents a position on a chessboard.
 */
public class ChessboardCoordinates {

	public static final String RANK_DIGITS = "12345678";
	public static final String FILE_LETTERS = "abcdefgh";

	public final int rank; // 0-indexed
	public final int file; // 0-indexed

	/**
	 * Construct using rank and file as 0-indexed integers.
	 */
	public ChessboardCoordinates(int rank, int file) {
		// Note we allow ranks and files outside of the board;
		// this makes various algorithms convenient.
		this.rank = rank;
		this.file = file;
	}

	/**
	 * Construct using rank as a 1-indexed integer and file as a letter.
	 */
	public ChessboardCoordinates(String rank, String file) {
		checkNotNull(rank, "rank");
		checkArgument(rank.length() == 1, "rank must be length one");
		checkArgument(RANK_DIGITS.contains(rank), "unexpected rank string: " + rank);
		checkNotNull(file, "file");
		checkArgument(file.length() == 1, "file must be length one");
		checkArgument(FILE_LETTERS.contains(file), "unexpected file string: " + file);
		this.rank = RANK_DIGITS.indexOf(rank);
		this.file = FILE_LETTERS.indexOf(file);
	}

	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (getClass() != other.getClass()) {
			return false;
		}
		final ChessboardCoordinates that = (ChessboardCoordinates) other;
		return Objects.equal(this.rank, that.rank)
				&& Objects.equal(this.file, that.file);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.rank, this.file);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("rank", rank)
				.add("file", file)
				.toString();
	}
}
