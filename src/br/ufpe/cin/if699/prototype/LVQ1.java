package br.ufpe.cin.if699.prototype;

import br.ufpe.cin.if699.arff.Dataset;
import br.ufpe.cin.if699.distances.Distance;

public class LVQ1 extends LVQ {

	public LVQ1(Dataset dataset, double learningRate, int epochs, Class<? extends Distance> distanceClass, int codebookPerClass) {
		super(dataset, learningRate, epochs, distanceClass, codebookPerClass);
	}

}
