package edu.columbia.eecs6893_2014.rjb.classifier;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.mahout.classifier.AbstractVectorClassifier;
import org.apache.mahout.classifier.naivebayes.ComplementaryNaiveBayesClassifier;
import org.apache.mahout.classifier.naivebayes.NaiveBayesModel;
import org.apache.mahout.classifier.naivebayes.StandardNaiveBayesClassifier;
import org.apache.mahout.classifier.naivebayes.training.TrainNaiveBayesJob;

/**
 * A naive Bayes classifier, either standard or complementary.
 */
public class NaiveBayesClassifier extends Classifier {
	private boolean trained;
	private NaiveBayesModel naiveBayesModel;
	private AbstractVectorClassifier classifier;
	private final Type type;

	public NaiveBayesClassifier(Type type, List<String> categories, List<String> features) {
		super(categories, features);
		this.trained = false;
		this.type = checkNotNull(type, "type");
	}

	public enum Type {
		STANDARD, COMPLEMENTARY;
	}

	/**
	 * Trains a naive Bayes classifier.
	 */
	@Override
	public void train(List<Vectorizer.Vector> vectors) {
		String sequenceFileName = "seqfile";
		SequenceFileWriter.write(vectors, sequenceFileName);
		train(sequenceFileName);
	}
	
	private void train(String sequenceFileName) {
		checkState(!trained, "already trained");

		String sequenceFile = "temp/" + sequenceFileName;
		String outputDirectory = "temp/output";
		String tempDirectory = "temp/temp";

		try {
			Configuration configuration = new Configuration();
			FileSystem fileSystem = FileSystem.getLocal(configuration);
			TrainNaiveBayesJob trainNaiveBayesJob = new TrainNaiveBayesJob();
			trainNaiveBayesJob.setConf(configuration);

			fileSystem.delete(new Path(outputDirectory), true);
			fileSystem.delete(new Path(tempDirectory), true);

			// Train the classifier:
			try {
				trainNaiveBayesJob.run(new String[] {
						"--input", sequenceFile,
						"--output", outputDirectory,
						"--labels", listToString(categories),
						"--overwrite",
						"--tempDir", tempDirectory });
			} catch (Exception exception) {
				throw new RuntimeException("error running training job", exception);
			}
			naiveBayesModel = NaiveBayesModel.materialize(new Path(outputDirectory),
					configuration);

			switch (type) {
				case STANDARD:
					classifier = new StandardNaiveBayesClassifier(naiveBayesModel);
					break;
				case COMPLEMENTARY:
					classifier = new ComplementaryNaiveBayesClassifier(naiveBayesModel);
					break;
				default:
					throw new RuntimeException("invalid type");
			}
		} catch (IOException exception) {
			throw new RuntimeException("error training classifier", exception);
		}

		trained = true;
	}

	/**
	 * Classifies a data point using the naive Bayes classifier.
	 */
	@Override
	public String classify(Vectorizer.Vector vector) {
		checkState(trained, "not trained");
		return getMostLikelyCategory(classifier.classifyFull(vector.vector));
	}

	@Override
	public void printInfo() {
		checkState(trained, "not trained");

		int numFeatures = (int) naiveBayesModel.numFeatures();
		int numLabels = naiveBayesModel.numLabels();

		for (int i = 0; i < numFeatures; i++) {
			System.out.println("feature " + i + ": " + naiveBayesModel.featureWeight(i));
		}
		System.out.println();

		for (int j = 0; j < numLabels; j++) {
			System.out.println("label " + j + ": " + naiveBayesModel.labelWeight(j));
		}
		System.out.println();

		for (int i = 0; i < numFeatures; i++) {
			for (int j = 0; j < numLabels; j++) {
				System.out.println("label " + j + ", feature " + i + ": " + naiveBayesModel.weight(j, i));
			}
		}
		System.out.println();

		System.out.println("features: " + numFeatures);
		System.out.println("labels: " + numLabels);
		System.out.println();
	}
}
