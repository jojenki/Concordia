package name.jenkins.paul.john.concordia;

import java.io.IOException;

import name.jenkins.paul.john.concordia.schema.Schema;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * <p>
 * Serializes a {@link Concordia} object for Jackson, which only includes the
 * internal {@link Schema}.
 * </p>
 *
 * @author jojenki
 */
public class ConcordiaSerializer extends JsonSerializer<Concordia> {
	/**
	 * Serializes only the schema field from the {@link Concordia} object.
	 */
	@Override
	public void serialize(
		final Concordia concordia,
		final JsonGenerator generator,
		final SerializerProvider provider)
		throws IOException, JsonProcessingException {
		
		// Write the schema.
		generator.writeObject(concordia.getSchema());
	}
}