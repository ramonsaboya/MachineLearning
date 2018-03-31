package br.ufpe.cin.if699.arff;

import java.text.DateFormat;

public class DateAttribute extends Attribute {

	private DateFormat dateFormat;

	public DateAttribute(String name) {
		super(name, AttributeType.DATE);
	}

	public DateFormat getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

}
