package edu.columbia.eecs6893_2014.rjb.chess;

import com.google.common.base.Objects;

/**
 * Immutable version of {@link ChessPiece}.
 */
public class ImmutableChessPiece extends ChessPiece {

	/**
	 * No public constructor, use {@link ChessPiece#asImmutable()}.
	 */
	ImmutableChessPiece(ChessPieceType type, ChessPlayer owner,
			ChessboardCoordinates position) {
		super(type, owner, position);
	}

	@Override
	public void setType(ChessPieceType type) {
		throw new IllegalArgumentException("cannot mutate type");
	}

	@Override
	public void setPosition(ChessboardCoordinates position) {
		throw new IllegalArgumentException("cannot mutate position");
	}

	@Override
	public int hashCode() {
		// This is a bit of a hack, ChessPiece uses Object's hashCode()
		// because it has to be immutable to use ChessPiece in a mutable HashSet.
		// (A ChessPiece's type and position may change during the game.)
		// However, ImmutableSet's equals() method fast-fails if the
		// hash codes don't match, so the hash codes of two ImmutableChessPieces
		// should match if they're equal.
		return Objects.hashCode(getType(), this.owner, getPosition());
	}
}
