package name.jenkins.paul.john.concordia.schema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import name.jenkins.paul.john.concordia.exception.ConcordiaException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>
 * The object Concordia type. This must include a definition of its fields.
 * </p>
 * 
 * <p>
 * This class is immutable.
 * </p>
 * 
 * @author John Jenkins
 */
public class ObjectSchema extends Schema {
	/**
	 * The field value for this type.
	 */
	public static final String TYPE_ID = "object";

	/**
	 * The JSON key for the list of fields.
	 */
	public static final String JSON_KEY_FIELDS = "fields";
	/**
	 * The JSON key for the name of a field.
	 */
	public static final String JSON_KEY_NAME = "name";

	/**
	 * The list of fields for this object.
	 */
	@JsonProperty(JSON_KEY_FIELDS)
	private final List<Schema> fields = new ArrayList<Schema>();

	/**
	 * This is a private constructor that will be used by Jackson to build the
	 * object with their defaults. It will then modify those defaults if they
	 * were provided in the JSON. This should not be used anywhere else.
	 * 
	 * @see #ObjectSchema(String, boolean, String, List)
	 */
	private ObjectSchema() {
		super(null, false, null);
	}

	/**
	 * Creates a new object schema.
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
	 * @param fields
	 *        The list of fields for this schema. If this is null, an empty
	 *        list is returned.
	 * 
	 * @throws ConcordiaException
	 *         The fields list is null.
	 */
	@JsonCreator
	public ObjectSchema(
		@JsonProperty(JSON_KEY_DOC) final String doc,
		@JsonProperty(JSON_KEY_OPTIONAL) final boolean optional,
		@JsonProperty(ObjectSchema.JSON_KEY_NAME) final String name,
		@JsonProperty(JSON_KEY_FIELDS) final List<Schema> fields)
		throws ConcordiaException {
		
		super(doc, optional, name);

		if(fields == null) {
			throw new ConcordiaException("The fields list is null.");
		}
		else {
			this.fields.addAll(fields);
		}
	}

	/**
	 * Returns the list of fields.
	 * 
	 * @return Returns the list of fields.
	 */
	public List<Schema> getFields() {
		return Collections.unmodifiableList(fields);
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
		return Collections.unmodifiableList(fields);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((fields == null) ? 0 : fields.hashCode());
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
		if(!(obj instanceof ObjectSchema)) {
			return false;
		}
		if(!super.equals(obj)) {
			return false;
		}
		ObjectSchema other = (ObjectSchema) obj;
		if(fields == null) {
			if(other.fields != null) {
				return false;
			}
		}
		else if(!fields.equals(other.fields)) {
			return false;
		}
		return true;
	}

	/**
	 * Retrieves the names of all of the fields include those from a referenced
	 * schema.
	 * 
	 * @return The name of each field, including those from referenced schemas.
	 */
	public List<String> getFieldNames() {
		List<String> result = new LinkedList<String>();

		for(Schema field : fields) {
			if(field instanceof ReferenceSchema) {
				result.addAll(((ReferenceSchema) field).getFieldNames());
			}
			else {
				result.add(field.getName());
			}
		}

		return result;
	}
}