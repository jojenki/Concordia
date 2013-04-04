package name.jenkins.paul.john.concordia.validator;

import name.jenkins.paul.john.concordia.exception.ConcordiaException;
import name.jenkins.paul.john.concordia.schema.NumberSchema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.NumericNode;

/**
 * The required validator for number schemas.
 * 
 * @author John Jenkins
 */
public final class NumberValidator
	implements
	SchemaValidator<NumberSchema>,
	DataValidator<NumberSchema> {

	@Override
	public void validate(
		final NumberSchema schema,
		final ValidationController controller) throws ConcordiaException {

		// There are no number-specific schema requirements.
	}

	/**
	 * The data must be a number. It also may be null if it is optional.
	 */
	@Override
	public void validate(
		final NumberSchema schema,
		final JsonNode data,
		final ValidationController controller) throws ConcordiaException {

		// If it is not a number,
		if(!(data instanceof NumericNode)) {
			// If it's null, then it must be optional.
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