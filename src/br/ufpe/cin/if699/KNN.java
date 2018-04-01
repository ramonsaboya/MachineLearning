package br.ufpe.cin.if699;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import br.ufpe.cin.if699.arff.AttributeRange;
import br.ufpe.cin.if699.arff.Dataset;
import br.ufpe.cin.if699.arff.Instance;

public class KNN {

	private int k;
	private int folds;
	private KFold kFold;

	private Dataset dataset;

	private boolean weighted;

	private double percentage;

	private List<Instance> trainSet;
	private List<AttributeRange> currentRange;

	public KNN(Dataset dataset, KFold kFold, int k) {
		this.k = k;
		this.folds = kFold.getK();
		this.kFold = kFold;

		this.dataset = dataset;

		this.weighted = false;
	}

	public boolean isWeighted() {
		return this.weighted;
	}

	public void setWeighted(boolean weighted) {
		this.weighted = weighted;
	}

	public double getPercantage() {
		return percentage;
	}

	public double evaluate() {
		percentage = 0D;

		for (int i = 0; i < folds; ++i) {
			train(i);
			percentage += test(i);
		}

		percentage /= folds;

		return percentage;
	}

	private void train(int testFold) {
		this.trainSet = kFold.getTrainSet(testFold);

		this.currentRange = kFold.getAttributesRange(testFold);
	}

	private double test(int testFold) {
		int right, wrong;

		right = wrong = 0;

		List<Instance> instances = kFold.getTestSet(testFold);

		PriorityQueue<Pair<Double, Instance>> heap = new PriorityQueue<Pair<Double, Instance>>();
		HashMap<String, List<Pair<Double, Instance>>> classes = new HashMap<String, List<Pair<Double, Instance>>>();

		for (int i = 0; i < instances.size(); ++i) {
			heap.clear();
			classes.clear();

			Instance testInstance = instances.get(i);

			for (int j = 0; j < trainSet.size(); ++j) {
				Instance trainInstance = trainSet.get(j);

				double distance = calculateDistance(testInstance, trainInstance);

				heap.add(new Pair<Double, Instance>(distance, trainInstance));
			}

			for (int j = 0; j < k; ++j) {
				Pair<Double, Instance> test = heap.poll();

				if (test == null) {
					break;
				}

				Instance instance = test.getSecond();

				String testClass = (String) instance.getAttributeValue(dataset.getClassIndex());

				if (!classes.containsKey(testClass)) {
					classes.put(testClass, new ArrayList<Pair<Double, Instance>>());
				}

				classes.get(testClass).add(test);
			}

			PriorityQueue<Pair<Double, String>> classEval = new PriorityQueue<Pair<Double, String>>();

			for (String testClass : classes.keySet()) {
				classEval.add(new Pair<Double, String>(-weight(classes.get(testClass)), testClass));
			}

			String resultClass = classEval.poll().getSecond();

			String testClass = (String) testInstance.getAttributeValue(dataset.getClassIndex());
			if (resultClass.equals(testClass)) {
				++right;
			} else {
				++wrong;
			}
		}

		return (double) right / (right + wrong);
	}

	private double calculateDistance(Instance a, Instance b) {
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

	private double weight(List<Pair<Double, Instance>> instances) {
		if (!weighted) {
			return instances.size();
		}

		double weight = 0D;

		for (int i = 0; i < instances.size(); ++i) {
			double value = instances.get(i).getFirst();

			if (value == 0) {
				weight += Double.MAX_VALUE / 2;
			} else {
				weight += 1 / value;
			}
		}

		return weight;
	}
}
