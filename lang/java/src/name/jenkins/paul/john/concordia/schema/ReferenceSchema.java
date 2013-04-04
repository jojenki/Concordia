package name.jenkins.paul.john.concordia.schema;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import name.jenkins.paul.john.concordia.Concordia;
import name.jenkins.paul.john.concordia.exception.ConcordiaException;
import name.jenkins.paul.john.concordia.validator.ValidationController;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>
 * A schema element that references an external schema.
 * </p>
 * 
 * <p>
 * This class is immutable.
 * </p>
 * 
 * @author John Jenkins
 */
public class ReferenceSchema extends Schema {
	/**
	 * The JSON key for objects that are references to remote types.
	 */
	public static final String JSON_KEY_REFERENCE = "$ref";

	/**
	 * The field value for this type.
	 */
	public static final String TYPE_ID = "reference";

	/**
	 * The URL for the referenced schema.
	 */
	@JsonProperty(JSON_KEY_REFERENCE)
	private URL reference = null;
	/**
	 * The sub-schema that will be populated during validation based on the
	 * {@link #reference}.
	 */
	@JsonIgnore
	private Concordia concordia = null;

	/**
	 * This is a private constructor that will be used by Jackson to build the
	 * object with their defaults. It will then modify those defaults if they
	 * were provided in the JSON. This should not be used anywhere else.
	 * 
	 * @see #ReferenceSchema(String, boolean, String, URL)
	 */
	private ReferenceSchema() {
		super(null, false, null);
	}

	/**
	 * Creates a new referenced schema from the given URL.
	 * 
	 * @param doc
	 *        Optional documentation for this Schema.
	 * 
	 * @param optional
	 *        Whether or not data for this Schema is optional.
	 * 
	 * @param name
	 *        The name of this field, which is needed when constructing an
	 *        {@link ObjectSchema}.
	 * 
	 * @param reference
	 *        The reference to the external schema.
	 * 
	 * @throws ConcordiaException
	 *         The reference URL was invalid or the schema it referenced was
	 *         invalid.
	 */
	@JsonCreator
	public ReferenceSchema(
		@JsonProperty(JSON_KEY_DOC) final String doc,
		@JsonProperty(JSON_KEY_OPTIONAL) final boolean optional,
		@JsonProperty(ObjectSchema.JSON_KEY_NAME) final String name,
		@JsonProperty(JSON_KEY_REFERENCE) final URL reference)
		throws ConcordiaException {
		
		super(doc, optional, name);

		if(reference == null) {
			throw new ConcordiaException("The reference URL is null.");
		}

		this.reference = reference;
		resolveReference();
	}

	/**
	 * Returns the sub-schema for this referenced schema.
	 * 
	 * @return The sub-schema for this referenced schema.
	 */
	public Concordia getConcordia() {
		return concordia;
	}

	/**
	 * If this schema does not have a name and the remote schema is an object
	 * schema, get the names of the fields in that object schema.
	 * 
	 * @return The names of the
	 */
	public List<String> getFieldNames() {
		List<String> result = null;

		if(getName() == null) {
			Schema subSchema = concordia.getSchema();

			if(subSchema instanceof ObjectSchema) {
				result = ((ObjectSchema) subSchema).getFieldNames();
			}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see name.jenkins.paul.john.concordia.schema.Schema#getType()
	 */
	@Override
	public String getType() {
		return TYPE_ID;
	}

	/*
	 * (non-Javadoc)
	 * @see name.jenkins.paul.john.concordia.schema.Schema#getSubSchemas()
	 */
	@Override
	public List<Schema> getSubSchemas() {
		return concordia.getSchema().getSubSchemas();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result =
			prime * result + ((concordia == null) ? 0 : concordia.hashCode());
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
		if(!super.equals(obj)) {
			return false;
		}
		if(!(obj instanceof ReferenceSchema)) {
			return false;
		}
		if(!super.equals(obj)) {
			return false;
		}
		ReferenceSchema other = (ReferenceSchema) obj;
		if(concordia == null) {
			if(other.concordia != null) {
				return false;
			}
		}
		else if(!concordia.equals(other.concordia)) {
			return false;
		}
		return true;
	}

	/**
	 * Resolves the URL to a schema, builds a Concordia object, and saves it
	 * internally.
	 */
	private void resolveReference() throws ConcordiaException {
		try {
			InputStream inputStream = reference.openStream();
			concordia =
				new Concordia(
					inputStream,
					ValidationController.BASIC_CONTROLLER);
			inputStream.close();
		}
		catch(IOException e) {
			throw new ConcordiaException(
				"There was an error reading the schema.",
				e);
		}
	}
}