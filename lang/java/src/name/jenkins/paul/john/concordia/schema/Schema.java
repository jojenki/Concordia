package name.jenkins.paul.john.concordia.schema;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * <p>
 * The superclass for all Concordia types.
 * </p>
 * 
 * <p>
 * This class is immutable.
 * </p>
 * 
 * <p>
 * All sub-classes MUST implement a no-argument constructor even if it is
 * private and never used. This is required by Jackson to properly perform its
 * deserialization methods. This is still applicable even if there are other
 * constructors with arguments.
 * </p>
 * 
 * @author John Jenkins
 */
@JsonTypeInfo(
	use = Id.NAME,
	include = As.PROPERTY,
	property = Schema.JSON_KEY_TYPE,
	defaultImpl = ReferenceSchema.class)
@JsonSubTypes({
	@JsonSubTypes.Type(
		value = BooleanSchema.class,
		name = BooleanSchema.TYPE_ID),
	@JsonSubTypes.Type(value = NumberSchema.class, name = NumberSchema.TYPE_ID),
	@JsonSubTypes.Type(value = StringSchema.class, name = StringSchema.TYPE_ID),
	@JsonSubTypes.Type(value = ObjectSchema.class, name = ObjectSchema.TYPE_ID),
	@JsonSubTypes.Type(value = ArraySchema.class, name = ArraySchema.TYPE_ID) })
@JsonAutoDetect(
	fieldVisibility = Visibility.DEFAULT,
	getterVisibility = Visibility.NONE,
	setterVisibility = Visibility.NONE,
	creatorVisibility = Visibility.DEFAULT)
public abstract class Schema {
	/**
	 * The JSON key used to define the type of the current schema.
	 */
	public static final String JSON_KEY_TYPE = "type";

	/**
	 * The JSON key used to define documentation for a schema.
	 */
	public static final String JSON_KEY_DOC = "doc";
	/**
	 * The JSON key used to define whether or not a schema is optional.
	 */
	public static final String JSON_KEY_OPTIONAL = "optional";

	/**
	 * The {@link #JSON_KEY_DOC} field with a default value of null.
	 */
	@JsonProperty(JSON_KEY_DOC)
	@JsonInclude(Include.NON_DEFAULT)
	private final String doc;
	/**
	 * The {@link #JSON_KEY_OPTIONAL} field with a default value of false.
	 */
	@JsonProperty(JSON_KEY_OPTIONAL)
	@JsonInclude(Include.NON_DEFAULT)
	private final boolean optional;

	/**
	 * The name for this field if it is part of an object.
	 */
	@JsonProperty(ObjectSchema.JSON_KEY_NAME)
	@JsonInclude(Include.NON_DEFAULT)
	private final String name;

	/**
	 * The other fields that were given but are not part of Concordia proper.
	 */
	@JsonIgnore
	private final Map<String, Object> others = new HashMap<String, Object>();
	
	/**
	 * Creates a Schema object with the schema-type agnostic fields.
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
	public Schema(
		final String doc,
		final boolean optional,
		final String name) {
		
		this.doc = doc;
		this.optional = optional;
		this.name = name;
	}
	
	/**
	 * Returns the documentation for this schema.
	 *  
	 * @return The documentation for this schema. This may be null.
	 */
	public String getDoc() {
		return doc;
	}

	/**
	 * Returns whether or not this type is optional.
	 * 
	 * @return Whether or not this type is optional.
	 */
	public boolean isOptional() {
		return optional;
	}

	/**
	 * Gets the name of this type, if given. It _may_ only be given if this is
	 * within an {@link ObjectSchema}'s field list.
	 * 
	 * @return The name of this object field or null if no name was given.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns any non-standard fields that were given.
	 * 
	 * @return A map of keys to values for non-standard fields.
	 */
	public Map<String, Object> getAdditionalFields() {
		return Collections.unmodifiableMap(others);
	}

	/**
	 * Returns the Concordia type name for this type.
	 * 
	 * @return The Concordia type name for this type.
	 */
	public abstract String getType();
	
	/**
	 * Returns the list of sub-schemas for this schema. Any type may not have
	 * any sub-schemas and some schemas, e.g. {@link BooleanSchema}, will never
	 * have any sub-schemas.
	 * 
	 * @return The list of sub-schemas for this schema. This should always
	 *         return an unmodifiable list, even if it is empty.
	 */
	public abstract List<Schema> getSubSchemas(); 

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((doc == null) ? 0 : doc.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (optional ? 1231 : 1237);
		result = prime * result + ((others == null) ? 0 : others.hashCode());
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
		if(!(obj instanceof Schema)) {
			return false;
		}
		Schema other = (Schema) obj;
		if(doc == null) {
			if(other.doc != null) {
				return false;
			}
		}
		else if(!doc.equals(other.doc)) {
			return false;
		}
		if(name == null) {
			if(other.name != null) {
				return false;
			}
		}
		else if(!name.equals(other.name)) {
			return false;
		}
		if(optional != other.optional) {
			return false;
		}
		if(others == null) {
			if(other.others != null) {
				return false;
			}
		}
		else if(!others.equals(other.others)) {
			return false;
		}
		return true;
	}

	/**
	 * Stores any unknown fields.
	 * 
	 * @param key
	 *        The field's key.
	 * 
	 * @param value
	 *        The field's value.
	 */
	@JsonAnySetter
	protected void handleUnknown(final String key, final Object value) {
		others.put(key, value);
	}

	/**
	 * Returns the map of unknown fields.
	 * 
	 * @return The map of unknown fields and their values.
	 */
	@JsonAnyGetter
	protected Map<String, Object> handleUnknown() {
		return Collections.unmodifiableMap(others);
	}
}