package name.jenkins.paul.john.concordia.schema;

import name.jenkins.paul.john.concordia.exception.ConcordiaException;

import org.junit.Assert;
import org.junit.Test;

/**
 * <p>
 * This class is responsible for testing everything specific to
 * {@link BooleanSchema}s.
 *</p>
 *
 * @author John Jenkins
 */
public class BooleanSchemaTest {
	/**
	 * Test that a {@link BooleanSchema} can be created.
	 * 
	 * @throws ConcordiaException This should not be thrown.
	 */
	@Test
	public void testBooleanSchema() {
		new BooleanSchema(null, false, null);
	}
	
	/**
	 * Test that a {@link BooleanSchema} is created with documentation.
	 * 
	 * @throws ConcordiaException This should not be thrown.
	 */
	@Test
	public void testBooleanSchemaDoc() {
		Schema schema = new BooleanSchema(SchemaTest.TEST_DOC, false, null);
		Assert.assertEquals(schema.getDoc(), SchemaTest.TEST_DOC);
	}
	
	/**
	 * Test that a {@link BooleanSchema} is created as optional.
	 * 
	 * @throws ConcordiaException This should not be thrown.
	 */
	@Test
	public void testBooleanSchemaOptionalTrue() {
		Schema schema =
			new BooleanSchema(null, SchemaTest.TEST_OPTIONAL_TRUE, null);
		Assert
			.assertEquals(schema.isOptional(), SchemaTest.TEST_OPTIONAL_TRUE);
	}
	
	/**
	 * Test that a {@link BooleanSchema} is created as not optional.
	 * 
	 * @throws ConcordiaException This should not be thrown.
	 */
	@Test
	public void testBooleanSchemaOptionalFalse() {
		Schema schema =
			new BooleanSchema(null, SchemaTest.TEST_OPTIONAL_FALSE, null);
		Assert
			.assertEquals(schema.isOptional(), SchemaTest.TEST_OPTIONAL_FALSE);
	}
	
	/**
	 * Test that a {@link BooleanSchema} is created with a name.
	 * 
	 * @throws ConcordiaException This should not be thrown.
	 */
	@Test
	public void testBooleanSchemaName() {
		Schema schema =
			new BooleanSchema(null, false, SchemaTest.TEST_NAME_NOT_OPTIONAL);
		Assert
			.assertEquals(schema.getName(), SchemaTest.TEST_NAME_NOT_OPTIONAL);
	}
}