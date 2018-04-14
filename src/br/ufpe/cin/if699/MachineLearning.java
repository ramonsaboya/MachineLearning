package br.ufpe.cin.if699;

import java.util.Locale;

import br.ufpe.cin.if699.arff.ARFFParser;
import br.ufpe.cin.if699.arff.Dataset;
import br.ufpe.cin.if699.distances.EuclidianDistance;

public class MachineLearning {

	public static void main(String[] args) {
		Dataset dataset = new ARFFParser("assets/datasets/cm1.arff").getDataset();
		dataset.setClassIndex(dataset.getAttributes().size() - 1);

		int folds = 5;
		int ks[] = new int[] { 1 };
		int prototypes[] = new int[] { 5, 10, 25, 50, 75, 100 };

		double results[] = new double[ks.length];

		int turns = 40;

		for (int prototype : prototypes) {
			for (int i = 0; i < turns; ++i) {
				for (int j = 0; j < ks.length; ++j) {
					int k = ks[j];
					dataset.createKFold(folds, EuclidianDistance.class, prototype);

					// O terceiro argumento corresponde a distancia utilizada, podendo ser:
					// EuclidianDistance.class
					// VDMDistance.class
					// HVDMDistance.class
					//
					// O quarto argumento deve ser true para Ponderado ou falso para por frequencia
					KNN kNN = new KNN(dataset, k, EuclidianDistance.class, true);

					results[j] += 100 * kNN.evaluate();
				}
			}

			for (int j = 0; j < ks.length; ++j) {
				System.out.printf(String.format(Locale.GERMANY, "%.2f ", results[j] / turns));
				results[j] = 0;
			}
		}
	}

}
