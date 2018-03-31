package br.ufpe.cin.if699.arff;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ARFFParser {

	private static final String ATTRIBUTE = "@attribute";

	private static final String NUMERIC = "numeric";
	private static final String DATE = "date";
	private static final String STRING = "string";

	private StreamTokenizer tokenizer;

	private Dataset dataset;

	public ARFFParser(String path) {
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path))) {
			this.tokenizer = new StreamTokenizer(bufferedReader);

			initTokenizer();

			String relation = parseRelation();

			nextToken();

			List<Attribute> attributes = parseAttributes();

			List<Instance> instances = parseInstances(attributes);

			this.dataset = new Dataset(relation, attributes, instances);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Dataset getDataset() {
		return dataset;
	}

	/**
	 * Tokenizer configuration
	 * 
	 * @throws IOException
	 */
	private void initTokenizer() throws IOException {
		tokenizer.resetSyntax();
		tokenizer.whitespaceChars(0, ' ');
		tokenizer.wordChars(' ' + 1, '\u00FF');
		tokenizer.whitespaceChars(',', ',');
		tokenizer.commentChar('%');
		tokenizer.quoteChar('\"');
		tokenizer.quoteChar('\'');
		tokenizer.ordinaryChar('{');
		tokenizer.ordinaryChar('}');
		tokenizer.eolIsSignificant(true);

		nextToken();
	}

	/**
	 * Shifts cursor to the next usable token
	 * 
	 * @throws IOException
	 */
	private void nextToken() throws IOException {
		while (tokenizer.nextToken() == StreamTokenizer.TT_EOL) {
			// Do nothing
		}
	}

	/**
	 * @return Dataset relation
	 * @throws IOException
	 */
	private String parseRelation() throws IOException {
		nextToken();

		return tokenizer.sval;
	}

	/**
	 * @return Attributes list
	 * @throws IOException
	 */
	private List<Attribute> parseAttributes() throws IOException {
		List<Attribute> attributes = new ArrayList<Attribute>();

		Attribute attribute = null;

		while (tokenizer.sval.equalsIgnoreCase(ATTRIBUTE)) {
			nextToken();
			String attributeName = tokenizer.sval;
			nextToken();

			if (tokenizer.ttype == StreamTokenizer.TT_WORD) { // Numeric, String and Date attributes
				switch (tokenizer.sval.toLowerCase()) {
				case NUMERIC:
					attribute = new Attribute(attributeName, AttributeType.NUMERIC);
					break;
				case STRING:
					attribute = new Attribute(attributeName, AttributeType.STRING);
					break;
				case DATE:
					DateAttribute dateAttribute = new DateAttribute(attributeName);

					// A Date attribute may be followed by a date format
					if (!tokenizer.sval.equalsIgnoreCase(ATTRIBUTE)) {
						dateAttribute.setDateFormat(new SimpleDateFormat(tokenizer.sval));
						nextToken();
					}

					attribute = dateAttribute;
					break;
				}
			} else { // Nominal attribute
				NominalAttribute nominalAttribute = new NominalAttribute(attributeName);

				// Parses possible attributes values
				List<String> values = new ArrayList<String>();
				while (tokenizer.nextToken() != '}') {
					values.add(tokenizer.sval);
				}

				nominalAttribute.setValues(values);

				attribute = nominalAttribute;
			}

			attributes.add(attribute);

			nextToken();
		}

		return attributes;
	}

	/**
	 * @param attributes
	 *          Attribute definitions
	 * @return Instances
	 * @throws IOException
	 */
	private List<Instance> parseInstances(List<Attribute> attributes) throws IOException {
		List<Instance> instances = new ArrayList<Instance>();

		nextToken();

		Instance instance = null;
		while (tokenizer.ttype != StreamTokenizer.TT_EOF) {
			instance = new Instance();

			for (Attribute attribute : attributes) {
				instance.insertAttributeValue(attribute.apply(tokenizer.sval));

				nextToken();
			}

			instances.add(instance);
		}

		return instances;
	}

}
