package br.ufpe.cin.if699.arff;

import java.text.ParseException;
import java.util.Date;

public enum AttributeType {

	DATE((attribute, input) -> {
		DateAttribute dateAttribute = (DateAttribute) attribute;

		Date date = null;
		try {
			date = dateAttribute.getDateFormat().parse(input);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return date;
	}),
	NOMINAL((attribute, input) -> input),
	NUMERIC((attribute, input) -> {
		return Double.parseDouble(input);
	}),
	STRING((attribute, input) -> input);

	private AttributeParsingFunction transform;

	private AttributeType(AttributeParsingFunction transform) {
		this.transform = transform;
	}

	/**
	 * Transforms an attribute value to its real type
	 * 
	 * @param attribute
	 * @param input
	 * @return
	 */
	public Object apply(Attribute attribute, String input) {
		return transform.apply(attribute, input);
	}

}
