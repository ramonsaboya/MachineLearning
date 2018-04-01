package br.ufpe.cin.if699.arff;

public class AttributeRange implements Cloneable {

	private double min;
	private double max;

	public AttributeRange() {
		min = Double.MAX_VALUE;
		max = -Double.MAX_VALUE;
	}

	public AttributeRange(AttributeRange range) {
		min = range.getMin();
		max = range.getMax();
	}

	public double getMin() {
		return this.min;
	}

	public void setMin(double min) {
		this.min = Math.min(this.min, min);
	}

	public double getMax() {
		return this.max;
	}

	public void setMax(double max) {
		this.max = Math.max(this.max, max);
	}

	public double getRange() {
		return max - min;
	}

	public void merge(AttributeRange other) {
		this.min = Math.min(this.min, other.getMin());
		this.max = Math.max(this.max, other.getMax());
	}

}
