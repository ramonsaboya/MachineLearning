package br.ufpe.cin.if699.distances;

import java.util.List;
import java.util.Map;

import br.ufpe.cin.if699.KFold;
import br.ufpe.cin.if699.arff.Attribute;
import br.ufpe.cin.if699.arff.AttributeRange;
import br.ufpe.cin.if699.arff.AttributeType;
import br.ufpe.cin.if699.arff.Dataset;
import br.ufpe.cin.if699.arff.Instance;
import br.ufpe.cin.if699.arff.NominalAttribute;

public class HVDMDistance implements KNNDistance {

	private static final int Q = 2;

	@Override
	public double calculateDistance(Dataset dataset, Instance a, Instance b) {
		double distance = 0D;

		KFold kFold = dataset.getKFold();

		for (int i = 0; i < dataset.getAttributes().size(); ++i) {
			if (i == dataset.getClassIndex()) {
				continue;
			}

			Attribute attribute = dataset.getAttributes().get(i);

			if (attribute.getType() == AttributeType.NOMINAL) {
				distance += Math.pow(vdm(dataset, i, a, b), 2);
			} else {
				AttributeRange attributeRange = kFold.getCachedFoldRange().get(i);
				double range = attributeRange.getMax() - attributeRange.getMin();

				double x = (Double) a.getAttributeValue(i);
				double y = (Double) b.getAttributeValue(i);

				distance += Math.pow((x - y) / range, 2);
			}
		}

		return distance;
	}

	private static double vdm(Dataset dataset, int attributeIndex, Instance a, Instance b) {
		double distance = 0D;

		NominalAttribute nominal = (NominalAttribute) dataset.getAttributes().get(dataset.getClassIndex());

		for (int i = 0; i < nominal.getValues().size(); ++i) {
			String className = nominal.getValues().get(i);

			String valueA = (String) a.getAttributeValue(attributeIndex);
			String valueB = (String) b.getAttributeValue(attributeIndex);

			KFold kFold = dataset.getKFold();

			List<Map<String, Map<String, Integer>>> classValueCounts = kFold.getCachedFoldClassValueCount();
			Map<String, Map<String, Integer>> attributeClassValueCount = classValueCounts.get(attributeIndex);
			Map<String, Integer> classValueCount = attributeClassValueCount.get(className);
			double niac = classValueCount.getOrDefault(valueA, 0);
			double nibc = classValueCount.getOrDefault(valueB, 0);

			double nia = dataset.getKFold().getCachedFoldValueCount().get(attributeIndex).get(valueA);
			double nib = dataset.getKFold().getCachedFoldValueCount().get(attributeIndex).get(valueB);

			// ABS is not needed since Q is 2
			distance += Math.pow(niac / nia - nibc / nib, Q);
		}

		return distance;
	}

}
