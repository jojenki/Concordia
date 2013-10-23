package name.jenkins.paul.john.concordia.schema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
	private final Schema constType;
	/**
	 * The {@link Schema}s for this array if it is a constant-type array.
	 */
	@JsonProperty(JSON_KEY_CONST_LENGTH)
	private final List<Schema> constLength;

	/**
	 * This is a private constructor that will be used by Jackson to build the
	 * object with their defaults. It will then modify those defaults if they
	 * were provided in the JSON. This should not be used anywhere else.
	 * 
	 * @see #ArraySchema(String, boolean, String, Schema)
	 * 		Constant-Schema Constructor
	 * @see #ArraySchema(String, boolean, String, List)
	 * 		Constant-Length Constructor
	 */
	private ArraySchema() {
		super(null, false, null);
		constType = null;
		constLength = null;
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
	 * @throws ConcordiaException
	 * 		   The schema is null.
	 */
	public ArraySchema(
		final String doc,
		final boolean optional,
		final String name,
		final Schema constType)
		throws ConcordiaException {
		
		super(doc, optional, name);
		
		if(constType == null) {
			throw new ConcordiaException("The type is null.");
		}

		this.constType = constType;
		constLength = null;
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
		
		super(doc, optional, name);
		
		if(constLength == null) {
			throw new ConcordiaException("The array is null.");
		}

		this.constType = null;
		this.constLength = new ArrayList<Schema>(constLength);
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
	 */
	@JsonCreator
	private ArraySchema(
		@JsonProperty(JSON_KEY_DOC) final String doc,
		@JsonProperty(JSON_KEY_OPTIONAL) final boolean optional,
		@JsonProperty(ObjectSchema.JSON_KEY_NAME) final String name,
		@JsonProperty(JSON_KEY_CONST_TYPE) final Schema constType,
		@JsonProperty(JSON_KEY_CONST_LENGTH) final List<Schema> constLength)
		throws IllegalArgumentException {
		
		super(doc, optional, name);
		
		if((constType == null) && (constLength == null)) {
			throw new IllegalArgumentException("The schema is missing.");
		}
		else if((constType != null) && (constLength != null)) {
			throw
				new IllegalArgumentException(
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result =
			prime *
				result +
				((constLength == null) ? 0 : constLength.hashCode());
		result =
			prime * result + ((constType == null) ? 0 : constType.hashCode());
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