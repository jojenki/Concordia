package name.jenkins.paul.john.concordia.schema;

import java.util.List;

import name.jenkins.paul.john.concordia.exception.ConcordiaException;

import org.junit.Assert;
import org.junit.Test;

/**
 * <p>
 * This class is responsible for testing everything specific to
 * {@link ArraySchema}s.
 *</p>
 *
 * @author John Jenkins
 */
public class ArraySchemaTest {
	/**
	 * Make sure an exception is thrown when null is used to create a
	 * constant-type array.
	 * 
	 * @throws ConcordiaException This should be thrown.
	 */
	@Test(expected = ConcordiaException.class)
	public void testArraySchemaSchemaNull() throws ConcordiaException {
		new ArraySchema(null, false, null, (Schema) null);
	}

	/**
	 * Test creating a valid, constant-type array schema from the test child
	 * schema.
	 * 
	 * @throws ConcordiaException This should not be thrown.
	 */
	@Test
	public void testArraySchemaSchema() throws ConcordiaException {
		new ArraySchema(
			null, 
			false, 
			null, 
			SchemaTest.TEST_SCHEMA_NOT_OPTIONAL);
	}
	
	/**
	 * Test that a constant-type {@link ArraySchema} is created with 
	 * documentation.
	 * 
	 * @throws ConcordiaException This should not be thrown.
	 */
	@Test
	public void testArraySchemaSchemaDoc() throws ConcordiaException {
		Schema schema =
			new ArraySchema(
				SchemaTest.TEST_DOC,
				false,
				null,
				SchemaTest.TEST_SCHEMA_NOT_OPTIONAL);
		Assert.assertEquals(schema.getDoc(), SchemaTest.TEST_DOC);
	}
	
	/**
	 * Test that a constant-type {@link ArraySchema} is created with 
	 * documentation.
	 * 
	 * @throws ConcordiaException This should not be thrown.
	 */
	@Test
	public void testArraySchemaSchemaOptionalTrue() throws ConcordiaException {
		Schema schema =
			new ArraySchema(
				null,
				SchemaTest.TEST_OPTIONAL_TRUE,
				null,
				SchemaTest.TEST_SCHEMA_NOT_OPTIONAL);
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
	public void testArraySchemaSchemaOptionalFalse()
		throws ConcordiaException {
		
		Schema schema =
			new ArraySchema(
				null,
				SchemaTest.TEST_OPTIONAL_FALSE,
				null,
				SchemaTest.TEST_SCHEMA_NOT_OPTIONAL);
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
	public void testArraySchemaSchemaName() throws ConcordiaException {
		Schema schema =
			new ArraySchema(
				null,
				false,
				SchemaTest.TEST_NAME_NOT_OPTIONAL,
				SchemaTest.TEST_SCHEMA_NOT_OPTIONAL);
		Assert
			.assertEquals(schema.getName(), SchemaTest.TEST_NAME_NOT_OPTIONAL);
	}

	/**
	 * Make sure an exception is thrown when null is used to create a
	 * constant-length array.
	 * 
	 * @throws ConcordiaException This should be thrown.
	 */
	@Test(expected = ConcordiaException.class)
	public void testArraySchemaListNull() throws ConcordiaException {
		new ArraySchema(null, false, null, (List<Schema>) null);
	}

	/**
	 * Test creating a valid, constant-length array schema from the test child
	 * schema.
	 * 
	 * @throws ConcordiaException This should not be thrown.
	 */
	@Test
	public void testArraySchemaList() throws ConcordiaException {
		new ArraySchema(null, false, null, SchemaTest.TEST_SCHEMA_LIST_BOTH);
	}
	
	/**
	 * Test that a constant-length {@link ArraySchema} is created with 
	 * documentation.
	 * 
	 * @throws ConcordiaException This should not be thrown.
	 */
	@Test
	public void testArraySchemaListDoc() throws ConcordiaException {
		Schema schema =
			new ArraySchema(
				SchemaTest.TEST_DOC,
				false,
				null,
				SchemaTest.TEST_SCHEMA_LIST_BOTH);
		Assert.assertEquals(schema.getDoc(), SchemaTest.TEST_DOC);
	}
	
	/**
	 * Test that a constant-length {@link ArraySchema} is created with 
	 * documentation.
	 * 
	 * @throws ConcordiaException This should not be thrown.
	 */
	@Test
	public void testArraySchemaListOptionalTrue() throws ConcordiaException {
		Schema schema =
			new ArraySchema(
				null,
				SchemaTest.TEST_OPTIONAL_TRUE,
				null,
				SchemaTest.TEST_SCHEMA_LIST_BOTH);
		Assert
			.assertEquals(schema.isOptional(), SchemaTest.TEST_OPTIONAL_TRUE);
	}
	
	/**
	 * Test that a constant-length {@link ArraySchema} is created with 
	 * documentation.
	 * 
	 * @throws ConcordiaException This should not be thrown.
	 */
	@Test
	public void testArraySchemaListOptionalFalse()
		throws ConcordiaException {
		
		Schema schema =
			new ArraySchema(
				null,
				SchemaTest.TEST_OPTIONAL_FALSE,
				null,
				SchemaTest.TEST_SCHEMA_LIST_BOTH);
		Assert
			.assertEquals(schema.isOptional(), SchemaTest.TEST_OPTIONAL_FALSE);
	}
	
	/**
	 * Test that a constant-length {@link ArraySchema} is created with 
	 * documentation.
	 * 
	 * @throws ConcordiaException This should not be thrown.
	 */
	@Test
	public void testArraySchemaListName() throws ConcordiaException {
		Schema schema =
			new ArraySchema(
				null,
				false,
				SchemaTest.TEST_NAME_NOT_OPTIONAL,
				SchemaTest.TEST_SCHEMA_LIST_BOTH);
		Assert
			.assertEquals(schema.getName(), SchemaTest.TEST_NAME_NOT_OPTIONAL);
	}

	/**
	 * Test creating a constant-type schema and then be sure that the
	 * constant-type schema is returned and is the same as the one we gave it.
	 * Also, be sure that the constant-length schema is null.
	 *  
	 * @throws ConcordiaException This should not be thrown.
	 */
	@Test
	public void testGetConstType() throws ConcordiaException {
		ArraySchema arraySchema =
			new ArraySchema(
				null,
				false,
				null,
				SchemaTest.TEST_SCHEMA_NOT_OPTIONAL);
		
		Assert
			.assertEquals(
				arraySchema.getConstType(),
				SchemaTest.TEST_SCHEMA_NOT_OPTIONAL);
		Assert.assertNull(arraySchema.getConstLength());
	}

	/**
	 * Test creating a constant-length schema and then be sure that the
	 * constant-length schema is returned and is the same as the one we gave
	 * it. Also, be sure that the constant-type schema is null.
	 *  
	 * @throws ConcordiaException This should not be thrown.
	 */
	@Test
	public void testGetConstLength() throws ConcordiaException {
		ArraySchema schemaArray =
			new ArraySchema(
				null,
				false,
				null,
				SchemaTest.TEST_SCHEMA_LIST_BOTH);
		
		Assert
			.assertEquals(
				schemaArray.getConstLength(),
				SchemaTest.TEST_SCHEMA_LIST_BOTH);
		Assert.assertNull(schemaArray.getConstType());
	}
}