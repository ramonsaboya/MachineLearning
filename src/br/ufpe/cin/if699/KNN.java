package br.ufpe.cin.if699;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import br.ufpe.cin.if699.arff.Dataset;
import br.ufpe.cin.if699.arff.Instance;
import br.ufpe.cin.if699.distances.KNNDistance;

public class KNN {

	private int k;
	private int folds;
	private KFold kFold;

	private Dataset dataset;

	private boolean weighted;

	private double percentage;

	private List<Instance> trainSet;

	private KNNDistance kNNDistance;

	public KNN(Dataset dataset, int k, Class<? extends KNNDistance> distanceClass, boolean weighted) {
		this.k = k;
		this.kFold = dataset.getKFold();
		this.folds = kFold.getK();

		this.dataset = dataset;

		this.weighted = weighted;

		try {
			this.kNNDistance = distanceClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
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
		trainSet = kFold.getTrainSet(testFold);

		kFold.buildCache(testFold);
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

				double distance = kNNDistance.calculateDistance(dataset, testInstance, trainInstance);

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
