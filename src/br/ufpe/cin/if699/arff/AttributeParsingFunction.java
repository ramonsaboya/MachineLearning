package br.ufpe.cin.if699.arff;

@FunctionalInterface
public interface AttributeParsingFunction {

	public Object apply(Attribute attribute, String input);

}
