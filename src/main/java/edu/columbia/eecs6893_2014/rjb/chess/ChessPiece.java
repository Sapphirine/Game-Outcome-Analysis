package edu.columbia.eecs6893_2014.rjb.chess;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.base.Objects;

/**
 * Represents a piece in chess.
 */
public class ChessPiece {
	private ChessPieceType type;
	public final ChessPlayer owner;
	private ChessboardCoordinates position;

	public ChessPiece(ChessPieceType type, ChessPlayer owner, ChessboardCoordinates position) {
		this.type = checkNotNull(type, "type");
		this.owner = checkNotNull(owner, "owner");
		this.position = checkNotNull(position, "position");
	}

	/**
	 * Convenience constructor which also creates the {@link ChessboardCoordinates}.
	 */
	public ChessPiece(ChessPieceType type, ChessPlayer owner, int rank, int file) {
		this.type = checkNotNull(type, "type");
		this.owner = checkNotNull(owner, "owner");
		this.position = new ChessboardCoordinates(rank, file);
	}	

	public ChessPieceType getType() {
		return type;
	}

	public void setType(ChessPieceType type) {
		checkState(this.type == ChessPieceType.PAWN, "only pawns can be promoted");
		this.type = checkNotNull(type, "type");
	}

	public ChessboardCoordinates getPosition() {
		return position;
	}

	public void setPosition(ChessboardCoordinates position) {
		this.position = checkNotNull(position, "position");
	}

	/**
	 * Returns an immutable version of this piece.
	 */
	public ImmutableChessPiece asImmutable() {
		return new ImmutableChessPiece(type, owner, position);
	}

	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (getClass() != other.getClass()) {
			return false;
		}
		final ChessPiece that = (ChessPiece) other;
		return Objects.equal(this.type, that.type)
				&& Objects.equal(this.owner, that.owner)
				&& Objects.equal(this.position, that.position);
	}

	@Override
	public int hashCode() {
		// Note that this must be immutable to use this object in a mutable hash set.
		// See also the comment in ImmutableChessPiece's hashCode().
		return super.hashCode();
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("type", type)
				.add("owner", owner)
				.add("position", position)
				.toString();
	}
}
