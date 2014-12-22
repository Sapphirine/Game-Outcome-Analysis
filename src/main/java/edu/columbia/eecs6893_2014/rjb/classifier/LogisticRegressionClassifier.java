package edu.columbia.eecs6893_2014.rjb.classifier;

import static com.google.common.base.Preconditions.checkState;

import org.apache.mahout.classifier.sgd.L2;
import org.apache.mahout.classifier.sgd.OnlineLogisticRegression;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A logistic regression classifier.
 */
public class LogisticRegressionClassifier extends Classifier {
	private boolean trained;
	private OnlineLogisticRegression logisticRegression;

	public LogisticRegressionClassifier(List<String> categories, List<String> features) {
		super(categories, features);
		trained = false;
	}

	/**
	 * Trains a logistic regression classifier.
	 */
	@Override
	public void train(List<Vectorizer.Vector> vectors) {
		checkState(!trained, "already trained");

		logisticRegression =
				new OnlineLogisticRegression(categories.size(), features.size(), new L2(1));
		// Multiple passes are required for small data sets, large data sets
		// are probably fine with one pass but this doesn't hurt.
		Random random = new Random(1); // Use hard-coded seed for consistency.
		for (int pass = 0; pass < 10; pass++) {
			Collections.shuffle(Lists.newArrayList(vectors), random);
			for (Vectorizer.Vector vector : vectors) {
				logisticRegression.train(categories.indexOf(vector.category), vector.vector);
			}
		}

		trained = true;
	}

	/**
	 * Classifies a data point using the logistic regression classifier.
	 */
	@Override
	public String classify(Vectorizer.Vector vector) {
		checkState(trained, "not trained");
		return getMostLikelyCategory(logisticRegression.classifyFull(vector.vector));
	}

	@Override
	public void printInfo() {
		checkState(trained, "not trained");

		throw new RuntimeException("not implemented");
	}
}
