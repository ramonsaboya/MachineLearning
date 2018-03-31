package br.ufpe.cin.if699.arff;

import java.util.List;

public class NominalAttribute extends Attribute {

	private List<String> values;

	public NominalAttribute(String name) {
		super(name, AttributeType.NOMINAL);
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

}
