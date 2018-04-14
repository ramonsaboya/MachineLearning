package br.ufpe.cin.if699.distances;

import java.util.List;

import br.ufpe.cin.if699.arff.AttributeRange;
import br.ufpe.cin.if699.arff.Dataset;
import br.ufpe.cin.if699.arff.Instance;

public interface Distance {

	public double calculateDistance(Dataset dataset, Instance a, Instance b, List<AttributeRange> ranges);

}
