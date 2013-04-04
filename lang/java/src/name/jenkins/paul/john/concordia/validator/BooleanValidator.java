package name.jenkins.paul.john.concordia.validator;

import name.jenkins.paul.john.concordia.exception.ConcordiaException;
import name.jenkins.paul.john.concordia.schema.BooleanSchema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.NullNode;

/**
 * <p>
 * The required validator for boolean schemas.
 * </p>
 * 
 * @author John Jenkins
 */
public final class BooleanValidator
	implements SchemaValidator<BooleanSchema>, DataValidator<BooleanSchema> {

	/**
	 * Validates that some boolean schema is valid.
	 */
	@Override
	public void validate(
		final BooleanSchema schema,
		final ValidationController controller)
		throws ConcordiaException {

		// There are no boolean-specific schema requirements.
	}

	/**
	 * The data must be a boolean. It also may be null if it is optional.
	 */
	@Override
	public void validate(
		final BooleanSchema schema,
		final JsonNode data,
		final ValidationController controller) throws ConcordiaException {

		// If it is not a boolean,
		if(!(data instanceof BooleanNode)) {
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
					"The data was not a boolean value: " + data.toString());
			}
		}
	}
}