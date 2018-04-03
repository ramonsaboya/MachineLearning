package br.ufpe.cin.if699;

import java.util.Locale;

import br.ufpe.cin.if699.arff.ARFFParser;
import br.ufpe.cin.if699.arff.Dataset;
import br.ufpe.cin.if699.distances.HVDMDistance;

public class MachineLearning {

	public static void main(String[] args) {
		Dataset dataset = new ARFFParser("assets/datasets/ca.arff").getDataset();
		dataset.setClassIndex(dataset.getAttributes().size() - 1);

		int folds = 10;
		int ks[] = new int[] { 1, 2, 3, 5, 7, 9, 11, 13, 15 };

		for (int k : ks) {
			dataset.createKFold(folds);

			// O terceiro argumento corresponde a distancia utilizada, podendo ser:
			// EuclidianDistance.class
			// VDMDistance.class
			// HVDMDistance.class
			//
			// O quarto argumento deve ser true para Ponderado ou falso para por frequencia
			KNN kNN = new KNN(dataset, k, HVDMDistance.class, true);

			System.out.printf(String.format(Locale.GERMANY, "%.2f ", 100 * kNN.evaluate()));
		}
	}

}
