package br.ufpe.cin.if699.arff;

import java.util.ArrayList;
import java.util.List;

public class Instance {

	private List<Object> attributeValues;

	// Memorizes the hash as object is immutable
	private int hashCode;

	public Instance() {
		this.attributeValues = new ArrayList<Object>();

		this.hashCode = -1;
	}

	public Object getAttributeValue(int index) {
		return attributeValues.get(index);
	}

	public void insertAttributeValue(Object object) {
		attributeValues.add(object);
	}

	@Override
	public int hashCode() {
		if (hashCode != -1) {
			return hashCode;
		}

		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.attributeValues == null) ? 0 : this.attributeValues.hashCode());
		return hashCode = result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Instance)) {
			return false;
		}
		Instance other = (Instance) obj;
		if (this.attributeValues == null) {
			if (other.attributeValues != null) {
				return false;
			}
		} else if (!this.attributeValues.equals(other.attributeValues)) {
			return false;
		}
		return true;
	}

}
