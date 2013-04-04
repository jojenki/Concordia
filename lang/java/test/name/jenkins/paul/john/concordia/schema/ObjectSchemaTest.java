package name.jenkins.paul.john.concordia.schema;

import java.util.List;

import name.jenkins.paul.john.concordia.exception.ConcordiaException;

import org.junit.Assert;
import org.junit.Test;

/**
 * <p>
 * This class is responsible for testing everything specific to
 * {@link ObjectSchema}s.
 *</p>
 *
 * @author John Jenkins
 */
public class ObjectSchemaTest {
	/**
	 * Make sure an exception is thrown when null is used to create an object
	 * schema.
	 * 
	 * @throws ConcordiaException This should be thrown.
	 */
	@Test(expected = ConcordiaException.class)
	public void testObjectSchemaNull() throws ConcordiaException {
		new ObjectSchema(null, false, null, null);
	}
	
	/**
	 * Test that an object schema can be created.
	 * 
	 * @throws ConcordiaException This should not be thrown.
	 */
	@Test
	public void testObjectSchema() throws ConcordiaException {
		new ObjectSchema(null, false, null, SchemaTest.TEST_SCHEMA_LIST_BOTH);
	}
	
	/**
	 * Test that a constant-type {@link ArraySchema} is created with 
	 * documentation.
	 * 
	 * @throws ConcordiaException This should not be thrown.
	 */
	@Test
	public void testArraySchemaListDoc() throws ConcordiaException {
		Schema schema =
			new ObjectSchema(
				SchemaTest.TEST_DOC,
				false,
				null,
				SchemaTest.TEST_SCHEMA_LIST_BOTH);
		Assert.assertEquals(schema.getDoc(), SchemaTest.TEST_DOC);
	}
	
	/**
	 * Test that a constant-type {@link ArraySchema} is created with 
	 * documentation.
	 * 
	 * @throws ConcordiaException This should not be thrown.
	 */
	@Test
	public void testArraySchemaListOptionalTrue() throws ConcordiaException {
		Schema schema =
			new ObjectSchema(
				null,
				SchemaTest.TEST_OPTIONAL_TRUE,
				null,
				SchemaTest.TEST_SCHEMA_LIST_BOTH);
		Assert
			.assertEquals(schema.isOptional(), SchemaTest.TEST_OPTIONAL_TRUE);
	}
	
	/**
	 * Test that a constant-type {@link ArraySchema} is created with 
	 * documentation.
	 * 
	 * @throws ConcordiaException This should not be thrown.
	 */
	@Test
	public void testArraySchemaListOptionalFalse()
		throws ConcordiaException {
		
		Schema schema =
			new ObjectSchema(
				null,
				SchemaTest.TEST_OPTIONAL_FALSE,
				null,
				SchemaTest.TEST_SCHEMA_LIST_BOTH);
		Assert
			.assertEquals(schema.isOptional(), SchemaTest.TEST_OPTIONAL_FALSE);
	}
	
	/**
	 * Test that a constant-type {@link ArraySchema} is created with 
	 * documentation.
	 * 
	 * @throws ConcordiaException This should not be thrown.
	 */
	@Test
	public void testArraySchemaListName() throws ConcordiaException {
		Schema schema =
			new ObjectSchema(
				null,
				false,
				SchemaTest.TEST_NAME_NOT_OPTIONAL,
				SchemaTest.TEST_SCHEMA_LIST_BOTH);
		Assert
			.assertEquals(schema.getName(), SchemaTest.TEST_NAME_NOT_OPTIONAL);
	}

	/**
	 * Test that the correct fields are returned.
	 * 
	 * @throws ConcordiaException This should not be thrown.
	 */
	@Test
	public void testGetFields() throws ConcordiaException {
		ObjectSchema schema =
			new ObjectSchema(
				null,
				false,
				null,
				SchemaTest.TEST_SCHEMA_LIST_BOTH);
		
		Assert.assertNotNull(schema.getFields());
		List<Schema> fields = schema.getFields();

		Assert.assertEquals(fields.get(0), SchemaTest.TEST_SCHEMA_OPTIONAL);
		Assert
			.assertEquals(fields.get(1), SchemaTest.TEST_SCHEMA_NOT_OPTIONAL);
	}

	/**
	 * Test that the correct fields' names are returned.
	 * 
	 * @throws ConcordiaException This should not be thrown.
	 */
	@Test
	public void testGetFieldNames() throws ConcordiaException {
		ObjectSchema schema =
			new ObjectSchema(
				null,
				false,
				null,
				SchemaTest.TEST_SCHEMA_LIST_BOTH);
		
		Assert.assertNotNull(schema.getFieldNames());
		List<String> fields = schema.getFieldNames();

		Assert.assertEquals(fields.get(0), SchemaTest.TEST_NAME_OPTIONAL);
		Assert.assertEquals(fields.get(1), SchemaTest.TEST_NAME_NOT_OPTIONAL);
	}
}