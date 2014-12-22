package edu.columbia.eecs6893_2014.rjb.classifier;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.apache.mahout.math.Vector;

import java.util.List;

public abstract class Classifier {
	protected final List<String> categories; // Also known as labels.
	protected final List<String> features;

	protected Classifier(List<String> categories, List<String> features) {
		this.categories = checkNotNull(categories, "categories");
		this.features = checkNotNull(features, "features");
	}

	/**
	 * Trains the classifier using many data points.
	 */
	public abstract void train(List<Vectorizer.Vector> vectors);

	/**
	 * Classifies a data point.
	 */
	public abstract String classify(Vectorizer.Vector vector);

	/**
	 * Prints out info about this classifier.
	 */
	public abstract void printInfo();

	/**
	 * Classifies a group of vectors, and summarizes the results.
	 * Returns the accuracy of the classification.
	 */
	public double classifyAndSummarize(List<Vectorizer.Vector> vectors) {
		int total = 0;
		int correct = 0;
		int[] totalByCategory = new int[categories.size()];
		int[] correctByCategory = new int[categories.size()];
		for (int i = 0; i < categories.size(); i++) {
			totalByCategory[i] = 0;
			correctByCategory[i] = 0;
		}

		// Classify each input vector and record the results:
		for (Vectorizer.Vector vector : vectors) {
			String prediction = classify(vector);
			checkState(categories.indexOf(prediction) >= 0, "invalid prediction");

			if (prediction.equals(vector.category)) {
				correct++;
				correctByCategory[categories.indexOf(prediction)]++;
			}

			total++;
			totalByCategory[categories.indexOf(vector.category)]++;
		}

		// Summarize results:
		System.out.println("results:");
		System.out.println(String.format("\ttotal correct:\t %d / %d (%s)",
				correct, total, asRatio(correct, total)));
		for (int i = 0; i < categories.size(); i++) {
			System.out.println(String.format("\t%s correct:\t %d / %d (%s)",
					categories.get(i), correctByCategory[i], totalByCategory[i],
					asRatio(correctByCategory[i], totalByCategory[i])));
		}
		System.out.println();

		return ((double) correct) / ((double) total);
	}

	private String asRatio(int correct, int total) {
		double percentage = ((double) correct) / ((double) total);
		return String.format("%2.2f", percentage);
	}

	protected String getMostLikelyCategory(Vector prediction) {
		int highestPrediction = 0;
		for (int i = 0; i < categories.size(); i++) {
			if (prediction.get(i) > prediction.get(highestPrediction)) {
				highestPrediction = i;
			}
		}
		return categories.get(highestPrediction);
	}

	protected static String listToString(List<String> list) {
		checkArgument(list.size() > 0, "list is empty");
		StringBuilder builder = new StringBuilder();
		builder.append(list.get(0));
		for (int i = 1; i < list.size(); i++) {
			builder.append(",");
			builder.append(list.get(i));	
		}
		return builder.toString();
	}
}
