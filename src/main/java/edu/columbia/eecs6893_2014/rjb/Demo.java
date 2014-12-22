package edu.columbia.eecs6893_2014.rjb;

import static com.google.common.base.Preconditions.checkState;
import edu.columbia.eecs6893_2014.rjb.chess.ChessGameVectorizer;
import edu.columbia.eecs6893_2014.rjb.chess.HeuristicVectorizer;
import edu.columbia.eecs6893_2014.rjb.chess.PieceCountVectorizer;
import edu.columbia.eecs6893_2014.rjb.chess.PgnParser;
import edu.columbia.eecs6893_2014.rjb.classifier.Classifier;
import edu.columbia.eecs6893_2014.rjb.classifier.LogisticRegressionClassifier;
import edu.columbia.eecs6893_2014.rjb.classifier.NaiveBayesClassifier;
import edu.columbia.eecs6893_2014.rjb.classifier.Vectorizer.Vector;

import com.google.common.collect.ImmutableList;

import java.io.File;
import java.io.IOException;

/**
 * Demonstrates classification of chess game data using a variety of methods.
 */
public class Demo {
	public static void main(String[] args) {
		File file = new File("src/data/chessdata_full.pgn");

		// Create piece count vectors:
		ChessGameVectorizer pieceCountVectorizer =
				new PieceCountVectorizer(/* turnsFromLast */ 2);
		ImmutableList<Vector> pieceCountVectors =
				filterOutTies(PgnParser.parse(file, pieceCountVectorizer));

		// Classify using standard naive Bayes, piece count vectors:
		NaiveBayesClassifier pieceCountClassifier_standardNaiveBayes =
				new NaiveBayesClassifier(
						NaiveBayesClassifier.Type.STANDARD,
						pieceCountVectorizer.categories(),
						pieceCountVectorizer.features());
		trainAndTestClassifier(pieceCountClassifier_standardNaiveBayes,
				pieceCountVectors,
				"standard naive Bayes, piece count vectors");

		// Classify using complementary naive Bayes, piece count vectors:
		NaiveBayesClassifier pieceCountClassifier_complementaryNaiveBayes =
				new NaiveBayesClassifier(
						NaiveBayesClassifier.Type.COMPLEMENTARY,
						pieceCountVectorizer.categories(),
						pieceCountVectorizer.features());
		trainAndTestClassifier(pieceCountClassifier_complementaryNaiveBayes,
				pieceCountVectors,
				"complementary naive Bayes, piece count vectors");

		// Classify using logistic regression, piece count vectors:
		LogisticRegressionClassifier pieceCountClassifier_logisticRegression =
				new LogisticRegressionClassifier(
						pieceCountVectorizer.categories(),
						pieceCountVectorizer.features());
		trainAndTestClassifier(pieceCountClassifier_logisticRegression,
				pieceCountVectors,
				"logistic regression, piece count vectors");

		// Create chess-specific heuristic vectors:
		ChessGameVectorizer heuristicVectorizer =
				new HeuristicVectorizer(/* turnsFromLast */ 2);
		ImmutableList<Vector> heuristicVectors =
				filterOutTies(PgnParser.parse(file, heuristicVectorizer));

		// Classify using standard naive Bayes, chess-specific heuristic vectors:
		NaiveBayesClassifier heuristicClassifier_standardNaiveBayes =
				new NaiveBayesClassifier(
						NaiveBayesClassifier.Type.STANDARD,
						heuristicVectorizer.categories(),
						heuristicVectorizer.features());
		trainAndTestClassifier(heuristicClassifier_standardNaiveBayes,
				heuristicVectors,
				"standard naive Bayes, chess-specific heuristic vectors");

		// Classify using complementary naive Bayes, chess-specific heuristic vectors:
		NaiveBayesClassifier heuristicClassifier_complementaryNaiveBayes =
				new NaiveBayesClassifier(
						NaiveBayesClassifier.Type.COMPLEMENTARY,
						heuristicVectorizer.categories(),
						heuristicVectorizer.features());
		trainAndTestClassifier(heuristicClassifier_complementaryNaiveBayes,
				heuristicVectors,
				"complementary naive Bayes, chess-specific heuristic vectors");

		// Classify using logistic regression, chess-specific heuristic vectors:
		LogisticRegressionClassifier heuristicClassifier_logisticRegression =
				new LogisticRegressionClassifier(
						heuristicVectorizer.categories(),
						heuristicVectorizer.features());
		trainAndTestClassifier(heuristicClassifier_logisticRegression,
				heuristicVectors,
				"logistic regression, chess-specific heuristic vectors");

		// We now take the most accurate method from above, and try it on many
		// different game turns. The most accurate method from above is:
		// complementary naive Bayes using chess-specific heuristic vectors
		int totalTurnsFromLast = 20;
		double[] accuracy = new double[totalTurnsFromLast]; // Note this is 0-indexed.
		for (int i = 2; i <= totalTurnsFromLast; i += 2) {
			ChessGameVectorizer currentVectorizer =
					new HeuristicVectorizer(/* turnsFromLast */ i);
			ImmutableList<Vector> currentVectors =
					filterOutTies(PgnParser.parse(file, currentVectorizer));
			NaiveBayesClassifier currentClassifier =
					new NaiveBayesClassifier(
							NaiveBayesClassifier.Type.COMPLEMENTARY,
							heuristicVectorizer.categories(),
							heuristicVectorizer.features());
			accuracy[i - 1] = trainAndTestClassifier(currentClassifier, currentVectors,
					"complementary naive Bayes, chess-specific heuristic vectors, "
							+ "turnsFromLast=" + i, /* wait */ false);
		}
		System.out.println("summary of results using various turnsFromLast:");
		for (int i = 2; i <= totalTurnsFromLast; i += 2) {
			System.out.println(String.format("\tturnsFromLast=%d:\t%2.2f", i, accuracy[i - 1]));
		}
		System.out.println();

		System.out.println("demo complete");
	}


	/**
	 * Uses the given input to train and test a classifier. Returns the accuracy.
	 */
	private static double trainAndTestClassifier(Classifier classifier,
			ImmutableList<Vector> data, String description) {
		return trainAndTestClassifier(classifier, data, description, /* wait */ true);
	}

	/**
	 * Uses the given input to train and test a classifier. Returns the accuracy.
	 */
	private static double trainAndTestClassifier(Classifier classifier,
			ImmutableList<Vector> data, String description, boolean wait) {
		double testDataPercentage = 0.2; // This can be parameterized.

		// Split the input data into training and testing groups:
		ImmutableList.Builder<Vector> trainingDataBuilder = ImmutableList.builder();
		ImmutableList.Builder<Vector> testDataBuilder = ImmutableList.builder();
		double index = 0.0;
		for (Vector datum : data) {
			if ((index % (1 / testDataPercentage)) < 1.0) {
				testDataBuilder.add(datum);
			} else {
				trainingDataBuilder.add(datum);
			}
			index += 1.0;
		}
		ImmutableList<Vector> trainingData = trainingDataBuilder.build();
		ImmutableList<Vector> testData = testDataBuilder.build();
		checkState((trainingData.size() + testData.size()) == data.size(),
				"missing data");
		double actualPercentage = ((double) testData.size()) / ((double) data.size());
		checkState(Math.abs(actualPercentage - testDataPercentage) < .01,
				"ratio wrong" + actualPercentage);

		// Train the classifier:
		classifier.train(trainingData);

		// Test the classifier:
		System.out.println("\nfinished classification: " + description);
		double accuracy = classifier.classifyAndSummarize(testData);

		// If wait is true, wait for user to acknowledge results:
		if (wait) {
			System.out.println("press enter to continue...");
			try {
				System.in.read();
			} catch (IOException exception) {
				throw new RuntimeException("error getting user input", exception);
			}
		}

		return accuracy;
	}

	/**
	 * Filters out {@link Vector}s categorized as "tie".
	 */
	private static ImmutableList<Vector> filterOutTies(ImmutableList<Vector> vectors) {
		boolean seenTie = false;
		ImmutableList.Builder<Vector> filtered = ImmutableList.builder();
		for (Vector vector : vectors) {
			if (!vector.category.equals("tie")) {
				filtered.add(vector);
			} else {
				seenTie = true;
			}
		}
		checkState(seenTie, "filter didn't do anything, should only be used with chess vectors");
		return filtered.build();
	}
}
