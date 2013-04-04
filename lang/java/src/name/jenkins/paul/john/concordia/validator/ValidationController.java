package name.jenkins.paul.john.concordia.validator;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import name.jenkins.paul.john.concordia.exception.ConcordiaException;
import name.jenkins.paul.john.concordia.schema.ArraySchema;
import name.jenkins.paul.john.concordia.schema.BooleanSchema;
import name.jenkins.paul.john.concordia.schema.NumberSchema;
import name.jenkins.paul.john.concordia.schema.ObjectSchema;
import name.jenkins.paul.john.concordia.schema.ReferenceSchema;
import name.jenkins.paul.john.concordia.schema.Schema;
import name.jenkins.paul.john.concordia.schema.StringSchema;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * <p>
 * This class is responsible for controlling the high-level flow of schema and
 * data validation.
 * </p>
 * 
 * <p>
 * This class must be instantiated from its {@link Builder}. This builder is
 * responsible for adding custom validation rules via the {@link Validator}
 * interfaces.
 * </p>
 * 
 * <p>
 * Once constructed, it has two public methods for {@link #validate(Schema)
 * validating a schema} and
 * {@link #validate(Schema, JsonNode) validating data}.
 * </p>
 * 
 * <p>
 * This class is immutable.
 * </p>
 * 
 * @author John Jenkins
 */
public final class ValidationController {
	/**
	 * <p>
	 * The creator of {@link ValidationController} objects. This class should
	 * be used to add custom validation rules to Concordia.
	 * </p>
	 * 
	 * <p>
	 * This class is not immutable.
	 * </p>
	 * 
	 * @author John Jenkins
	 */
	public static final class Builder {
		/**
		 * This instance's map of schema validators.
		 */
		private final Map<Class<? extends Schema>, List<SchemaValidator<? extends Schema>>> schemaValidators =
			new HashMap<Class<? extends Schema>, List<SchemaValidator<? extends Schema>>>();
		/**
		 * This instance's map of data validators.
		 */
		private final Map<Class<? extends Schema>, List<DataValidator<? extends Schema>>> dataValidators =
			new HashMap<Class<? extends Schema>, List<DataValidator<? extends Schema>>>();

		/**
		 * Creates a new, empty {@link Builder}.
		 */
		public Builder() {
			// Do nothing.
		}

		/**
		 * Adds a schema validator to this map.
		 * 
		 * @param clazz
		 *        The {@link Class}, which must be a sub-class of
		 *        {@link Schema}, to which this schema validator applies. If
		 *        this is null, an exception is thrown.
		 * 
		 * @param validator
		 *        An instance of the validator to be used. If this is null,
		 *        nothing happens.
		 */
		public <T extends Schema> void addSchemaValidator(
			final Class<? extends T> clazz,
			final SchemaValidator<T> validator) throws ConcordiaException {

			if(clazz == null) {
				throw new ConcordiaException("The class is null.");
			}
			if(validator == null) {
				return;
			}

			List<SchemaValidator<? extends Schema>> validators =
				schemaValidators.get(clazz);

			if(validators == null) {
				validators =
					new LinkedList<SchemaValidator<? extends Schema>>();
				schemaValidators.put(clazz, validators);
			}

			validators.add(validator);
		}

		/**
		 * Adds a data validator to this list of data validators.
		 * 
		 * @param clazz
		 *        The {@link Class}, which must be a sub-class of
		 *        {@link Schema}, to which this data validator applies. If this
		 *        is null, an exception is thrown.
		 * 
		 * @param validator
		 *        An instance of the validator to be used. If this is null,
		 *        nothing happens.
		 */
		public <T extends Schema> void addDataValidator(
			final Class<? extends T> clazz,
			final DataValidator<T> validator) throws ConcordiaException {

			if(clazz == null) {
				throw new ConcordiaException("The class is null.");
			}
			if(validator == null) {
				return;
			}

			List<DataValidator<? extends Schema>> validators =
				dataValidators.get(clazz);

			if(validators == null) {
				validators = new LinkedList<DataValidator<? extends Schema>>();
				dataValidators.put(clazz, validators);
			}

			validators.add(validator);
		}

		/**
		 * Adds a validator to this list schema and/or data of validators
		 * depending on the validator's type.
		 * 
		 * @param clazz
		 *        The {@link Class}, which must be a sub-class of
		 *        {@link Schema}, to which this validator applies. If this is
		 *        null, an exception is thrown.
		 * 
		 * @param validator
		 *        An instance of the validator to be used. If this is null,
		 *        nothing happens.
		 */
		public <T extends Schema> void addValidator(
			final Class<? extends T> clazz,
			final Validator<T> validator) throws ConcordiaException {

			if(clazz == null) {
				throw new ConcordiaException("The class is null.");
			}
			if(validator == null) {
				return;
			}

			// If it's a schema validator, add it to the schema validators.
			if(validator instanceof SchemaValidator) {
				addSchemaValidator(clazz, (SchemaValidator<T>) validator);
			}

			// If it's a data validator, add it to the data validators.
			if(validator instanceof DataValidator) {
				addDataValidator(clazz, (DataValidator<T>) validator);
			}
		}

		/**
		 * Builds the {@link ValidationController} based on this builder's
		 * configuration.
		 * 
		 * @return The {@link ValidationController} based on this builder's
		 *         configuration.
		 * 
		 * @throws IllegalStateException
		 *         There was an error while building the
		 *         {@link ValidationController}.
		 */
		public ValidationController build() throws IllegalStateException {
			return new ValidationController(this);
		}
	}

	/**
	 * A default, pre-built controller that uses only the required validators.
	 */
	public static final ValidationController BASIC_CONTROLLER =
		new ValidationController(new Builder());

	/**
	 * This instance's map of schema validators.
	 */
	private final
		Map<Class<? extends Schema>, List<SchemaValidator<? extends Schema>>>
			schemaValidators;
	/**
	 * This instance's map of data validators.
	 */
	private final
		Map<Class<? extends Schema>, List<DataValidator<? extends Schema>>>
			dataValidators;

	/**
	 * <p>
	 * Creates a new ValidationController with the given schema and data
	 * validators.
	 * </p>
	 * 
	 * <p>
	 * The required schema and data validators will be added, so the
	 * parameterized validators must not contain them.
	 * </p>
	 * 
	 * @param schemaValidators
	 *        The map of {@link Schema}s to their schema validators.
	 * 
	 * @param dataValidators
	 *        The map of {@link Schema}s to their data validators.
	 */
	private ValidationController(final Builder builder) {
		// Add the required validators.
		try {
			builder.addValidator(BooleanSchema.class, new BooleanValidator());
			builder.addValidator(NumberSchema.class, new NumberValidator());
			builder.addValidator(StringSchema.class, new StringValidator());
			builder.addValidator(ObjectSchema.class, new ObjectValidator());
			builder.addValidator(ArraySchema.class, new ArrayValidator());
			builder
				.addValidator(ReferenceSchema.class, new ReferenceValidator());
		}
		catch(ConcordiaException e) {
			throw
				new IllegalStateException(
					"An internal error occurred while building the " +
						"validation controller.",
					e);
		}

		// Set the internal state.
		this.schemaValidators =
			Collections.unmodifiableMap(builder.schemaValidators);
		this.dataValidators =
			Collections.unmodifiableMap(builder.dataValidators);
	}

	/**
	 * Validates some schema against its pre-configured schema validators.
	 * 
	 * @param schema
	 *        The schema to validate.
	 * 
	 * @throws ConcordiaException
	 *         The schema is not valid.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void validate(final Schema schema) throws ConcordiaException {
		// Get the schema validators for the 'schema's schema.
		List<SchemaValidator<? extends Schema>> validators =
			schemaValidators.get(schema.getClass());

		// Apply each validator to the 'schema'.
		// Note: Ignore the type warning here. Type-erasure makes this
		// painfully difficult, and, while the type checker cannot guarantee
		// that this is type-safe, our code should ensure that it actually is.
		for(SchemaValidator validator : validators) {
			validator.validate(schema, this);
		}
	}

	/**
	 * Validates some data against its pre-configured data validators.
	 * 
	 * @param schema
	 *        The schema that defines this data.
	 * 
	 * @param data
	 *        The data to validate.
	 * 
	 * @throws ConcordiaException
	 *         The data is not valid.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void validate(final Schema schema, final JsonNode data)
		throws ConcordiaException {

		// Get the 'data' validators for the 'schema'.
		List<DataValidator<? extends Schema>> validators =
			dataValidators.get(schema.getClass());

		// Apply each validator to the 'data' with the 'schema'.
		// Note: Ignore the type warning here. Type-erasure makes this
		// painfully difficult, and, while the type checker cannot guarantee
		// that this is type-safe, our code should ensure that it actually is.
		for(DataValidator validator : validators) {
			validator.validate(schema, data, this);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result =
			prime *
				result +
				((dataValidators == null) ? 0 : dataValidators.hashCode());
		result =
			prime *
				result +
				((schemaValidators == null) ? 0 : schemaValidators.hashCode());
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
		if(!(obj instanceof ValidationController)) {
			return false;
		}
		ValidationController other = (ValidationController) obj;
		if(dataValidators == null) {
			if(other.dataValidators != null) {
				return false;
			}
		}
		else if(!dataValidators.equals(other.dataValidators)) {
			return false;
		}
		if(schemaValidators == null) {
			if(other.schemaValidators != null) {
				return false;
			}
		}
		else if(!schemaValidators.equals(other.schemaValidators)) {
			return false;
		}
		return true;
	}
}