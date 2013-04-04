package name.jenkins.paul.john.concordia.validator;

import name.jenkins.paul.john.concordia.exception.ConcordiaException;
import name.jenkins.paul.john.concordia.schema.Schema;

/**
 * The interface for all type validators.
 * 
 * @author John Jenkins
 */
public interface SchemaValidator<T extends Schema> extends Validator<T> {
	/**
	 * Validates that some schema conforms to its specific type.
	 * 
	 * @param schema
	 *        The schema to validate.
	 * 
	 * @param controller
	 *        The controller to use to validate the sub-schemas.
	 * 
	 * @throws ConcordiaException
	 *         The type is not valid.
	 */
	public abstract void validate(
		final T schema,
		final ValidationController controller) throws ConcordiaException;
}