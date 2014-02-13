package name.jenkins.paul.john.concordia.schema;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import name.jenkins.paul.john.concordia.exception.ConcordiaException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>
 * The boolean Concordia type.
 * </p>
 *
 * <p>
 * This class is immutable.
 * </p>
 *
 * @author John Jenkins
 */
public class BooleanSchema extends Schema {
    /**
     * <p>
     * A builder specifically for reference schemas.
     * </p>
     *
     * @author John Jenkins
     */
    public static class Builder extends Schema.Builder {
        /**
         * Creates a builder based off of an existing schema.
         *
         * @param original
         *        The original schema to base the fields in this builder off
         *        of.
         */
        public Builder(final BooleanSchema original) {
            super(original);
        }

        /*
         * (non-Javadoc)
         * @see name.jenkins.paul.john.concordia.schema.Schema.Builder#build()
         */
        @Override
        public BooleanSchema build() throws ConcordiaException {
            return
                new BooleanSchema(
                    getDoc(),
                    getOptional(),
                    getName(),
                    getOthers());
        }
    }

	/**
	 * The field value for this type.
	 */
	public static final String TYPE_ID = "boolean";

	/**
	 * An ID for this class for serialization purposes.
	 */
	private static final long serialVersionUID = 1L;

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
	 */
	@JsonCreator
	public BooleanSchema(
		@JsonProperty(JSON_KEY_DOC) final String doc,
		@JsonProperty(JSON_KEY_OPTIONAL) final boolean optional,
		@JsonProperty(ObjectSchema.JSON_KEY_NAME) final String name) {

		this(doc, optional, name, null);
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
	protected BooleanSchema(
        final String doc,
        final boolean optional,
        final String name,
        final Map<String, Object> others) {

	    super(doc, optional, name, others);
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

    /*
     * (non-Javadoc)
     * @see name.jenkins.paul.john.concordia.schema.Schema#getBuilder()
     */
    @Override
    public Schema.Builder getBuilder() {
        return new BooleanSchema.Builder(this);
    }
}