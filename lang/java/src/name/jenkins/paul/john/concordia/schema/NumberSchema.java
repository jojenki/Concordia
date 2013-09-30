package name.jenkins.paul.john.concordia.schema;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>
 * The number Concordia type.
 * </p>
 * 
 * <p>
 * This class is immutable.
 * </p>
 * 
 * @author John Jenkins
 */
public class NumberSchema extends Schema {
	/**
	 * The field value for this type.
	 */
	public static final String TYPE_ID = "number";
	
	/**
	 * An ID for this class for serialization purposes.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This is a private constructor that will be used by Jackson to build the
	 * object with their defaults. It will then modify those defaults if they
	 * were provided in the JSON. This should not be used anywhere else.
	 * 
	 * @see #NumberSchema(String, boolean, String)
	 */
	private NumberSchema() {
		super(null, false, null);
	}

	/**
	 * Creates a new number schema.
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
	 */
	public NumberSchema(
		@JsonProperty(JSON_KEY_DOC) final String doc,
		@JsonProperty(JSON_KEY_OPTIONAL) final boolean optional,
		@JsonProperty(ObjectSchema.JSON_KEY_NAME) final String name) {
		
		super(doc, optional, name);
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
		return Collections.emptyList();
	}
}