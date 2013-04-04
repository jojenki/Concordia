package name.jenkins.paul.john.concordia.schema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Ignore;

/**
 * <p>
 * This class represents all of the testing information related to testing
 * {@link @Schema}s.
 * </p>
 *
 * @author John Jenkins
 */
@Ignore
public class SchemaTest {
	/**
	 * A sample documentation value.
	 */
	public static final String TEST_DOC = "doc";
	/**
	 * The flag to make a schema optional.
	 */
	public static final boolean TEST_OPTIONAL_TRUE = true;
	/**
	 * The flag to make a schema not optional.
	 */
	public static final boolean TEST_OPTIONAL_FALSE = false;
	/**
	 * A sample name for an optional field.
	 * 
	 * Whether or not a schema is optional has no bearing on the name, but our
	 * sample list that contains both the optional and non-optional schemas
	 * requires that the names on the two schemas be different.
	 */
	public static final String TEST_NAME_OPTIONAL = "name_optional";
	/**
	 * A sample name for a non-optional field.
	 * 
	 * Whether or not a schema is optional has no bearing on the name, but our
	 * sample list that contains both the optional and non-optional schemas
	 * requires that the names on the two schemas be different.
	 */
	public static final String TEST_NAME_NOT_OPTIONAL = "name_non_optional";
	
	/**
	 * A testing schema that is optional.
	 */
	public static final Schema TEST_SCHEMA_OPTIONAL =
		new BooleanSchema(TEST_DOC, TEST_OPTIONAL_TRUE, TEST_NAME_OPTIONAL);
	/**
	 * A testing schema list that contains only the optional schema.
	 */
	public static final List<Schema> TEST_SCHEMA_LIST_OPTIONAL;
	static {
		List<Schema> list = new ArrayList<Schema>(1);
		list.add(TEST_SCHEMA_OPTIONAL);
		TEST_SCHEMA_LIST_OPTIONAL = Collections.unmodifiableList(list);
	}
	/**
	 * A testing schema that is not optional.
	 */
	public static final Schema TEST_SCHEMA_NOT_OPTIONAL =
		new BooleanSchema(
				TEST_DOC,
				TEST_OPTIONAL_FALSE,
				TEST_NAME_NOT_OPTIONAL);
	/**
	 * A testing schema list that contains only the non-optional schema.
	 */
	public static final List<Schema> TEST_SCHEMA_LIST_NOT_OPTIONAL;
	static {
		List<Schema> list = new ArrayList<Schema>(1);
		list.add(TEST_SCHEMA_NOT_OPTIONAL);
		TEST_SCHEMA_LIST_NOT_OPTIONAL = Collections.unmodifiableList(list);
	}
	/**
	 * A testing schema list that contains both the optional and non-optional
	 * schemas.
	 */
	public static final List<Schema> TEST_SCHEMA_LIST_BOTH;
	static {
		List<Schema> list = new ArrayList<Schema>(2);
		list.add(TEST_SCHEMA_OPTIONAL);
		list.add(TEST_SCHEMA_NOT_OPTIONAL);
		TEST_SCHEMA_LIST_BOTH = Collections.unmodifiableList(list);
	}
}