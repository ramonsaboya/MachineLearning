package br.ufpe.cin.if699.distances;

import br.ufpe.cin.if699.arff.AttributeRange;
import br.ufpe.cin.if699.arff.Dataset;
import br.ufpe.cin.if699.arff.Instance;

public class EuclidianDistance implements KNNDistance {

	@Override
	public double calculateDistance(Dataset dataset, Instance a, Instance b) {
		double distance = 0D;

		for (int i = 0; i < dataset.getAttributes().size(); ++i) {
			if (i == dataset.getClassIndex()) {
				continue;
			}

			AttributeRange attributeRange = currentRange.get(i);
			double range = attributeRange.getMax() - attributeRange.getMin();

			double x = (Double) a.getAttributeValue(i);
			double y = (Double) b.getAttributeValue(i);

			distance += Math.pow((x - y) / range, 2);
		}

		return distance;
	}

}
