package name.jenkins.paul.john.concordia.validator;

import java.util.Iterator;
import java.util.List;

import name.jenkins.paul.john.concordia.exception.ConcordiaException;
import name.jenkins.paul.john.concordia.schema.ArraySchema;
import name.jenkins.paul.john.concordia.schema.Schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;

/**
 * <p>
 * The required validator for array schemas.
 * </p>
 * 
 * @author John Jenkins
 */
public class ArrayValidator
	implements
	SchemaValidator<ArraySchema>,
	DataValidator<ArraySchema> {

	/**
	 * Validates that a schema for an array is valid.
	 */
	@Override
	public void validate(
		final ArraySchema schema,
		final ValidationController controller)
		throws ConcordiaException {

		// If it's a constant type array, validate the sub-schema.
		if(schema.getConstType() != null) {
			controller.validate(schema.getConstType());
		}
		// Otherwise, it must be a constant-length array in which case we
		// validate each of the indices' schema.
		else {
			for(Schema indexSchema : schema.getConstLength()) {
				if(indexSchema == null) {
					throw
						new ConcordiaException("An index's schema was null.");
				}
				
				controller.validate(indexSchema);
			}
		}
	}

	/**
	 * Validates that every data point in an array complies with its applicable
	 * schema.
	 */
	@Override
	public void validate(
		ArraySchema schema,
		JsonNode data,
		ValidationController controller) throws ConcordiaException {

		// If it is not an array,
		if(!(data instanceof ArrayNode)) {
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
					"The data was not a string value: " + data.toString());
			}
		}
		
		// Get the data as an array.
		ArrayNode dataArray = (ArrayNode) data;
		
		// If it's a constant type array, validate that each index in the data
		// array conforms to this schema.
		if(schema.getConstType() != null) {
			// Get the sub-schema.
			Schema subSchema = schema.getConstType();
			
			// Validate each of the indices.
			Iterator<JsonNode> dataArrayIter = dataArray.iterator();
			while(dataArrayIter.hasNext()) {
				controller.validate(subSchema, dataArrayIter.next());
			}
		}
		// Otherwise, it's a constant length array, and we need to validate
		// that both lists are the same length and that the data in each index
		// of the data list conforms to its corresponding schema in the schema
		// list.
		else {
			// Get the sub-schema list.
			List<Schema> subSchemas = schema.getConstLength();
			
			// Validate that the schemas are the same length.
			if(subSchemas.size() != dataArray.size()) {
				throw
					new ConcordiaException(
						"The schemas array and the data array are different " +
							"lengths.");
			}
			
			// Get an iterator for the data array.
			Iterator<JsonNode> dataArrayIter = dataArray.iterator();
			
			// Cycle through the schemas, each time guaranteeing that the
			// corresponding data matches the schema.
			for(Schema indexSchema : schema.getConstLength()) {
				controller.validate(indexSchema, dataArrayIter.next());
			}
		}
	}
}