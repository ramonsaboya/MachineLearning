package br.ufpe.cin.if699.prototype;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import br.ufpe.cin.if699.KFold;
import br.ufpe.cin.if699.Pair;
import br.ufpe.cin.if699.arff.Attribute;
import br.ufpe.cin.if699.arff.Dataset;
import br.ufpe.cin.if699.arff.Instance;
import br.ufpe.cin.if699.distances.Distance;

public class LVQ21 extends LVQ {

	private static final double WINDOW = 0.60;

	public LVQ21(Dataset dataset, double learningRate, int epochs, Class<? extends Distance> distanceClass, int codebookPerClass) {
		super(dataset, learningRate, epochs, distanceClass, codebookPerClass);
	}

	@Override
	public List<Instance> generate(int fold) {
		codebooks = randomCodebooks(fold, codebookPerClass);

		KFold kFold = dataset.getKFold();
		List<Attribute> attributes = dataset.getAttributes();
		List<Instance> trainSet = kFold.getFold(fold);

		for (int epoch = 0; epoch < epochs; ++epoch) {
			double rate = learningRate * (1 - (epoch / (double) epochs));
			for (Instance instance : trainSet) {
				List<Instance> bMUs = getTwoBestMatchingUnits(fold, instance);

				Instance bMU1 = bMUs.get(0);
				Instance bMU2 = bMUs.get(1);

				if (outsideWindow(fold, instance, bMU1, bMU2)) {
					continue;
				}

				if (sameClass(bMU1, bMU2)) {
					continue;
				}

				for (int i = 0; i < attributes.size(); ++i) {
					if (i == dataset.getClassIndex()) {
						continue;
					}

					double error1 = getError(instance, bMU1, i) * rate;
					double error2 = getError(instance, bMU2, i) * rate;

					if (sameClass(instance, bMU1)) {
						updateAttribute(bMU1, i, error1);
						updateAttribute(bMU2, i, -error2);
					} else if (sameClass(instance, bMU2)) {
						updateAttribute(bMU1, i, -error1);
						updateAttribute(bMU2, i, error2);
					}
				}
			}
		}

		return codebooks;
	}

	private List<Instance> getTwoBestMatchingUnits(int fold, Instance instance) {
		List<Instance> instances = new ArrayList<Instance>();

		PriorityQueue<Pair<Double, Instance>> heap = new PriorityQueue<Pair<Double, Instance>>();

		KFold kFold = dataset.getKFold();

		for (Instance codebook : codebooks) {
			Double calculatedDistance = distance.calculateDistance(dataset, instance, codebook, kFold.getFoldAttributesRange(fold));
			heap.add(new Pair<Double, Instance>(calculatedDistance, codebook));
		}

		instances.add(heap.poll().getSecond());
		instances.add(heap.poll().getSecond());

		return instances;
	}

	private boolean outsideWindow(int fold, Instance instance, Instance bMU1, Instance bMU2) {
		KFold kFold = dataset.getKFold();

		double d1 = distance.calculateDistance(dataset, instance, bMU1, kFold.getFoldAttributesRange(fold));
		double d2 = distance.calculateDistance(dataset, instance, bMU2, kFold.getFoldAttributesRange(fold));

		return Math.min(d1 / d2, d2 / d1) <= (1 - WINDOW) / (1 + WINDOW);
	}

}
