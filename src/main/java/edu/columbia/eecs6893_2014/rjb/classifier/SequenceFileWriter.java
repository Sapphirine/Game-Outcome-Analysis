package edu.columbia.eecs6893_2014.rjb.classifier;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.mahout.math.VectorWritable;

import java.io.IOException;
import java.util.List;

/**
 * Writes vectors to a sequence file located in {@code temp/}.
 *<p>
 * Note that this overwrites existing files.
 */
public class SequenceFileWriter {
	public static void write(List<Vectorizer.Vector> vectors, String sequenceFileName) {
		try {
			Configuration configuration = new Configuration();
			FileSystem fileSystem = FileSystem.getLocal(configuration);
			Path path = new Path("temp/" + sequenceFileName);

			fileSystem.delete(path, false);

			SequenceFile.Writer writer = SequenceFile.createWriter(fileSystem,
					configuration,
					path,
					Text.class,
					VectorWritable.class);

			try {
				for (Vectorizer.Vector vector : vectors) {
					VectorWritable vectorWritable = new VectorWritable();
					vectorWritable.set(vector.vector);
					writer.append(new Text("/" + vector.category + "/"), vectorWritable);
				}
			} finally {
				writer.close();
			}
		} catch (IOException exception) {
			throw new RuntimeException("error writing sequence file", exception);
		}
	}
}
