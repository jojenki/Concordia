package name.jenkins.paul.john.concordia.validator;

import name.jenkins.paul.john.concordia.exception.ConcordiaException;
import name.jenkins.paul.john.concordia.schema.Schema;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * The interface for all data validators.
 * 
 * @author John Jenkins
 */
public interface DataValidator<T extends Schema> extends Validator<T> {
	/**
	 * Validates that some data conforms to some schema.
	 * 
	 * @param schema
	 *        The Schema schema to use when validating the data.
	 * 
	 * @param data
	 *        The data to validate.
	 * 
	 * @param controller
	 *        The controller to use to validate the sub-schema data.
	 * 
	 * @throws ConcordiaException
	 *         The data does not conform to the type's schema.
	 */
	public abstract void validate(
		final T schema,
		final JsonNode data,
		final ValidationController controller) throws ConcordiaException;
}