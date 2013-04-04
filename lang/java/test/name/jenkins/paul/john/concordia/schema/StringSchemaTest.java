package name.jenkins.paul.john.concordia.schema;

import name.jenkins.paul.john.concordia.exception.ConcordiaException;

import org.junit.Assert;
import org.junit.Test;

/**
 * <p>
 * This class is responsible for testing everything specific to
 * {@link StringSchema}s.
 *</p>
 *
 * @author John Jenkins
 */
public class StringSchemaTest {
	/**
	 * Test that a {@link StringSchema} can be created.
	 * 
	 * @throws ConcordiaException This should not be thrown.
	 */
	@Test
	public void testStringSchema() {
		new StringSchema(null, false, null);
	}
	
	/**
	 * Test that a {@link StringSchema} is created with documentation.
	 * 
	 * @throws ConcordiaException This should not be thrown.
	 */
	@Test
	public void testStringSchemaDoc() {
		Schema schema = new StringSchema(SchemaTest.TEST_DOC, false, null);
		Assert.assertEquals(schema.getDoc(), SchemaTest.TEST_DOC);
	}
	
	/**
	 * Test that a {@link StringSchema} is created as optional.
	 * 
	 * @throws ConcordiaException This should not be thrown.
	 */
	@Test
	public void testStringSchemaOptionalTrue() {
		Schema schema =
			new StringSchema(null, SchemaTest.TEST_OPTIONAL_TRUE, null);
		Assert
			.assertEquals(schema.isOptional(), SchemaTest.TEST_OPTIONAL_TRUE);
	}
	
	/**
	 * Test that a {@link StringSchema} is created as not optional.
	 * 
	 * @throws ConcordiaException This should not be thrown.
	 */
	@Test
	public void testStringSchemaOptionalFalse() {
		Schema schema =
			new StringSchema(null, SchemaTest.TEST_OPTIONAL_FALSE, null);
		Assert
			.assertEquals(schema.isOptional(), SchemaTest.TEST_OPTIONAL_FALSE);
	}
	
	/**
	 * Test that a {@link StringSchema} is created with a name.
	 * 
	 * @throws ConcordiaException This should not be thrown.
	 */
	@Test
	public void testStringSchemaName() {
		Schema schema =
			new StringSchema(null, false, SchemaTest.TEST_NAME_NOT_OPTIONAL);
		Assert
			.assertEquals(schema.getName(), SchemaTest.TEST_NAME_NOT_OPTIONAL);
	}
}