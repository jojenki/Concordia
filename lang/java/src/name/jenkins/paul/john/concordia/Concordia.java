package name.jenkins.paul.john.concordia;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import name.jenkins.paul.john.concordia.exception.ConcordiaException;
import name.jenkins.paul.john.concordia.jackson.ConcordiaDeserializer;
import name.jenkins.paul.john.concordia.jackson.ConcordiaSerializer;
import name.jenkins.paul.john.concordia.jackson.StrictBooleanDeserializer;
import name.jenkins.paul.john.concordia.jackson.StrictStringDeserializer;
import name.jenkins.paul.john.concordia.schema.ArraySchema;
import name.jenkins.paul.john.concordia.schema.ObjectSchema;
import name.jenkins.paul.john.concordia.schema.ReferenceSchema;
import name.jenkins.paul.john.concordia.schema.Schema;
import name.jenkins.paul.john.concordia.validator.ValidationController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
@JsonSerialize(using = ConcordiaSerializer.class, as = ObjectNode.class)
@JsonDeserialize(using = ConcordiaDeserializer.class)
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
	 * The key to use when injecting a {@link ValidationController} into an
	 * {@link ObjectMapper}.
	 */
	public static final String JACKSON_INJECTABLE_VALIDATION_CONTROLLER =
		"_concordia_injectable_validation_controller_";

	/**
	 * The schema for this object.
	 */
	private final Schema schema;
	/**
	 * The validation controller for building this object.
	 */
	private ValidationController controller;
	
	/**
	 * Creates a new Concordia object and validates it. It will use the default
	 * validator of {@link ValidationController#BASIC_CONTROLLER}.
	 * 
	 * @param schema
	 *        The schema to validate and use to create this object.
	 * 
	 * @throws IllegalArgumentException
	 *         The schema is null.
	 * 
	 * @throws IOException
	 *         The schema could not be read.
	 * 
	 * @throws JsonParseException
	 *         The schema was not valid JSON.
	 * 
	 * @throws ConcordiaException
	 *         The schema is invalid.
	 */
	public Concordia(
		final String schema)
		throws 
			IllegalArgumentException,
			IOException,
			JsonParseException,
			ConcordiaException {
		
		this(new ByteArrayInputStream(schema.getBytes()), null);
	}
	
	/**
	 * Creates a new Concordia object and validates it.
	 * 
	 * @param schema
	 *        The schema to validate and use to create this object.
	 *        
	 * @param controller
	 *        A custom validation controller or null, in which case the default
	 *        controller will be used,
	 *        {@link ValidationController#BASIC_CONTROLLER}.
	 *         
	 * @throws IllegalArgumentException
	 *         The schema is null.
	 * 
	 * @throws IOException
	 *         The schema could not be read.
	 * 
	 * @throws JsonParseException
	 *         The schema was not valid JSON.
	 * 
	 * @throws ConcordiaException
	 *         The schema is invalid.
	 */
	public Concordia(
		final String schema,
		final ValidationController controller)
		throws 
			IllegalArgumentException,
			IOException,
			JsonParseException,
			ConcordiaException {
		
		this(new ByteArrayInputStream(schema.getBytes()), controller);
	}
	
	/**
	 * Creates a new Concordia object and validates it. It will use the default
	 * validator of {@link ValidationController#BASIC_CONTROLLER}.
	 * 
	 * @param schema
	 *        The schema to validate and use to create this object.
	 * 
	 * @throws IllegalArgumentException
	 *         The schema is null.
	 * 
	 * @throws IOException
	 *         The schema could not be read.
	 * 
	 * @throws JsonParseException
	 *         The schema was not valid JSON.
	 * 
	 * @throws ConcordiaException
	 *         The schema is invalid.
	 */
	public Concordia(
		final InputStream schema)
		throws 
			IllegalArgumentException,
			IOException,
			JsonParseException,
			ConcordiaException {
		
		this(schema, null);
	}

	/**
	 * Creates a new Concordia object and validates it.
	 * 
	 * @param schema
	 *        The schema to validate and use to create this object.
	 *        
	 * @param controller
	 *        A custom validation controller or null, in which case the default
	 *        controller will be used,
	 *        {@link ValidationController#BASIC_CONTROLLER}.
	 *         
	 * @throws IllegalArgumentException
	 *         The schema is null.
	 * 
	 * @throws IOException
	 *         The schema could not be read.
	 * 
	 * @throws JsonParseException
	 *         The schema was not valid JSON.
	 * 
	 * @throws ConcordiaException
	 *         The schema is invalid.
	 */
	public Concordia(
		final InputStream schema,
		final ValidationController controller)
		throws 
			IllegalArgumentException,
			IOException,
			JsonParseException,
			ConcordiaException {
		
		// Validate the schema.
		if(schema == null) {
			throw new IllegalArgumentException("The schema is null.");
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

		// If a controller was not given, fall back to the default one.
		if(controller == null) {
			this.controller = ValidationController.BASIC_CONTROLLER;
		}
		// Otherwise, use the given controller.
		else {
			this.controller = controller;
		}
		
		// Validate the schema using the controller.
		setup();
	}
	
	/**
	 * Creates a new Concordia object and validates it.
	 * 
	 * @param parser
	 *        A JsonParser that is pointing to the definition and can be read.
	 * 
	 * @param controller
	 *        A custom validation controller or null, in which case the default
	 *        controller will be used,
	 *        {@link ValidationController#BASIC_CONTROLLER}.
	 * 
	 * @throws IllegalArgumentException
	 *         The schema is null.
	 * 
	 * @throws IOException
	 *         The schema could not be read.
	 * 
	 * @throws JsonParseException
	 *         The schema was not valid JSON.
	 * 
	 * @throws ConcordiaException
	 *         The schema is invalid.
	 */
	public Concordia(
		final JsonParser parser,
		final ValidationController controller)
		throws 
			IllegalArgumentException,
			IOException,
			JsonParseException,
			ConcordiaException {

		// Validate the schema.
		if(parser == null) {
			throw new IllegalArgumentException("The parser is null.");
		}

		// Process the JSON and create a Schema from it.
		try {
			this.schema = JSON_READER.readValue(parser);
		}
		catch(JsonMappingException e) {
			throw
				new ConcordiaException(
					"The schema was malformed or invalid.",
					e);
		}
		
		// If a controller was not given, fall back to the default one.
		if(controller == null) {
			this.controller = ValidationController.BASIC_CONTROLLER;
		}
		// Otherwise, use the given controller.
		else {
			this.controller = controller;
		}

		// Validate the schema using the controller.
		setup();
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
	 * Post-construction validation. This should be used in constructors after
	 * the initial state of the machine has been setup. This will then validate
	 * the schema using the validator and do any additional, necessary
	 * validation and setup.
	 * 
	 * @throws ConcordiaException
	 *         The schema is invalid.
	 */
	private void setup() throws ConcordiaException {
		// Validate the schema.
		this.controller.validate(this.schema);
		
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