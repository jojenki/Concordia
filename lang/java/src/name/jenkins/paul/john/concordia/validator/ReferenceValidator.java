package name.jenkins.paul.john.concordia.validator;

import name.jenkins.paul.john.concordia.exception.ConcordiaException;
import name.jenkins.paul.john.concordia.schema.ReferenceSchema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;

/**
 * <p>
 * The required validator for referenced schemas.
 * </p>
 *
 * @author John Jenkins
 */
public class ReferenceValidator
	implements
		SchemaValidator<ReferenceSchema>,
		DataValidator<ReferenceSchema> {

	/**
	 * Verifies that a {@link ReferenceSchema} is valid.
	 */
	@Override
	public void validate(
		final ReferenceSchema schema,
		final ValidationController controller)
		throws ConcordiaException {

		// There is no additional validation for referenced schemas.
	}

	/**
	 * Validates that some schema conforms to its referenced schema.
	 */
	@Override
	public void validate(
		final ReferenceSchema schema,
		final JsonNode data,
		final ValidationController controller)
		throws ConcordiaException {

		// First, verify that the data exists.
		if((data == null) || (data instanceof NullNode)) {
			if(schema.isOptional()) {
				return;
			}
			else {
				throw new ConcordiaException(
					"The data is missing and not optional: " +
						schema.toString());
			}
		}

		// Then, pass the validation off to the referenced schema.
		controller.validate(schema.getSchema(), data);
	}
}