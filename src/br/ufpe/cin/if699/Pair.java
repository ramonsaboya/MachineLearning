package br.ufpe.cin.if699;

public class Pair<F extends Comparable<F>, S extends Comparable<S>> implements Comparable<Pair<F, S>> {

	private F first;
	private S second;

	public Pair(F first, S second) {
		this.first = first;
		this.second = second;
	}

	public F getFirst() {
		return this.first;
	}

	public void setFirst(F first) {
		this.first = first;
	}

	public S getSecond() {
		return this.second;
	}

	public void setSecond(S second) {
		this.second = second;
	}

	@Override
	public int compareTo(Pair<F, S> other) {
		int firstComparison = first.compareTo(other.getFirst());

		if (firstComparison == 0) {
			return second.compareTo(other.getSecond());
		}

		return firstComparison;
	}

}
