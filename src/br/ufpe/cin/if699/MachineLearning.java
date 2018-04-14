package br.ufpe.cin.if699;

import java.util.Locale;

import br.ufpe.cin.if699.arff.ARFFParser;
import br.ufpe.cin.if699.arff.Dataset;
import br.ufpe.cin.if699.distances.EuclidianDistance;
import br.ufpe.cin.if699.prototype.LVQ1;
import br.ufpe.cin.if699.prototype.LVQ3;

public class MachineLearning {

	public static void main(String[] args) {
		Dataset dataset = new ARFFParser("assets/datasets/kc2.arff").getDataset();
		dataset.setClassIndex(dataset.getAttributes().size() - 1);

		int folds = 5;
		int ks[] = new int[] { 3 };
		int prototypes[] = new int[] { 10, 20, 30, 40, 50, 60 };

		double results[] = new double[ks.length];

		int turns = 100;

		for (int prototype : prototypes) {
			for (int i = 0; i < turns; ++i) {
				for (int j = 0; j < ks.length; ++j) {
					int k = ks[j];
					dataset.createKFold(folds, EuclidianDistance.class, prototype, LVQ1.class, LVQ3.class);

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
