package edu.columbia.eecs6893_2014.rjb.chess;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import org.junit.Test;

/**
 * Test cases for {@link PgnParser}.
 */
public class PgnParserTest {
	private final String pgnString = "\n\n\n" +
			"[Event \"It (open) \\\"Aeroflot\\\" (A2)\"]\n" +
			"[Site \"Moscow (Russia)\"]\n" +
			"[Date \"2006.02.12\"]\n" +
			"[Round \"6\"]\n" +
			"[White \"Nalbandian, Tigran\"]\n" +
			"[Black \"Tunik, Gennady\"]\n" +
			"[Result \"1-0\"]\n" +
			"[WhiteElo \"2524\"]\n" +
			"[BlackElo \"2484\"]\n" +
			"[ECO \"A05\"]\n" +
			"[PlyCount \"65\"]\n" +
			"\n" +
			"1. Nf3 Nf6 2. g3 b6 3. Bg2 Bb7 4. d3 d6 5. O-O Nbd7 6. e4 c5 7. Nc3 a6\n" +
			"8. Nh4 e6 9. f4 Qc7 10. g4 h6 11. f5 e5 12. Bf3 b5 13. Ng2 b4 14. Ne2 d5\n" +
			"15. exd5 Nxd5 16. Ng3 N7f6 17. Ne3 Nxe3 18. Bxe3 Nd5 19. Qe2 Be7\n" +
			"20. a3 bxa3 21. bxa3 c4 22. Rab1 Bc6 23. Qg2 O-O-O 24. Bb6 Nxb6\n" +
			"25. Bxc6 Rd4 26. a4 a5 27. f6 gxf6 28. Nf5 Bc5 29. Nxd4 Bxd4+ 30. Kh1 cxd3\n" +
			"31. cxd3 Qd6 32. Be4 Nxa4 33. Bf5+ 1-0\n" +
			"\n\n";

	@Test
	public void testParsing() {
		ImmutableList<ChessGame> games = PgnParser.parse(pgnString,
				new PgnParser.ChessGameConverter<ChessGame>() {
			@Override
			public ChessGame convert(ChessGame game) {
				return game;
			}
		});

		assertTrue("only one game expected", games.size() == 1);
		ChessGame game = games.get(0);

		// Check winner:
		assertEquals("winner", ChessPlayer.WHITE, game.winner);

		// Check metadata:
		ImmutableMap.Builder<String, String> expectedMetadata =
				ImmutableMap.builder();
		expectedMetadata.put("Event", "It (open) \"Aeroflot\" (A2)");
		expectedMetadata.put("Site", "Moscow (Russia)");
		expectedMetadata.put("Date", "2006.02.12");
		expectedMetadata.put("Round", "6");
		expectedMetadata.put("White", "Nalbandian, Tigran");
		expectedMetadata.put("Black", "Tunik, Gennady");
		expectedMetadata.put("Result", "1-0");
		expectedMetadata.put("WhiteElo", "2524");
		expectedMetadata.put("BlackElo", "2484");
		expectedMetadata.put("ECO", "A05");
		expectedMetadata.put("PlyCount", "65");
		assertEquals("metadata", expectedMetadata.build(), game.metadata);

		// Check the first turn:
		ImmutableChessboard firstTurnExpected = getFirstTurnExpected();
		ImmutableChessboard firstTurnActual = game.boardStates.get(0);
		assertEquals("first turn", firstTurnExpected, firstTurnActual);

		// Check a middle turn:
		ImmutableChessboard middleTurnExpected = getMiddleTurnExpected();
		ImmutableChessboard middleTurnActual = game.boardStates.get(45);
		assertEquals("last turn", middleTurnExpected, middleTurnActual);

		// Check the last turn:
		ImmutableChessboard lastTurnExpected = getLastTurnExpected();
		ImmutableChessboard lastTurnActual = game.boardStates.get(64);
		assertEquals("last turn", lastTurnExpected, lastTurnActual);
	}

	private ImmutableChessboard getFirstTurnExpected() {
		ImmutableSet.Builder<ChessPiece> pieces = ImmutableSet.builder();
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
		pieces.add(new ChessPiece(ChessPieceType.KNIGHT, ChessPlayer.WHITE, 2 , 5));
		pieces.add(new ChessPiece(ChessPieceType.ROOK, ChessPlayer.WHITE, 0 , 7));
		Chessboard board = new Chessboard(pieces.build());
		return board.asImmutable();
	}

	private ImmutableChessboard getMiddleTurnExpected() {
		ImmutableSet.Builder<ChessPiece> pieces = ImmutableSet.builder();
		pieces.add(new ChessPiece(ChessPieceType.PAWN, ChessPlayer.BLACK, 5, 0));
		pieces.add(new ChessPiece(ChessPieceType.PAWN, ChessPlayer.BLACK, 3, 2));
		pieces.add(new ChessPiece(ChessPieceType.PAWN, ChessPlayer.BLACK, 4, 4));
		pieces.add(new ChessPiece(ChessPieceType.PAWN, ChessPlayer.BLACK, 6, 5));
		pieces.add(new ChessPiece(ChessPieceType.PAWN, ChessPlayer.BLACK, 6, 6));
		pieces.add(new ChessPiece(ChessPieceType.PAWN, ChessPlayer.BLACK, 5, 7));
		pieces.add(new ChessPiece(ChessPieceType.KNIGHT, ChessPlayer.BLACK, 4, 3));
		pieces.add(new ChessPiece(ChessPieceType.BISHOP, ChessPlayer.BLACK, 5, 2));
		pieces.add(new ChessPiece(ChessPieceType.BISHOP, ChessPlayer.BLACK, 6, 4));
		pieces.add(new ChessPiece(ChessPieceType.ROOK, ChessPlayer.BLACK, 7, 3));
		pieces.add(new ChessPiece(ChessPieceType.ROOK, ChessPlayer.BLACK, 7, 7));
		pieces.add(new ChessPiece(ChessPieceType.QUEEN, ChessPlayer.BLACK, 6, 2));
		pieces.add(new ChessPiece(ChessPieceType.KING, ChessPlayer.BLACK, 7, 2));
		pieces.add(new ChessPiece(ChessPieceType.PAWN, ChessPlayer.WHITE, 2, 0));
		pieces.add(new ChessPiece(ChessPieceType.PAWN, ChessPlayer.WHITE, 1, 2));
		pieces.add(new ChessPiece(ChessPieceType.PAWN, ChessPlayer.WHITE, 2, 3));
		pieces.add(new ChessPiece(ChessPieceType.PAWN, ChessPlayer.WHITE, 4, 5));
		pieces.add(new ChessPiece(ChessPieceType.PAWN, ChessPlayer.WHITE, 3, 6));
		pieces.add(new ChessPiece(ChessPieceType.PAWN, ChessPlayer.WHITE, 1, 7));
		pieces.add(new ChessPiece(ChessPieceType.KNIGHT, ChessPlayer.WHITE, 2, 6));
		pieces.add(new ChessPiece(ChessPieceType.BISHOP, ChessPlayer.WHITE, 2, 4));
		pieces.add(new ChessPiece(ChessPieceType.BISHOP, ChessPlayer.WHITE, 2, 5));
		pieces.add(new ChessPiece(ChessPieceType.ROOK, ChessPlayer.WHITE, 0, 1));
		pieces.add(new ChessPiece(ChessPieceType.ROOK, ChessPlayer.WHITE, 0, 5));
		pieces.add(new ChessPiece(ChessPieceType.QUEEN, ChessPlayer.WHITE, 1, 6));
		pieces.add(new ChessPiece(ChessPieceType.KING, ChessPlayer.WHITE, 0 , 6));
		Chessboard board = new Chessboard(pieces.build());
		return board.asImmutable();
	}

	private ImmutableChessboard getLastTurnExpected() {
		ImmutableSet.Builder<ChessPiece> pieces = ImmutableSet.builder();
		pieces.add(new ChessPiece(ChessPieceType.PAWN, ChessPlayer.BLACK, 4, 0));
		pieces.add(new ChessPiece(ChessPieceType.PAWN, ChessPlayer.BLACK, 4, 4));
		pieces.add(new ChessPiece(ChessPieceType.PAWN, ChessPlayer.BLACK, 5, 5));
		pieces.add(new ChessPiece(ChessPieceType.PAWN, ChessPlayer.BLACK, 6, 5));
		pieces.add(new ChessPiece(ChessPieceType.PAWN, ChessPlayer.BLACK, 5, 7));
		pieces.add(new ChessPiece(ChessPieceType.KNIGHT, ChessPlayer.BLACK, 3, 0));
		pieces.add(new ChessPiece(ChessPieceType.BISHOP, ChessPlayer.BLACK, 3, 3));
		pieces.add(new ChessPiece(ChessPieceType.ROOK, ChessPlayer.BLACK, 7, 7));
		pieces.add(new ChessPiece(ChessPieceType.QUEEN, ChessPlayer.BLACK, 5, 3));
		pieces.add(new ChessPiece(ChessPieceType.KING, ChessPlayer.BLACK, 7, 2));
		pieces.add(new ChessPiece(ChessPieceType.PAWN, ChessPlayer.WHITE, 2, 3));
		pieces.add(new ChessPiece(ChessPieceType.PAWN, ChessPlayer.WHITE, 3, 6));
		pieces.add(new ChessPiece(ChessPieceType.PAWN, ChessPlayer.WHITE, 1, 7));
		pieces.add(new ChessPiece(ChessPieceType.BISHOP, ChessPlayer.WHITE, 4, 5));
		pieces.add(new ChessPiece(ChessPieceType.ROOK, ChessPlayer.WHITE, 0, 1));
		pieces.add(new ChessPiece(ChessPieceType.ROOK, ChessPlayer.WHITE, 0, 5));
		pieces.add(new ChessPiece(ChessPieceType.QUEEN, ChessPlayer.WHITE, 1, 6));
		pieces.add(new ChessPiece(ChessPieceType.KING, ChessPlayer.WHITE, 0, 7));
		Chessboard board = new Chessboard(pieces.build());
		return board.asImmutable();
	}
}
