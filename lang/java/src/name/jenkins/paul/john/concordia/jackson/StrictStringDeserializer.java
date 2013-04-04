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
 * A strict deserializer for string (text) values. If the value is a JSON text
 * value, then a Java String will be returned. If it is a JSON null value, then
 * Java's null will be returned. Otherwise, a {@link JsonMappingException} will
 * be thrown.
 * </p>
 * 
 * @author John Jenkins
 */
public class StrictStringDeserializer extends JsonDeserializer<String> {
	/**
	 * A strict deserializer for string (text) values. If the value is a JSON
	 * text value, then its Java counterpart will be returned. If it is a JSON
	 * null value, then Java's null will be returned. Otherwise, a
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
	public String deserialize(
		final JsonParser parser,
		final DeserializationContext context)
		throws IOException,
		JsonProcessingException {

		// Get the token.
		JsonToken token = parser.getCurrentToken();
		// If it is a string value, return its text.
		if(JsonToken.VALUE_STRING.equals(token)) {
			return parser.getText();
		}
		// If it is a null value, return null.
		else if(JsonToken.VALUE_NULL.equals(token)) {
			return null;
		}
		// Otherwise, it is invalid and an exception should be thrown.
		else {
			throw new JsonMappingException(
				"A string value was expected but not given.");
		}
	}
}