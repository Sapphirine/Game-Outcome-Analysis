package edu.columbia.eecs6893_2014.rjb.chess;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents a type of piece in chess.
 */
public enum ChessPieceType {
	KING("K"), QUEEN("Q"), ROOK("R"), BISHOP("B"), KNIGHT("N"), PAWN("P");

	public final String abbreviation;

	private ChessPieceType(String abbreviation) {
		this.abbreviation = checkNotNull(abbreviation, "abbreviation");
	}
}
