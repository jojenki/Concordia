package name.jenkins.paul.john.concordia.jackson;

import java.io.IOException;

import name.jenkins.paul.john.concordia.Concordia;
import name.jenkins.paul.john.concordia.exception.ConcordiaException;
import name.jenkins.paul.john.concordia.validator.ValidationController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * <p>
 * Deserializes a Concordia definition into a Concordia object.
 * </p>
 * 
 * <p>
 * To add a custom ValidationController, decorate the ObjectMapper:
 * 
 * <pre>
 * <code>
 *  // Create the object mapper.
 *  ObjectMapper mapper = new ObjectMapper();
 * 	
 *  // Add your custom validation controller as an injectable value in the
 *  // ObjectMapper.
 *  InjectableValues.Std injectableValues = new InjectableValues.Std();
 *  	injectableValues
 *  		.addValue(
 *  			ConcordiaDeserializer.JACKSON_INJECTABLE_VALIDATION_CONTROLLER,
 *  			customValidationController);
 *  mapper.setInjectableValues(injectableValues);
 * </code>
 * </pre>
 * </p>
 *
 * @author John Jenkins
 */
public class ConcordiaDeserializer extends JsonDeserializer<Concordia> {
	/*
	 * (non-Javadoc)
	 * @see com.fasterxml.jackson.databind.JsonDeserializer#deserialize(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext)
	 */
	@Override
	public Concordia deserialize(
		final JsonParser parser,
		final DeserializationContext context)
		throws IOException, JsonProcessingException {
		
		// Check if a validation controller was injected.
		ValidationController controller = null;
		try {
			Object validationControllerObject =
				context
					.findInjectableValue(
						Concordia.JACKSON_INJECTABLE_VALIDATION_CONTROLLER,
						null,
						null);

			
			if(validationControllerObject instanceof ValidationController) {
				controller = (ValidationController) validationControllerObject;
			}
			else {
				throw
					new JsonParseException(
						"The object refrenced by the validation controller " +
							"key is not a validation controller.",
						parser.getCurrentLocation());
			}
		}
		catch(IllegalStateException e) {
			// There was no injected validation controller.
		}
		
		// Use parser to build the Concordia object.
		try {
			return new Concordia(parser, controller);
		}
		catch(ConcordiaException e) {
			throw
				new JsonParseException(
					"The definition is invalid.",
					parser.getCurrentLocation(),
					e);
		}
	}
}