package br.ufpe.cin.if699;

import java.util.List;

import br.ufpe.cin.if699.arff.ARFFParser;
import br.ufpe.cin.if699.arff.Dataset;
import br.ufpe.cin.if699.arff.Instance;

public class MachineLearning {

	public static void main(String[] args) {
		Dataset dataset = new ARFFParser("assets/datasets/cm1.arff").getDataset();

		dataset.setClassIndex(dataset.getAttributes().size() - 1);

		dataset.splitClassesInstances();

		int k = 3;
		KFold kFold = new KFold(k);

		for (List<Instance> instances : dataset.getSplitedInstances()) {
			kFold.distribute(instances);
		}

		for (int i = 0; i < k; ++i) {
			System.out.println("Fold: " + (i + 1));
			kFold.count(dataset, i);
		}
	}

}
