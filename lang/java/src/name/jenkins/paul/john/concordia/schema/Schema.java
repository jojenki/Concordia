package name.jenkins.paul.john.concordia.schema;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import name.jenkins.paul.john.concordia.exception.ConcordiaException;

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
	@JsonSubTypes.Type(
		value = NumberSchema.class,
		name = NumberSchema.TYPE_ID),
	@JsonSubTypes.Type(
		value = StringSchema.class,
		name = StringSchema.TYPE_ID),
	@JsonSubTypes.Type(
		value = ObjectSchema.class,
		name = ObjectSchema.TYPE_ID),
	@JsonSubTypes.Type(
		value = ArraySchema.class,
		name = ArraySchema.TYPE_ID) })
@JsonAutoDetect(
	fieldVisibility = Visibility.DEFAULT,
	getterVisibility = Visibility.NONE,
	setterVisibility = Visibility.NONE,
	creatorVisibility = Visibility.DEFAULT)
public abstract class Schema implements Serializable {
    /**
     * <p>
     * The root builder for all schema builders. Each schema should have its
     * own builder class that extends this class.
     * </p>
     *
     * @author John Jenkins
     */
    public static abstract class Builder {
        /**
         * The documentation for the schema.
         */
        private String doc;
        /**
         * Whether or not data points may use null in place of a value. This is
         * only relevant for fields and array indexes.
         */
        private boolean optional;
        /**
         * The field name for this schema.
         */
        private String name;

        /**
         * The map of additional, unknown fields.
         */
        private final Map<String, Object> others;

        /**
         * Creates a builder based off of an existing schema.
         *
         * @param original
         *        The original schema to base the fields in this builder off
         *        of.
         */
        public Builder(final Schema original) {
            doc = original.doc;
            optional = original.optional;
            name = original.name;
            others = new HashMap<String, Object>(original.others);
        }

        /**
         * Returns the currently set documentation.
         *
         * @return The currently set documentation.
         */
        public String getDoc() {
            return doc;
        }

        /**
         * Sets the documentation.
         *
         * @param doc
         *        The desired documentation including null to clear the
         *        currently set documentation.
         *
         * @return Returns this to facilitate chaining.
         */
        public Schema.Builder setDoc(final String doc) {
            this.doc = doc;

            return this;
        }

        /**
         * Returns the currently set optional flag.
         *
         * @return The currently set optional flag.
         */
        public boolean getOptional() {
            return optional;
        }

        /**
         * Sets the optional flag.
         *
         * @param optional
         *        The desired value.
         *
         * @return Returns this to facilitate chaining.
         */
        public Schema.Builder setOptional(final boolean optional) {
            this.optional = optional;

            return this;
        }

        /**
         * Returns the currently set field name.
         *
         * @return The currently set field name.
         */
        public String getName() {
            return name;
        }

        /**
         * Sets the field name.
         *
         * @param name
         *        The desired name including null to clear the currently set
         *        name.
         *
         * @return Returns this to facilitate chaining.
         */
        public Schema.Builder setName(final String name) {
            this.name = name;

            return this;
        }

        /**
         * Returns the additional, undefined values.
         *
         * @return The additional, undefined values.
         */
        protected Map<String, Object> getOthers() {
            return others;
        }

        /**
         * Uses the current state of this builder to produce a Schema object.
         * This may be called any number of times without affecting the state
         * of the builder.
         *
         * @return A Schema object whose type is based on how this builder was
         *         created.
         *
         * @throws ConcordiaException
         *         The state of this builder is invalid for building a Survey
         *         object.
         */
        public abstract Schema build() throws ConcordiaException;
    }

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
	 * An ID for this class for serialization purposes.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The {@link #JSON_KEY_DOC} field with a default value of null.
	 */
	@JsonProperty(JSON_KEY_DOC)
	@JsonInclude(Include.NON_NULL)
	private final String doc;
	/**
	 * The {@link #JSON_KEY_OPTIONAL} field with a default value of false.
	 */
	@JsonProperty(JSON_KEY_OPTIONAL)
	@JsonInclude(Include.NON_NULL)
	private final boolean optional;

	/**
	 * The name for this field if it is part of an object.
	 */
	@JsonProperty(ObjectSchema.JSON_KEY_NAME)
	@JsonInclude(Include.NON_NULL)
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
     *
     * @param others
     *        Additional undefined fields that are being preserved.
     */
	public Schema(
		final String doc,
		final boolean optional,
		final String name,
		final Map<String, Object> others) {

		this.doc = doc;
		this.optional = optional;
		this.name = name;

		if(others != null) {
		    this.others.putAll(others);
		}
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

    /**
     * Returns a builder specific to this type of schema with all of the values
     * in the builder set to the value of this schema.
     *
     * @return A builder to be used to create a new schema based off of this
     *         schema.
     */
	public abstract Schema.Builder getBuilder();

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((doc == null) ? 0 : doc.hashCode());
		result = (prime * result) + ((name == null) ? 0 : name.hashCode());
		result = (prime * result) + (optional ? 1231 : 1237);
		result = (prime * result) + ((others == null) ? 0 : others.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
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