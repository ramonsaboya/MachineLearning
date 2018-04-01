package br.ufpe.cin.if699.distances;

import br.ufpe.cin.if699.arff.Dataset;
import br.ufpe.cin.if699.arff.Instance;

public interface KNNDistance {

	public double calculateDistance(Dataset dataset, Instance a, Instance b);

}
