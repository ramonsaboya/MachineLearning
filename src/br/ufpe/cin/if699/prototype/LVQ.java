package br.ufpe.cin.if699.prototype;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import br.ufpe.cin.if699.KFold;
import br.ufpe.cin.if699.arff.Attribute;
import br.ufpe.cin.if699.arff.AttributeRange;
import br.ufpe.cin.if699.arff.Dataset;
import br.ufpe.cin.if699.arff.Instance;
import br.ufpe.cin.if699.arff.NominalAttribute;
import br.ufpe.cin.if699.distances.Distance;

public class LVQ {

	protected Dataset dataset;
	protected double learningRate;
	protected int epochs;

	protected Distance distance;

	protected int codebookPerClass;

	protected List<Instance> codebooks;

	public LVQ(Dataset dataset, double learningRate, int epochs, Class<? extends Distance> distanceClass, int codebookPerClass) {
		this.dataset = dataset;
		this.learningRate = learningRate;
		this.epochs = epochs;

		try {
			this.distance = distanceClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		this.codebookPerClass = codebookPerClass;
	}

	public List<Instance> generate(int fold) {
		codebooks = randomCodebooks(fold, codebookPerClass);

		KFold kFold = dataset.getKFold();
		List<Attribute> attributes = dataset.getAttributes();
		List<Instance> trainSet = kFold.getFold(fold);

		for (int epoch = 0; epoch < epochs; ++epoch) {
			double rate = learningRate * (1 - (epoch / (double) epochs));
			for (Instance instance : trainSet) {
				Instance bMU = getBestMatchingUnit(fold, instance);

				for (int i = 0; i < attributes.size(); ++i) {
					if (i == dataset.getClassIndex()) {
						continue;
					}

					double error = getError(instance, bMU, i) * rate;
					if (sameClass(instance, bMU)) {
						updateAttribute(bMU, i, error);
					} else {
						updateAttribute(bMU, i, -error);
					}
				}
			}
		}

		return codebooks;
	}

	protected List<Instance> randomCodebooks(int fold, double codebooksPerClass) {
		List<Instance> codebooks = new ArrayList<Instance>();

		Random random = new Random();

		KFold kFold = dataset.getKFold();
		List<Attribute> attributes = dataset.getAttributes();
		List<AttributeRange> attributesRange = kFold.getFoldAttributesRange(fold);
		List<Instance> trainSet = kFold.getFold(fold);
		Map<String, Integer> classCount = kFold.getFoldClassCount(fold);

		NominalAttribute classAttribute = (NominalAttribute) attributes.get(dataset.getClassIndex());
		int classAmount = classAttribute.getValues().size();

		for (int i = 0; i < classAmount; ++i) {
			String className = (String) classAttribute.getValues().get(i);

			codebooksPerClass *= (double) classCount.get(className) / trainSet.size();

			codebooksPerClass = Math.ceil(codebooksPerClass);

			for (int j = 0; j < codebooksPerClass; ++j) {
				Instance codebook = new Instance(dataset.getClassIndex() + 1);

				codebook.setAttributeValue(dataset.getClassIndex(), classAttribute.getValues().get(i));

				for (int k = 0; k < attributes.size(); ++k) {
					if (k == dataset.getClassIndex()) {
						continue;
					}

					AttributeRange range = attributesRange.get(k);

					double value = random.nextDouble() * range.getRange() + range.getMin();

					codebook.setAttributeValue(k, value);
				}

				codebooks.add(codebook);
			}
		}

		return codebooks;
	}

	protected Instance getBestMatchingUnit(int fold, Instance instance) {
		Instance closer = null;
		double minDistance = Double.MAX_VALUE;

		KFold kFold = dataset.getKFold();

		for (Instance codebook : codebooks) {
			double calculatedDistance = distance.calculateDistance(dataset, instance, codebook, kFold.getFoldAttributesRange(fold));
			if (calculatedDistance < minDistance) {
				minDistance = calculatedDistance;
				closer = codebook;
			}
		}

		return closer;
	}

	protected double getError(Instance a, Instance b, int i) {
		double aValue = (Double) a.getAttributeValue(i);
		double bValue = (Double) b.getAttributeValue(i);
		return aValue - bValue;
	}

	protected boolean sameClass(Instance a, Instance b) {
		int classIndex = dataset.getClassIndex();

		Object aValue = a.getAttributeValue(classIndex);
		Object bValue = b.getAttributeValue(classIndex);

		return aValue.equals(bValue);
	}

	protected void updateAttribute(Instance instance, int index, double error) {
		double current = (double) instance.getAttributeValue(index);

		instance.setAttributeValue(index, current + error);
	}

}
