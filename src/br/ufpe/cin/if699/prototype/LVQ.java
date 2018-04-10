package br.ufpe.cin.if699.prototype;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import br.ufpe.cin.if699.KFold;
import br.ufpe.cin.if699.arff.Attribute;
import br.ufpe.cin.if699.arff.AttributeRange;
import br.ufpe.cin.if699.arff.Dataset;
import br.ufpe.cin.if699.arff.Instance;
import br.ufpe.cin.if699.arff.NominalAttribute;

public class LVQ {

	private Dataset dataset;
	private double learningRate;
	private int epoch;
	
	private int codebookPerClass;
	
	private int testFold;
	
	private List<Instance> codebooks;

	public LVQ(Dataset dataset, double learningRate, int epoch, int codebookPerClass, int testFold) {
		this.dataset = dataset;
		this.learningRate = learningRate;
		this.epoch = epoch;
		
		this.codebookPerClass = codebookPerClass;
		
		this.testFold = testFold;
	}
	
	public List<Instance> generate() {
		codebooks = randomCodebooks(codebookPerClass);

		KFold kFold = dataset.getKFold();
		List<Instance> trainSet = kFold.getTrainSet(testFold);
		
		for(Instance instance : trainSet) {
//			Instance BMU = getBestMatchingUnit(instance);
		}
		
		return codebooks;
	}
	
	private List<Instance> randomCodebooks(int codebooksPerClass) {
		List<Instance> codebooks = new ArrayList<Instance>();
		
		Random random = new Random(epoch);
		
		KFold kFold = dataset.getKFold();
		List<Attribute> attributes = dataset.getAttributes();
		List<AttributeRange> attributesRange = kFold.getAttributesRange(testFold);
		
		NominalAttribute classAttribute = (NominalAttribute) attributes.get(dataset.getClassIndex());
		int classAmount = classAttribute.getValues().size();
		
		for(int i = 0; i < classAmount; ++i) {
			for(int j = 0; j < codebooksPerClass; ++j) {
				Instance codebook = new Instance();
				
				codebook.setAttributeValue(dataset.getClassIndex(), classAttribute.getValues().get(i));
				
				for(int k = 0; k < attributes.size(); ++k) {
					if(k == dataset.getClassIndex()) {
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
	
}
