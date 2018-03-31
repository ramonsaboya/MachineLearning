package br.ufpe.cin.if699;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.ufpe.cin.if699.arff.Dataset;
import br.ufpe.cin.if699.arff.Instance;

public class KFold {

	private int k;

	private List<List<Instance>> folds;

	public KFold(int k) {
		this.k = k;

		this.folds = new ArrayList<List<Instance>>(k);
	}

	/**
	 * Distributes instances equally between folds
	 * 
	 * @param instances
	 *          Instances from the same class
	 */
	public void distribute(List<Instance> instances) {
		int amount = instances.size();
		int perFold = amount / k;

		int index = 0;
		for (int i = 0; i < k; ++i) {
			// Initialize the fold as empty
			folds.add(new ArrayList<Instance>());

			// Populate fold
			for (int j = 0; j < perFold; ++j) {
				folds.get(i).add(instances.get(index++));
			}
		}

		// Distrubute remainder of instances
		for (int i = 0; index < instances.size(); ++i) {
			folds.get(i).add(instances.get(index++));
		}
	}

	/**
	 * Test method to display fold distribution
	 * 
	 * @param dataset
	 * @param foldIndex
	 */
	public void count(Dataset dataset, int foldIndex) {
		List<Instance> fold = folds.get(foldIndex);

		HashMap<String, Integer> cnt = new HashMap<String, Integer>();
		for (int i = 0; i < fold.size(); ++i) {
			Instance instance = fold.get(i);

			String className = (String) instance.getAttributeValue(dataset.getClassIndex());
			cnt.put(className, cnt.getOrDefault(className, 0) + 1);
		}

		for (String className : cnt.keySet()) {
			System.out.println("    " + className + ": " + cnt.get(className));
		}
	}

}
