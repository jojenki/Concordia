package name.jenkins.paul.john.concordia.schema;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import name.jenkins.paul.john.concordia.Concordia;
import name.jenkins.paul.john.concordia.exception.ConcordiaException;
import name.jenkins.paul.john.concordia.validator.ValidationController;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>
 * A schema element that references another schema.
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
     * <p>
     * A builder specifically for reference schemas.
     * </p>
     *
     * @author John Jenkins
     */
    public static class Builder extends Schema.Builder {
        /**
         * The reference to the external schema.
         */
        private URL reference;

        /**
         * Creates a builder based off of an existing schema.
         *
         * @param original
         *        The original schema to base the fields in this builder off
         *        of.
         */
        public Builder(final ReferenceSchema original) {
            super(original);

            reference = original.reference;
        }

        /**
         * Returns the currently set reference.
         *
         * @return The currently set reference.
         */
        public URL getReference() {
            return reference;
        }

        /**
         * Sets the reference. It is not evaluated until the schema is built.
         *
         * @param reference
         *        The desired reference.
         *
         * @return Returns this to facilitate chaining.
         *
         * @see #build()
         */
        public ReferenceSchema.Builder setReference(final URL reference) {
            this.reference = reference;

            return this;
        }

        /*
         * (non-Javadoc)
         * @see name.jenkins.paul.john.concordia.schema.Schema.Builder#build()
         */
        @Override
        public ReferenceSchema build() throws ConcordiaException {
            return
                new ReferenceSchema(
                    getDoc(),
                    getOptional(),
                    getName(),
                    reference,
                    getOthers());
        }
    }

	/**
	 * The field value for this type.
	 */
	public static final String TYPE_ID = "reference";

    /**
     * The JSON key for objects that are references to remote types.
     */
    public static final String JSON_KEY_REFERENCE = "$ref";

	/**
	 * The validated sub-definition for this field.
	 */
	public static final String JSON_KEY_DEFINITION = "definition";

	/**
	 * An ID for this class for serialization purposes.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The URL for the referenced schema.
	 */
	@JsonProperty(JSON_KEY_REFERENCE)
    @JsonInclude(Include.NON_NULL)
	private URL reference = null;

	/**
	 * The sub-schema that will be populated during validation based on the
	 * {@link #reference}.
	 */
	@JsonIgnore
	private Schema subSchema = null;

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

		this(doc, optional, name, reference, null);
	}

    /**
     * Creates a new boolean schema.
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
    protected ReferenceSchema(
        final String doc,
        final boolean optional,
        final String name,
        final URL reference,
        final Map<String, Object> others)
        throws ConcordiaException {

        super(doc, optional, name, others);

        if(reference == null) {
            throw new ConcordiaException("The reference URL is null.");
        }

        this.reference = reference;
        try {
            InputStream inputStream = reference.openStream();
            subSchema =
                new Concordia(
                    inputStream,
                    ValidationController.BASIC_CONTROLLER)
                    .getSchema();
            inputStream.close();
        }
        catch(IOException e) {
            throw new ConcordiaException(
                "There was an error reading the schema.",
                e);
        }
    }

	/**
	 * Returns the sub-schema for this referenced schema.
	 *
	 * @return The sub-schema for this referenced schema.
	 */
	public Schema getSchema() {
		return subSchema;
	}

    /**
     * Retrieves the appropriate field name(s) for this schema. If the
     * sub-schema has a field name, that is used. If not, the sub-schema must
     * be an object schema, in which case its field names are used.
     *
     * @return The sub-schema's name, if given, otherwise, the field names of
     *         this sub-schema's object fields (assuming this it is an object
     *         schema).
     *
     * @throws ConcordiaException
     *         The sub-schema doesn't define a field name and is not an object
     *         schema.
     */
	public List<String> getFieldNames() throws ConcordiaException {
		List<String> result = new ArrayList<String>(1);

		if(getName() == null) {
			if(subSchema instanceof ObjectSchema) {
				result = ((ObjectSchema) subSchema).getFieldNames();
			}
			else {
			    throw
			        new ConcordiaException(
			            "The sub-schema does not define a name and is not " +
			                "an object schema.");
			}
		}
		else {
		    result.add(getName());
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
		return subSchema.getSubSchemas();
	}

	/*
	 * (non-Javadoc)
	 * @see name.jenkins.paul.john.concordia.schema.Schema#getBuilder()
	 */
	@Override
    public Schema.Builder getBuilder() {
	    return new ReferenceSchema.Builder(this);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result =
			(prime * result) + ((subSchema == null) ? 0 : subSchema.hashCode());
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
		if(!(obj instanceof ReferenceSchema)) {
			return false;
		}
		if(!super.equals(obj)) {
			return false;
		}
		ReferenceSchema other = (ReferenceSchema) obj;
		if(subSchema == null) {
			if(other.subSchema != null) {
				return false;
			}
		}
		else if(!subSchema.equals(other.subSchema)) {
			return false;
		}
		return true;
	}
}