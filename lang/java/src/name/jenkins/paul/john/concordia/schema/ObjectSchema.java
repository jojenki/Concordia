package name.jenkins.paul.john.concordia.schema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
     * <p>
     * A builder specifically for reference schemas.
     * </p>
     *
     * @author John Jenkins
     */
    public static class Builder extends Schema.Builder {
        /**
         * The list of fields in this object.
         */
        private List<Schema> fields;

        /**
         * Creates a builder based off of an existing schema.
         *
         * @param original
         *        The original schema to base the fields in this builder off
         *        of.
         */
        public Builder(final ObjectSchema original) {
            super(original);

            fields = new ArrayList<Schema>(original.fields);
        }

        /**
         * Returns the currently set list of fields. The internal list is
         * backed by the response value, so modifications to the returned list
         * will be reflected in this builder.
         *
         * @return The currently set list of fields.
         */
        public List<Schema> getFields() {
            return fields;
        }

        /**
         * Sets the list of fields. There is no aggregation; this will replace
         * the current list of fields. It will be a deep copy of the parameter,
         * so modifying the parameterized list will have no effect on the
         * internal list of fields.
         *
         * @param fields
         *        The desired list of fields.
         *
         * @return Returns this to facilitate chaining.
         */
        public ObjectSchema.Builder setReference(final List<Schema> fields) {
            this.fields = new ArrayList<Schema>(fields);

            return this;
        }

        /*
         * (non-Javadoc)
         * @see name.jenkins.paul.john.concordia.schema.Schema.Builder#build()
         */
        @Override
        public ObjectSchema build() throws ConcordiaException {
            return
                new ObjectSchema(
                    getDoc(),
                    getOptional(),
                    getName(),
                    fields,
                    getOthers());
        }
    }

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
	 * An ID for this class for serialization purposes.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The list of fields for this object.
	 */
	@JsonProperty(JSON_KEY_FIELDS)
	private final List<Schema> fields = new ArrayList<Schema>();

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

		this(doc, optional, name, fields, null);
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
     * @param others
     *        Additional undefined fields that are being preserved.
     */
    protected ObjectSchema(
        final String doc,
        final boolean optional,
        final String name,
        final List<Schema> fields,
        final Map<String, Object> others)
        throws ConcordiaException {

        super(doc, optional, name, others);

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

    /*
     * (non-Javadoc)
     * @see name.jenkins.paul.john.concordia.schema.Schema#getBuilder()
     */
    @Override
    public Schema.Builder getBuilder() {
        return new ObjectSchema.Builder(this);
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = (prime * result) + ((fields == null) ? 0 : fields.hashCode());
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
     * Retrieves the names of all of the fields including those from a
     * referenced schema.
     *
     * @return The name of each field, including those from referenced schemas.
     *
     * @throws ConcordiaException
     *         One of the child fields defines an invalid reference schema.
     */
	public List<String> getFieldNames() throws ConcordiaException {
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