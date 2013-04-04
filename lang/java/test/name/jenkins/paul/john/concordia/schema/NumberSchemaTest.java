package name.jenkins.paul.john.concordia.schema;

import name.jenkins.paul.john.concordia.exception.ConcordiaException;

import org.junit.Assert;
import org.junit.Test;

/**
 * <p>
 * This class is responsible for testing everything specific to
 * {@link NumberSchema}s.
 *</p>
 *
 * @author John Jenkins
 */
public class NumberSchemaTest {
	/**
	 * Test that a {@link NumberSchema} can be created.
	 * 
	 * @throws ConcordiaException This should not be thrown.
	 */
	@Test
	public void testNumberSchema() {
		new NumberSchema(null, false, null);
	}
	
	/**
	 * Test that a {@link NumberSchema} is created with documentation.
	 * 
	 * @throws ConcordiaException This should not be thrown.
	 */
	@Test
	public void testNumberSchemaDoc() {
		Schema schema = new NumberSchema(SchemaTest.TEST_DOC, false, null);
		Assert.assertEquals(schema.getDoc(), SchemaTest.TEST_DOC);
	}
	
	/**
	 * Test that a {@link NumberSchema} is created as optional.
	 * 
	 * @throws ConcordiaException This should not be thrown.
	 */
	@Test
	public void testNumberSchemaOptionalTrue() {
		Schema schema =
			new NumberSchema(null, SchemaTest.TEST_OPTIONAL_TRUE, null);
		Assert
			.assertEquals(schema.isOptional(), SchemaTest.TEST_OPTIONAL_TRUE);
	}
	
	/**
	 * Test that a {@link NumberSchema} is created as not optional.
	 * 
	 * @throws ConcordiaException This should not be thrown.
	 */
	@Test
	public void testNumberSchemaOptionalFalse() {
		Schema schema =
			new NumberSchema(null, SchemaTest.TEST_OPTIONAL_FALSE, null);
		Assert
			.assertEquals(schema.isOptional(), SchemaTest.TEST_OPTIONAL_FALSE);
	}
	
	/**
	 * Test that a {@link NumberSchema} is created with a name.
	 * 
	 * @throws ConcordiaException This should not be thrown.
	 */
	@Test
	public void testNumberSchemaName() {
		Schema schema =
			new NumberSchema(null, false, SchemaTest.TEST_NAME_NOT_OPTIONAL);
		Assert
			.assertEquals(schema.getName(), SchemaTest.TEST_NAME_NOT_OPTIONAL);
	}
}