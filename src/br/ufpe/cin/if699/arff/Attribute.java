package br.ufpe.cin.if699.arff;

public class Attribute {

	private String name;

	private AttributeType type;

	public Attribute(String name, AttributeType type) {
		this.name = name;

		this.type = type;
	}

	public String getName() {
		return this.name;
	}

	public AttributeType getType() {
		return this.type;
	}

	public Object apply(String input) {
		return type.apply(this, input);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
		result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Attribute)) {
			return false;
		}
		Attribute other = (Attribute) obj;
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
			return false;
		}
		if (this.type != other.type) {
			return false;
		}
		return true;
	}

}
