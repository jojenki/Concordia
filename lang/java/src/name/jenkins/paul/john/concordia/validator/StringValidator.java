package name.jenkins.paul.john.concordia.validator;

import name.jenkins.paul.john.concordia.exception.ConcordiaException;
import name.jenkins.paul.john.concordia.schema.StringSchema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;

/**
 * The required validator for string schemas.
 * 
 * @author John Jenkins
 */
public class StringValidator
	implements
	SchemaValidator<StringSchema>,
	DataValidator<StringSchema> {

	/**
	 * Validates that some string schema is valid.
	 */
	@Override
	public void validate(
		final StringSchema schema,
		final ValidationController controller) throws ConcordiaException {

		// There are no string-specific schema requirements.
	}

	/**
	 * The data must be a number. It also may be null if it is optional.
	 */
	@Override
	public void validate(
		final StringSchema schema,
		final JsonNode data,
		final ValidationController controller) throws ConcordiaException {

		// If it is not text,
		if(!(data instanceof TextNode)) {
			// If it is null, then it must be optional.
			if((data == null) || (data instanceof NullNode)) {
				if(schema.isOptional()) {
					return;
				}
				else {
					throw new ConcordiaException(
						"The value is null but not optional: " +
							schema.toString());
				}
			}
			// Otherwise, it is invalid.
			else {
				throw new ConcordiaException(
					"The data was not a number value: " + data.toString());
			}
		}
	}
}