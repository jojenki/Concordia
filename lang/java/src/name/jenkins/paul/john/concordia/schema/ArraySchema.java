package name.jenkins.paul.john.concordia.schema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import name.jenkins.paul.john.concordia.exception.ConcordiaException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>
 * The array Concordia type. This must include either a single {@link Schema}
 * defining a constant-type array or a list of {@link Schema}s defining a
 * constant-length array.
 * </p>
 *
 * <p>
 * This class is immutable.
 * </p>
 *
 * @author John Jenkins
 */
@JsonInclude(Include.NON_NULL)
public class ArraySchema extends Schema {
    /**
     * <p>
     * A builder specifically for reference schemas.
     * </p>
     *
     * @author John Jenkins
     */
    public static class Builder extends Schema.Builder {
        /**
         * The {@link Schema} for this array if it is a constant-type array.
         */
        private Schema constType;
        /**
         * The {@link Schema}s for this array if it is a constant-type array.
         */
        private List<Schema> constLength;

        /**
         * Creates a builder based off of an existing schema.
         *
         * @param original
         *        The original schema to base the fields in this builder off
         *        of.
         */
        public Builder(final ArraySchema original) {
            super(original);

            constType = original.constType;
            constLength = original.constLength;
        }

        /**
         * Returns the currently set type for a constant type array.
         *
         * @return The currently set type for a constant type array.
         */
        public Schema getConstType() {
            return constType;
        }

        /**
         * Sets the type for a constant type array. This will not be checked
         * with whether or not there is a constant length value until the
         * schema is built. This may be null to clear the constant type-ness.
         *
         * @param constType
         *        The type for this constant type array.
         *
         * @return Returns this to facilitate chaining.
         *
         * @see #build()
         */
        public ArraySchema.Builder setConstType(final Schema constType) {
            this.constType = constType;

            return this;
        }

        /**
         * Returns the currently set list of types for a constant length array.
         * The internal list is backed by the response value, so modifications
         * to the returned list will be reflected in this builder.
         *
         * @return The currently set list of types for a constant length array.
         */
        public List<Schema> getConstLength() {
            return constLength;
        }

        /**
         * Sets the list of types for a constant length array. This will not be
         * checked with whether or not there is a constant type value until the
         * schema is built. This may be null to clear the constant length-ness.
         *
         * @param constLength
         *        The list of types for this constant length array.
         *
         * @return Returns this to facilitate chaining.
         *
         * @see #build()
         */
        public ArraySchema.Builder setConstLength(
            final List<Schema> constLength) {

            this.constLength = constLength;

            return this;
        }

        /*
         * (non-Javadoc)
         * @see name.jenkins.paul.john.concordia.schema.Schema.Builder#build()
         */
        @Override
        public ArraySchema build() throws ConcordiaException {
            return
                new ArraySchema(
                    getDoc(),
                    getOptional(),
                    getName(),
                    constType,
                    constLength,
                    getOthers());
        }
    }

	/**
	 * The field value for this type.
	 */
	public static final String TYPE_ID = "array";

	/**
	 * The JSON key for the sub-schema for a constant-type array.
	 */
	public static final String JSON_KEY_CONST_TYPE = "constType";

	/**
	 * The JSON key for the sub-schema for a constant-type array.
	 */
	public static final String JSON_KEY_CONST_LENGTH = "constLength";

	/**
	 * An ID for this class for serialization purposes.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The {@link Schema} for this array if it is a constant-type array.
	 */
	@JsonProperty(JSON_KEY_CONST_TYPE)
    @JsonInclude(Include.NON_NULL)
	private final Schema constType;
	/**
	 * The {@link Schema}s for this array if it is a constant-type array.
	 */
	@JsonProperty(JSON_KEY_CONST_LENGTH)
    @JsonInclude(Include.NON_NULL)
	private final List<Schema> constLength;

	/**
	 * Creates a new constant-type array schema.
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
	 * @param constType
	 *        The type of all of the elements in corresponding data arrays.
	 *
	 * @throws ConcordiaException
	 * 		   The schema is null.
	 */
	public ArraySchema(
		final String doc,
		final boolean optional,
		final String name,
		final Schema constType)
		throws ConcordiaException {

		this(doc, optional, name, constType, null);
	}

	/**
	 * Creates a new constant-length array schema.
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
	 * @param constLength
	 *        An array of types where the type at each index in this array must
	 *        equal the type in at the corresponding index in an array of data.
	 *
	 * @throws ConcordiaException
	 * 		   The list of schemas is null.
	 */
	public ArraySchema(
		final String doc,
		final boolean optional,
		final String name,
		final List<Schema> constLength)
		throws ConcordiaException {

		this(doc, optional, name, null, constLength);
	}

    /**
     * Creates a new constant-type array schema.
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
     * @param constType
     *        The type of all of the elements in corresponding data arrays.
     *
     * @param constLength
     *        An array of types where the type at each index in this array must
     *        equal the type in at the corresponding index in an array of data.
     *
     * @throws ConcordiaException
     *         The constType and constLength are both null or are both not
     *         null. Only one may be given.
     */
	@JsonCreator
	protected ArraySchema(
		@JsonProperty(JSON_KEY_DOC) final String doc,
		@JsonProperty(JSON_KEY_OPTIONAL) final boolean optional,
		@JsonProperty(ObjectSchema.JSON_KEY_NAME) final String name,
		@JsonProperty(JSON_KEY_CONST_TYPE) final Schema constType,
		@JsonProperty(JSON_KEY_CONST_LENGTH) final List<Schema> constLength)
		throws ConcordiaException {

		this(doc, optional, name, constType, constLength, null);
	}

    /**
     * Creates a new array schema.
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
     *
     * @throws ConcordiaException
     *         The constType and constLength are both null or are both not
     *         null. Only one may be given.
     */
    protected ArraySchema(
        final String doc,
        final boolean optional,
        final String name,
        final Schema constType,
        final List<Schema> constLength,
        final Map<String, Object> others)
        throws ConcordiaException {

        super(doc, optional, name, others);

        if((constType == null) && (constLength == null)) {
            throw new ConcordiaException("The schema is missing.");
        }
        else if((constType != null) && (constLength != null)) {
            throw
                new ConcordiaException(
                    "Both a constant-type and constant-length were " +
                        "defined for the same array.");
        }

        this.constType = constType;
        this.constLength = constLength;
    }

	/**
	 * Returns the schema. If null, this is a constant-length array.
	 *
	 * @return The schema for every element in corresponding data.
	 */
	public Schema getConstType() {
		return constType;
	}

	/**
	 * Returns the index-by-index schema for every element in a constant-type
	 * array. If this is null, this is a constant-type schema.
	 *
	 * @return An index-by-index schema for corresponding data.
	 */
	public List<Schema> getConstLength() {
		if(constLength == null) {
			return null;
		}
		else {
			return Collections.unmodifiableList(constLength);
		}
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
		// Create a result list, which will either be exactly the constant-type
		// type or the constant-length list which will overwrite this.
		List<Schema> result = new ArrayList<Schema>(1);

		// If the constant-type exists, use that.
		if(constType != null) {
			result.add(constType);
		}
		// If the constant-length exists, use that.
		else if(constLength != null) {
			result = constLength;
		}

		// Return an unmodifiable copy of the list.
		return Collections.unmodifiableList(result);
	}

    /*
     * (non-Javadoc)
     * @see name.jenkins.paul.john.concordia.schema.Schema#getBuilder()
     */
    @Override
    public Schema.Builder getBuilder() {
        return new ArraySchema.Builder(this);
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result =
			(prime *
				result) +
				((constLength == null) ? 0 : constLength.hashCode());
		result =
			(prime * result) + ((constType == null) ? 0 : constType.hashCode());
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
		if(!(obj instanceof ArraySchema)) {
			return false;
		}
		if(!super.equals(obj)) {
			return false;
		}
		ArraySchema other = (ArraySchema) obj;
		if(constLength == null) {
			if(other.constLength != null) {
				return false;
			}
		}
		else if(!constLength.equals(other.constLength)) {
			return false;
		}
		if(constType == null) {
			if(other.constType != null) {
				return false;
			}
		}
		else if(!constType.equals(other.constType)) {
			return false;
		}
		return true;
	}
}