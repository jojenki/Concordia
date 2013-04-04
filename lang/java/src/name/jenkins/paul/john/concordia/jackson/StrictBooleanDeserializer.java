package name.jenkins.paul.john.concordia.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * <p>
 * A strict deserializer for boolean values. If the value is a JSON boolean
 * value, then its Java counterpart will be returned. If it is a JSON null
 * value, then Java's null will be returned. Otherwise, a
 * {@link JsonMappingException} will be thrown.
 * </p>
 * 
 * @author John Jenkins
 */
public class StrictBooleanDeserializer extends JsonDeserializer<Boolean> {
	/**
	 * A strict deserializer for boolean values. If the value is a JSON boolean
	 * value, then its Java counterpart will be returned. If it is a JSON null
	 * value, then Java's null will be returned. Otherwise, a
	 * {@link JsonMappingException} will be thrown.
	 * 
	 * @param parser
	 *        The parser at the location of the object that is currently
	 *        attempting to be converted into a boolean.
	 * 
	 * @param context
	 *        The context of the parser.
	 * 
	 * @return The Java boolean value that represents this value or null if it
	 *         is JSON null.
	 * 
	 * @throws IOException
	 *         The parser could not read the value.
	 * 
	 * @throws JsonProcessingException
	 *         The value was not syntactically valid JSON.
	 * 
	 * @throws JsonMappingException
	 *         The value was not a JSON boolean or JSON null.
	 */
	@Override
	public Boolean deserialize(
		final JsonParser parser,
		final DeserializationContext context)
		throws IOException,
		JsonProcessingException {

		// Get the token.
		JsonToken token = parser.getCurrentToken();
		// If this is a JSON boolean value, then return Java's representation
		// of it.
		if(JsonToken.VALUE_TRUE.equals(token) ||
			JsonToken.VALUE_FALSE.equals(token)) {

			return parser.getBooleanValue();
		}
		// If this is a JSON null value, then return Java's null.
		else if(JsonToken.VALUE_NULL.equals(parser.getCurrentToken())) {
			return null;
		}
		// Otherwise, do not attempt to coerce the value. Throw an exception
		// indicating that it was not valid.
		else {
			throw new JsonMappingException(
				"A boolean value was expected but not given.");
		}
	}
}