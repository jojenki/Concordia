package name.jenkins.paul.john.concordia.validator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import name.jenkins.paul.john.concordia.exception.ConcordiaException;
import name.jenkins.paul.john.concordia.schema.ObjectSchema;
import name.jenkins.paul.john.concordia.schema.ReferenceSchema;
import name.jenkins.paul.john.concordia.schema.Schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * <p>
 * The required validator for object schemas.
 * </p>
 *
 * @author John Jenkins
 */
public class ObjectValidator
	implements SchemaValidator<ObjectSchema>, DataValidator<ObjectSchema> {

	/**
	 * Validates that a schema for an object is valid.
	 */
	@Override
	public void validate(
		final ObjectSchema schema,
		final ValidationController controller) throws ConcordiaException {

		// First, ensure that a list of fields was given.
		List<Schema> fields = schema.getFields();
		if(fields == null) {
			throw new ConcordiaException("The list of fields, '" +
				ObjectSchema.JSON_KEY_FIELDS +
				"', was not supplied: " +
				schema.toString());
		}

		// Create a list of names, to use to guarantee that a name is not
		// duplicated.
		Set<String> names = new HashSet<String>();

		// Cycle through the list of fields and validate them.
		for(Schema field : fields) {
			// Check to be sure the field is not null.
			if(field == null) {
				throw new ConcordiaException("A field was null: " +
					schema.toString());
			}

			// Validate the field.
			controller.validate(field);

			// Get the name of the field.
			String name = field.getName();
			// The name may be omitted if a reference was given.
			if(name == null) {
				// If the field is a reference type, validate that it also
			    // defines an object and that the object shares no field names
			    // with this object.
				if(field instanceof ReferenceSchema) {
					// Get the names for this field.
					for(String refFieldName :
					    ((ReferenceSchema) field).getFieldNames()) {

						if(!names.add(refFieldName)) {
							throw new ConcordiaException(
								"Multiple fields have the same name, '" +
									name +
									"': " +
									field.toString());
						}
					}
				}
				// Otherwise, this is just a field without a name.
				else {
					throw new ConcordiaException("The field is missing a '" +
						ObjectSchema.JSON_KEY_NAME +
						"' field: " +
						field.toString());
				}
			}
			// Make sure the name does not already exist.
			else {
				if(!names.add(name)) {
					throw new ConcordiaException(
						"Multiple fields have the same name, '" +
							name +
							"': " +
							field.toString());
				}
			}
		}
	}

	/**
	 * Validates that some data conforms to its object schema.
	 */
	@Override
	public void validate(
		final ObjectSchema schema,
		final JsonNode data,
		final ValidationController controller) throws ConcordiaException {

		// If it is not an object,
		if(!(data instanceof ObjectNode)) {
			// If it is null, then it must be optional.
			if((data == null) || (data instanceof NullNode)) {
				if(schema.isOptional()) {
					return;
				}
				else {
					throw new ConcordiaException(
						"The value is null but not optional: " +
							schema.toString());
				}
			}
			// Otherwise, it is invalid.
			else {
				throw new ConcordiaException(
					"The data was not an object value: " + data.toString());
			}
		}

		// Get the data object node.
		ObjectNode dataObject = (ObjectNode) data;

		// For each of the fields in the definition, validate them.
		for(Schema fieldDefinition : schema.getFields()) {
			// If it doesn't have a field name, then it must be a referenced
			// schema, so we need to pass that on to the controller.
			if(fieldDefinition.getName() == null) {
				controller.validate(fieldDefinition, dataObject);
			}
			// If it has a field name then we continue with the controller.
			else {
				// Validate the field.
				controller
					.validate(
						fieldDefinition,
						dataObject.get(fieldDefinition.getName()));
			}
		}
	}
}