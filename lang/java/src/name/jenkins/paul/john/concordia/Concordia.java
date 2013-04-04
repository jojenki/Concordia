package name.jenkins.paul.john.concordia;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import name.jenkins.paul.john.concordia.exception.ConcordiaException;
import name.jenkins.paul.john.concordia.jackson.StrictBooleanDeserializer;
import name.jenkins.paul.john.concordia.jackson.StrictStringDeserializer;
import name.jenkins.paul.john.concordia.schema.ArraySchema;
import name.jenkins.paul.john.concordia.schema.ObjectSchema;
import name.jenkins.paul.john.concordia.schema.ReferenceSchema;
import name.jenkins.paul.john.concordia.schema.Schema;
import name.jenkins.paul.john.concordia.validator.DataValidator;
import name.jenkins.paul.john.concordia.validator.SchemaValidator;
import name.jenkins.paul.john.concordia.validator.ValidationController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * <p>
 * The driver class for Concordia.
 * </p>
 * 
 * <p>
 * This class is immutable.
 * </p>
 *
 * @author John Jenkins
 */
public class Concordia {
	/**
	 * The internal reader that converts schemas to {@link Schema} objects.
	 */
	private static final ObjectReader JSON_READER;
	static {
		ObjectMapper mapper = new ObjectMapper();

		mapper
			.registerModule(
				(new SimpleModule())
					.addDeserializer(
						boolean.class,
						new StrictBooleanDeserializer()));

		mapper
			.registerModule(
				(new SimpleModule())
					.addDeserializer(
						Boolean.class,
						new StrictBooleanDeserializer()));
		
		mapper
			.registerModule(
				(new SimpleModule())
					.addDeserializer(
						String.class,
						new StrictStringDeserializer()));
		
		JSON_READER = mapper.reader(Schema.class);
	}

	/**
	 * The schema for this object.
	 */
	private final Schema schema;
	/**
	 * The validation controller for building this object.
	 */
	private ValidationController controller;

	/**
	 * Creates a new Concordia object after validating it.
	 * 
	 * @param schema
	 *        The schema to validate and use to create this object.
	 *        
	 * @param controller
	 *        The validation controller which should include all custom
	 *        {@link SchemaValidator}s and {@link DataValidator}s to use.
	 * 
	 * @throws ConcordiaException
	 *         The schema is invalid.
	 *         
	 * @throws IllegalArgumentException
	 *         The schema and/or controller are null.
	 * 
	 * @throws JsonParseException
	 *         The schema was not valid JSON.
	 * 
	 * @throws IOException
	 *         The schema could not be read.
	 */
	public Concordia(
		final InputStream schema,
		final ValidationController controller)
		throws 
			ConcordiaException,
			IllegalArgumentException,
			IOException {
		
		if(schema == null) {
			throw new IllegalArgumentException("The schema is null.");
		}
		if(controller == null) {
			throw new IllegalArgumentException("The controller is null.");
		}

		// Process the JSON and create a Schema from it.
		try {
			this.schema = JSON_READER.readValue(schema);
		}
		catch(JsonMappingException e) {
			throw
				new ConcordiaException(
					"The schema was malformed or invalid.",
					e);
		}
		
		// Save the controller.
		this.controller = controller;

		// Validate the schema.
		controller.validate(this.schema);
		
		// Make sure the root is either an object or an array.
		if( (! (this.schema instanceof ObjectSchema)) &&
			(! (this.schema instanceof ArraySchema))) {
			
			throw
				new ConcordiaException(
					"The root type of this schema must either be '" +
						ObjectSchema.TYPE_ID +
						"' or '" +
						ArraySchema.TYPE_ID +
						"'.");
		}
		
		// Make sure the root is not optional.
		if(this.schema.isOptional()) {
			throw
				new ConcordiaException(
					"The root of the schema cannot be optional.");
		}
		
		// Update the controller on any child schemas.
		updateController(this.schema.getSubSchemas(), controller);
	}

	/**
	 * Copy constructor.
	 * 
	 * @param concordia The Concordia object to copy.
	 */
	public Concordia(final Concordia concordia) {
		schema = concordia.schema;
		controller = concordia.controller;
	}

	/**
	 * Returns the root {@link Schema} that defines this object.
	 * 
	 * @return The root {@link Schema}.
	 */
	public Schema getSchema() {
		return schema;
	}

	/**
	 * Validates that some data conforms to the given schema.
	 * 
	 * @param data The data to validate.
	 * 
	 * @throws ConcordiaException The data is invalid. 
	 */
	public void validateData(final JsonNode data) throws ConcordiaException {
		controller.validate(schema, data);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result =
			prime *
				result +
				((controller == null) ? 0 : controller.hashCode());
		result = prime * result + ((schema == null) ? 0 : schema.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if(obj == null) {
			return false;
		}
		if(!(obj instanceof Concordia)) {
			return false;
		}
		Concordia other = (Concordia) obj;
		if(controller == null) {
			if(other.controller != null) {
				return false;
			}
		}
		else if(!controller.equals(other.controller)) {
			return false;
		}
		if(schema == null) {
			if(other.schema != null) {
				return false;
			}
		}
		else if(!schema.equals(other.schema)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Updates the controller on this object and any other Concordia
	 * sub-objects.
	 * 
	 * @param schemas
	 *        This object's schemas, which can never be a
	 *        {@link ReferenceSchema}.
	 * 
	 * @param controller
	 *        The controller to update to.
	 */
	private void updateController(
		final List<Schema> schemas,
		final ValidationController controller) {
		
		// Set this object's controller in case this is being called
		// recursively.
		this.controller = controller;
		
		// Cycle through the schemas.
		for(Schema schema : schemas) {
			// If it's a ReferenceSchema, update its Concordia's controller.
			if(schema instanceof ReferenceSchema) {
				((ReferenceSchema) schema)
					.getConcordia()
						.updateController(schema.getSubSchemas(), controller);
			}
			// Otherwise, be sure to recurse on the child schemas.
			else {
				updateController(schema.getSubSchemas(), controller);
			}
		}
	}
}