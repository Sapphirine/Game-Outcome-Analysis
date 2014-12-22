package edu.columbia.eecs6893_2014.rjb.classifier;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;

/**
 * A class which turns instances of <A> into {@link Vector}s.
 */
public abstract class Vectorizer<A> {

	/**
	 * Wrapper around a Mahout vector which also includes a category.
	 */
	public static class Vector {
		public final String category;
		public final org.apache.mahout.math.Vector vector;

		public Vector(String category,
				org.apache.mahout.math.Vector vector) {
			this.category = checkNotNull(category, "category");
			this.vector = checkNotNull(vector, "vector");
		}
	}

	/**
	 * Turns the input <A> into a {@link Vector}. 
	 */
	public abstract Vector vectorize(A input);

	/**
	 * Returns the complete list of possible categories.
	 */
	public abstract ImmutableList<String> categories();

	/**
	 * Returns a list containing each feature name.
	 */
	public abstract ImmutableList<String> features();
}
