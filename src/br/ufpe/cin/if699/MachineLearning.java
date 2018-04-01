package br.ufpe.cin.if699;

import br.ufpe.cin.if699.arff.ARFFParser;
import br.ufpe.cin.if699.arff.Dataset;

public class MachineLearning {

	public static void main(String[] args) {
		Dataset dataset = new ARFFParser("assets/datasets/iris.arff").getDataset();

		int ks[] = new int[] { 1, 2, 3, 5, 6, 11, 16, 21, 31 };

		for (int k : ks) {
			int folds = 10;

			dataset.setClassIndex(dataset.getAttributes().size() - 1);

			dataset.createKFold(folds);

			KNN kNN = new KNN(dataset, k);

			long init = System.currentTimeMillis();
			System.out.println(k + " = " + kNN.evaluate() + " (" + (System.currentTimeMillis() - init) + ")");
		}
	}

}
