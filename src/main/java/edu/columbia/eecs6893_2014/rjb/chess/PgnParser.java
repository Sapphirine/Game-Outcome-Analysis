package edu.columbia.eecs6893_2014.rjb.chess;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses chess games represented in portable game notation (PGN).
 */
public class PgnParser {

	/**
	 * Converter for turning ChessGames into other output; used
	 * with {@link #parse(File, ChessGameConverter)}.
	 *<p>
	 * This is necessary because storing every {@link ChessGame} during
	 * runtime is usually too expensive.
	 */
	public interface ChessGameConverter<A> {
		public A convert(ChessGame game);
	}

	/**
	 * Parses a PGN file into {@link ChessGame}s, and then returns
	 * a list of output generated using those parsed games.
	 */
	public static <A> ImmutableList<A> parse(File pgnFile,
			ChessGameConverter<A> converter) {
		try {
			FileReader reader = new FileReader(pgnFile);
			return parse(reader, converter);
		} catch (FileNotFoundException exception) {
			throw new RuntimeException("file not found", exception);
		}
	}

	/**
	 * Parses a PGN string into {@link ChessGame}s, and then returns
	 * a list of output generated using those parsed games.
	 */
	public static <A> ImmutableList<A> parse(String pgnString,
			ChessGameConverter<A> converter) {
		return parse(new StringReader(pgnString), converter);
	}

	/**
	 * Parses a PGN reader into {@link ChessGame}s, and then returns
	 * a list of output generated using those parsed games.
	 */
	private static <A> ImmutableList<A> parse(Reader pgnReader,
			ChessGameConverter<A> converter) {
		ImmutableList.Builder<A> convertedGames = ImmutableList.builder();
		int gamesParsed = 0;
		final int gamesPerProgressUpdate = 10000;

		// Parser state:
		boolean parsingMetadata = true;
		ChessGame.Builder currentGame = new ChessGame.Builder();
		PgnMovesParser movesParser = new PgnMovesParser(currentGame);

		// Parse each line of the PGN file:
		BufferedReader bufferedReader = new BufferedReader(pgnReader);
		try {
			for (String line; (line = bufferedReader.readLine()) != null; /* do nothing */ ) {
				if (parsingMetadata) {
					if (line.length() == 0) {
						continue;
					} else if (line.charAt(0) == '[') {
						parseMetadata(line, currentGame);
						continue;
					} else {
						parsingMetadata = false;
						movesParser = new PgnMovesParser(currentGame);
						// End of metadata; the game moves have started.
						// No continue; this falls through to game parsing code below.
					}
				}
				if (line.length() == 0) {
					throw new IllegalStateException("empty lines not expected while parsing moves");
				}
				String[] tokens = line.split(" ");
				for (int i = 0; i < tokens.length; i++) {
					if (movesParser.parseMove(tokens[i])) {
						// End of moves, the game is over.
						convertedGames.add(converter.convert(movesParser.getGame()));
						parsingMetadata = true;
						currentGame = new ChessGame.Builder();
						movesParser = new PgnMovesParser(currentGame);
						checkState(i == (tokens.length - 1), "no moves should be left");
						gamesParsed++;
						if ((gamesParsed % gamesPerProgressUpdate) == 0) {
							System.out.println("games parsed: " + gamesParsed);
						}
					}
				}
			}
		} catch (IOException exception) {
			throw new RuntimeException("error parsing", exception);
		} finally {
			try {
				bufferedReader.close();
			} catch (IOException exception) {
				throw new RuntimeException("could not close bufferedReader", exception);
			}
		}

		if ((gamesParsed % gamesPerProgressUpdate) != 0) {
			System.out.println("games parsed: " + gamesParsed);
		}
		return convertedGames.build();
	}

	private static void parseMetadata(String line, ChessGame.Builder currentGame) {
		// The key is the first token, except for a leading "[".
		String key = line.split(" ")[0].substring(1);
		// Note that the value may contain spaces and escaped quotes.
		Pattern pattern = Pattern.compile("([^\\\\]\")(.*)([^\\\\]\")");
		Matcher matcher = pattern.matcher(line);
		if (!matcher.find()) {
			throw new IllegalArgumentException("value should be wrapped in quotes");
		}
		String match = matcher.group();
		String value = match.substring(2, match.length() - 1).replace("\\\"", "\"");
		currentGame.addMetadata(key, value);
	}

	/**
	 * Takes in a sequence of move tokens for a single chess game.
	 */
	private static class PgnMovesParser {
		private final ChessGame.Builder currentGame;
		private NextTokenExpected nextTokenExpected = NextTokenExpected.MOVE_NUMBER;
		private int nextTurn = 1;
		private boolean gameOver = false;

		private PgnMovesParser(ChessGame.Builder currentGame) {
			this.currentGame = checkNotNull(currentGame, "currentGame");
		}

		/**
		 * Takes in the next move, formatted using standard algebraic notation (SAN).
		 * Returns true if the game is over.
		 */
		private boolean parseMove(String moveToken) {
			checkState(!gameOver, "game is over");
			if (isGameResult(moveToken)) {
				currentGame.setWinner(getWinner(moveToken));
				gameOver = true;
				return true;
			}

			switch (nextTokenExpected) {
				case MOVE_NUMBER:
					checkMoveNumberToken(moveToken, nextTurn);
					nextTurn++;
					break;
				case WHITE_MOVE:
				case BLACK_MOVE:
					ChessPlayer player = player(nextTokenExpected);
					currentGame.addMove(moveToken, player);
					break;
				default:
					throw new IllegalArgumentException("invalid nextTokenExpected");
			}

			nextTokenExpected = next(nextTokenExpected);
			return false;
		}

		private boolean isGameResult(String moveToken) {
			if (moveToken.equals("1-0")
					|| moveToken.equals("0-1")
					|| moveToken.equals("1/2-1/2")) {
				return true;
			}
			return false;
		}

		/**
		 * Returns the winner of the game, or null for a tie.
		 */
		@Nullable
		private ChessPlayer getWinner(String moveToken) {
			if (moveToken.equals("1-0")) {
				return ChessPlayer.WHITE;
			} else if (moveToken.equals("0-1")) {
				return ChessPlayer.BLACK;
			} else if (moveToken.equals("1/2-1/2")) {
				return null;
			} else {
				throw new IllegalArgumentException("not a game-ending move");
			}
		}

		private void checkMoveNumberToken(String moveToken, Integer nextTurn) {
			String expected = nextTurn.toString() + ".";
			if (!moveToken.equals(expected)) {
				throw new IllegalArgumentException("move number incorrect");
			}
		}

		/**
		 * Returns the game after applying all moves; should only be called once
		 * {@link #parseMove(String)} returns true.
		 */
		private ChessGame getGame() {
			checkState(gameOver, "game not over");
			return currentGame.build();
		}

		private enum NextTokenExpected {
			MOVE_NUMBER, WHITE_MOVE, BLACK_MOVE;
		}

		private NextTokenExpected next(NextTokenExpected next) {
			switch (next) {
				case MOVE_NUMBER:
					return NextTokenExpected.WHITE_MOVE;
				case WHITE_MOVE:
					return NextTokenExpected.BLACK_MOVE;
				case BLACK_MOVE:
					return NextTokenExpected.MOVE_NUMBER;
				default:
					throw new IllegalArgumentException("invalid NextTokenExpected");
			}
		}

		private ChessPlayer player(NextTokenExpected next) {
			switch (next) {
				case WHITE_MOVE:
					return ChessPlayer.WHITE;
				case BLACK_MOVE:
					return ChessPlayer.BLACK;
				default:
					throw new IllegalArgumentException("invalid NextTokenExpected");
			}
		}
	}
}
