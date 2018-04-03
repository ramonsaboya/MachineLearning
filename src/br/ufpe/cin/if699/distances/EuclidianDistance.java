package br.ufpe.cin.if699.distances;

import br.ufpe.cin.if699.KFold;
import br.ufpe.cin.if699.arff.Attribute;
import br.ufpe.cin.if699.arff.AttributeRange;
import br.ufpe.cin.if699.arff.AttributeType;
import br.ufpe.cin.if699.arff.Dataset;
import br.ufpe.cin.if699.arff.Instance;

public class EuclidianDistance implements KNNDistance {

	@Override
	public double calculateDistance(Dataset dataset, Instance a, Instance b) {
		double distance = 0D;

		KFold kFold = dataset.getKFold();

		for (int i = 0; i < dataset.getAttributes().size(); ++i) {
			if (i == dataset.getClassIndex()) {
				continue;
			}

			Attribute attribute = dataset.getAttributes().get(i);

			if (attribute.getType() != AttributeType.NUMERIC) {
				continue;
			}

			AttributeRange attributeRange = kFold.getCachedFoldRange().get(i);
			double range = attributeRange.getMax() - attributeRange.getMin();

			double x = (Double) a.getAttributeValue(i);
			double y = (Double) b.getAttributeValue(i);

			distance += Math.pow((x - y) / range, 2);
		}

		return distance;
	}

}
