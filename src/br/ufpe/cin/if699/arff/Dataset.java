package br.ufpe.cin.if699.arff;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import br.ufpe.cin.if699.KFold;

public class Dataset {

	private String relation;
	private List<Attribute> attributes;
	private List<Instance> instances;

	private int classIndex;

	private Map<String, List<Instance>> classInstances;

	private KFold kFold;

	public Dataset(String relation, List<Attribute> attributes, List<Instance> instances) {
		this.relation = relation;
		this.attributes = attributes;
		this.instances = instances;
	}

	public void createKFold(int k) {
		splitClassesInstances(k);

		kFold = new KFold(this, k);

		for (List<Instance> instances : classInstances.values()) {
			kFold.distribute(instances);
		}

		kFold.preprocess();
	}

	public String getRelation() {
		return this.relation;
	}

	public List<Attribute> getAttributes() {
		return this.attributes;
	}

	public List<Instance> getInstances() {
		return this.instances;
	}

	public int getClassIndex() {
		return this.classIndex;
	}

	public KFold getKFold() {
		return this.kFold;
	}

	public void setClassIndex(int classIndex) {
		this.classIndex = classIndex;
	}

	/**
	 * Preprocessing technique to split every instance to it's class
	 */
	private void splitClassesInstances(int k) {
		this.classInstances = new HashMap<String, List<Instance>>();

		for (Instance instance : instances) {
			Object value = instance.getAttributeValue(classIndex);
			String instanceClass = String.valueOf(value);

			if (!classInstances.containsKey(instanceClass)) {
				classInstances.put(instanceClass, new ArrayList<Instance>());
			}

			classInstances.get(String.valueOf(value)).add(instance);
		}

		// Shuffle each list to ensure better distribution
		Random random = new Random(k); // Use K as random seed to always get same result
		for (List<Instance> list : classInstances.values()) {
			Collections.shuffle(list, random);
		}
	}

}
