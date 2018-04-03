package br.ufpe.cin.if699;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import br.ufpe.cin.if699.arff.Attribute;
import br.ufpe.cin.if699.arff.AttributeRange;
import br.ufpe.cin.if699.arff.AttributeType;
import br.ufpe.cin.if699.arff.Dataset;
import br.ufpe.cin.if699.arff.Instance;

public class KFold {

	private int k;

	private Dataset dataset;
	private List<Attribute> attributes;

	private List<List<Instance>> folds;

	private List<List<AttributeRange>> foldRanges;
	private List<List<Map<String, Integer>>> foldValueCount;
	private List<List<Map<String, Map<String, Integer>>>> foldClassValueCount;

	private List<AttributeRange> cachedFoldRange;
	private List<Map<String, Integer>> cachedFoldValueCount;
	private List<Map<String, Map<String, Integer>>> cachedFoldClassValueCount;

	public KFold(Dataset dataset, int k) {
		this.k = k;

		this.dataset = dataset;
		this.attributes = dataset.getAttributes();

		this.folds = new ArrayList<List<Instance>>(k);

		this.foldRanges = new ArrayList<List<AttributeRange>>(k);
		this.foldValueCount = new ArrayList<List<Map<String, Integer>>>(k);
		this.foldClassValueCount = new ArrayList<List<Map<String, Map<String, Integer>>>>(k);
	}

	public int getK() {
		return this.k;
	}

	/**
	 * Distributes instances equally between folds
	 * 
	 * @param instances
	 *          Instances from the same class
	 */
	public void distribute(List<Instance> instances) {
		int amount = instances.size();
		int perFold = amount / k;

		int index = 0;
		for (int i = 0; i < k; ++i) {
			// Initialize the fold as empty
			folds.add(new ArrayList<Instance>());

			// Populate fold
			for (int j = 0; j < perFold; ++j) {
				folds.get(i).add(instances.get(index++));
			}
		}

		// Distrubute remainder of instances
		for (int i = 0; index < instances.size(); ++i) {
			folds.get(i).add(instances.get(index++));
		}
	}

	/**
	 * @param testFold
	 *          Test fold index (0-based)
	 * @return Train set excluding test fold
	 */
	public List<Instance> getTrainSet(int testFold) {
		List<Instance> instances = new ArrayList<Instance>();

		for (int i = 0; i < k; ++i) {
			if (i == testFold) {
				continue;
			}

			instances.addAll(folds.get(i));
		}

		return instances;
	}

	/**
	 * @param testFold
	 *          Test fold index (0-based)
	 * @return Test fold only
	 */
	public List<Instance> getTestSet(int testFold) {
		return folds.get(testFold);
	}

	public List<AttributeRange> getCachedFoldRange() {
		return this.cachedFoldRange;
	}

	public List<Map<String, Integer>> getCachedFoldValueCount() {
		return this.cachedFoldValueCount;
	}

	public List<Map<String, Map<String, Integer>>> getCachedFoldClassValueCount() {
		return this.cachedFoldClassValueCount;
	}

	/**
	 * Store train set cache
	 * 
	 * @param testFold
	 *          Test fold index (0-based)
	 */
	public void buildCache(int testFold) {
		this.cachedFoldRange = getAttributesRange(testFold);
		this.cachedFoldValueCount = getValueCount(testFold);
		this.cachedFoldClassValueCount = getClassValueCount(testFold);
	}

	/**
	 * @param testFold
	 *          Test fold index (0-based)
	 * @return Merged ranges from train folds
	 */
	public List<AttributeRange> getAttributesRange(int testFold) {
		List<AttributeRange> ranges = new ArrayList<AttributeRange>();

		for (int i = 0; i < attributes.size(); ++i) {
			ranges.add(new AttributeRange());
		}

		for (int i = 0; i < k; ++i) {
			if (i == testFold) {
				continue;
			}

			for (int j = 0; j < attributes.size(); ++j) {
				Attribute attribute = attributes.get(j);

				// We only want to normalize numeric attributes
				if (attribute.getType() != AttributeType.NUMERIC) {
					continue;
				}

				AttributeRange range = this.foldRanges.get(i).get(j);

				ranges.get(j).merge(range);
			}
		}

		return ranges;
	}

	/**
	 * @param testFold
	 *          Test fold index (0-based)
	 * @return Merged value count from train folds
	 */
	public List<Map<String, Integer>> getValueCount(int testFold) {
		List<Map<String, Integer>> valuesCount = new ArrayList<Map<String, Integer>>();

		for (int i = 0; i < attributes.size(); ++i) {
			valuesCount.add(new HashMap<String, Integer>());
		}

		for (int i = 0; i < k; ++i) {
			if (i == testFold) {
				continue;
			}

			for (int j = 0; j < attributes.size(); ++j) {
				Attribute attribute = attributes.get(j);

				// We only want to normalize nominal attributes
				if (attribute.getType() != AttributeType.NOMINAL) {
					continue;
				}

				Map<String, Integer> valueCount = this.foldValueCount.get(i).get(j);

				for (Entry<String, Integer> entry : valueCount.entrySet()) {
					String key = entry.getKey();
					int value = entry.getValue();

					valuesCount.get(j).put(key, valuesCount.get(j).getOrDefault(key, 0) + value);
				}
			}
		}

		return valuesCount;
	}

	/**
	 * @param testFold
	 *          Test fold index (0-based)
	 * @return Merged class value count from train folds
	 */
	public List<Map<String, Map<String, Integer>>> getClassValueCount(int testFold) {
		List<Map<String, Map<String, Integer>>> classValuesCount = new ArrayList<Map<String, Map<String, Integer>>>();

		for (int i = 0; i < attributes.size(); ++i) {
			classValuesCount.add(new HashMap<String, Map<String, Integer>>());
		}

		for (int i = 0; i < k; ++i) {
			if (i == testFold) {
				continue;
			}

			for (int j = 0; j < attributes.size(); ++j) {
				Attribute attribute = attributes.get(j);

				// We only want to normalize nominal attributes
				if (attribute.getType() != AttributeType.NOMINAL) {
					continue;
				}

				Map<String, Map<String, Integer>> classValueCount = this.foldClassValueCount.get(i).get(j);

				for (Entry<String, Map<String, Integer>> entry : classValueCount.entrySet()) {
					String key = entry.getKey();
					Map<String, Integer> value = entry.getValue();

					if (!classValuesCount.get(j).containsKey(key)) {
						classValuesCount.get(j).put(key, new HashMap<String, Integer>());
					}

					Map<String, Integer> aux = classValuesCount.get(j).get(key);

					for (Entry<String, Integer> valueEntry : value.entrySet()) {
						String attributeValue = valueEntry.getKey();
						int attributeCount = valueEntry.getValue();

						aux.put(attributeValue, aux.getOrDefault(attributeValue, 0) + attributeCount);
					}
				}
			}
		}

		return classValuesCount;
	}

	/**
	 * Pre-compute fold attributes range
	 */
	public void preprocess() {
		for (int i = 0; i < k; ++i) {
			List<Instance> fold = folds.get(i);

			foldRanges.add(new ArrayList<AttributeRange>());
			foldValueCount.add(new ArrayList<Map<String, Integer>>());
			foldClassValueCount.add(new ArrayList<Map<String, Map<String, Integer>>>());

			List<AttributeRange> ranges = foldRanges.get(i);
			List<Map<String, Integer>> valueCounts = foldValueCount.get(i);
			List<Map<String, Map<String, Integer>>> classValueCounts = foldClassValueCount.get(i);

			for (int j = 0; j < attributes.size(); ++j) {
				ranges.add(new AttributeRange());
				valueCounts.add(new HashMap<String, Integer>());
				classValueCounts.add(new HashMap<String, Map<String, Integer>>());
			}

			for (Instance instance : fold) {
				for (int j = 0; j < attributes.size(); ++j) {
					Attribute attribute = attributes.get(j);

					AttributeType type = attribute.getType();
					if (type == AttributeType.NUMERIC) {
						AttributeRange range = ranges.get(j);

						double value = (Double) instance.getAttributeValue(j);

						range.setMax(value);
						range.setMin(value);
					} else if (type == AttributeType.NOMINAL) {
						Map<String, Integer> valueCount = valueCounts.get(j);
						Map<String, Map<String, Integer>> classValueCount = classValueCounts.get(j);

						String value = (String) instance.getAttributeValue(j);
						valueCount.put(value, valueCount.getOrDefault(value, 0) + 1);

						String className = (String) instance.getAttributeValue(dataset.getClassIndex());
						if (!classValueCount.containsKey(className)) {
							classValueCount.put(className, new HashMap<String, Integer>());
						}

						Map<String, Integer> classValue = classValueCount.get(className);
						classValue.put(value, classValue.getOrDefault(value, 0) + 1);
					}
				}
			}
		}
	}

	/**
	 * Test method to display fold distribution
	 * 
	 * @param dataset
	 * @param foldIndex
	 */
	public void count(Dataset dataset, int foldIndex) {
		List<Instance> fold = folds.get(foldIndex);

		HashMap<String, Integer> cnt = new HashMap<String, Integer>();
		for (int i = 0; i < fold.size(); ++i) {
			Instance instance = fold.get(i);

			String className = (String) instance.getAttributeValue(dataset.getClassIndex());
			cnt.put(className, cnt.getOrDefault(className, 0) + 1);
		}

		for (String className : cnt.keySet()) {
			System.out.println("    " + className + ": " + cnt.get(className));
		}
	}

}
