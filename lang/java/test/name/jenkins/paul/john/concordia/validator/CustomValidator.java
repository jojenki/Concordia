package name.jenkins.paul.john.concordia.validator;

import name.jenkins.paul.john.concordia.exception.ConcordiaException;
import name.jenkins.paul.john.concordia.schema.ObjectSchema;
import name.jenkins.paul.john.concordia.schema.Schema;

import org.junit.Ignore;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * <p>
 * A custom validator for all types that simply throws an exception when its
 * validation code is executed.
 * </p>
 *
 * @author John Jenkins
 */
@Ignore
public class CustomValidator
	implements SchemaValidator<Schema>, DataValidator<Schema> {

	/**
	 * The name of the decorated field.
	 */
	public static final String DECORATOR_FIELD = "extra";

	/**
	 * The message that will be "thrown" if this test passes.
	 */
	public static final String PASS =
		"The '" + DECORATOR_FIELD + "' field was found.";
	
	/**
	 * The message that will be "thrown" if this test fails.
	 */
	public static final String FAIL =
		"The '" + DECORATOR_FIELD + "' field not was found.";

	/**
	 * Throws a Concordia exception with either the {@link #PASS} or
	 * {@link #FAIL} string.
	 */
	@Override
	public void validate(
		final Schema schema,
		final ValidationController controller)
		throws ConcordiaException {
		
		// If we found the decorated field, then we "throw" success.
		if(schema.getAdditionalFields().containsKey(DECORATOR_FIELD)) {
			throw new ConcordiaException(PASS);
		}
		
		// This is a special case because all of the schemas are wrapped in an
		// object schema. Therefore, when testing the outer object schema, we
		// check if it has children, and, if so, we let the processing
		// continue.
		if((schema instanceof ObjectSchema) && 
			(((ObjectSchema) schema).getFields().size() > 0)) {
			
			return;
		}
		
		// Otherwise, throw an exception.
		throw new ConcordiaException(FAIL);
	}

	/**
	 * Throws a Concordia exception with either the {@link #PASS} or
	 * {@link #FAIL} string.
	 */
	@Override
	public void validate(
		final Schema schema,
		final JsonNode data,
		final ValidationController controller)
		throws ConcordiaException {
		
		// If we found the decorated field, then we "throw" success.
		if(schema.getAdditionalFields().containsKey(DECORATOR_FIELD)) {
			throw new ConcordiaException(PASS);
		}
		
		// This is a special case because all of the schemas are wrapped in an
		// object schema. Therefore, when testing the outer object schema, we
		// check if it has children, and, if so, we let the processing
		// continue.
		if((schema instanceof ObjectSchema) && 
			(((ObjectSchema) schema).getFields().size() > 0)) {
			
			return;
		}
		
		// Otherwise, throw an exception.
		throw new ConcordiaException(FAIL);
	}
}