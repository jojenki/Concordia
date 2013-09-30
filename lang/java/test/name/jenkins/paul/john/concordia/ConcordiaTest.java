package name.jenkins.paul.john.concordia;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import name.jenkins.paul.john.concordia.exception.ConcordiaException;
import name.jenkins.paul.john.concordia.schema.ArraySchema;
import name.jenkins.paul.john.concordia.schema.BooleanSchema;
import name.jenkins.paul.john.concordia.schema.NumberSchema;
import name.jenkins.paul.john.concordia.schema.ObjectSchema;
import name.jenkins.paul.john.concordia.schema.Schema;
import name.jenkins.paul.john.concordia.schema.StringSchema;
import name.jenkins.paul.john.concordia.validator.CustomValidator;
import name.jenkins.paul.john.concordia.validator.ValidationController;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingJsonFactory;

/**
 * <p>
 * The main testing class. This class is responsible for testing everything
 * related to the {@link Concordia} class.
 * </p>
 * 
 * <p>
 * {@link ConcordiaException}s should always be caught by JUnit. Any that are
 * not caught should be considered bugs.
 * </p>
 *
 * @author John Jenkins
 */
public class ConcordiaTest {
	/**
	 * A filter that will only return directories.
	 *
	 * @author John Jenkins
	 */
	private static final class DirectoryFilter implements FileFilter {
		/*
		 * (non-Javadoc)
		 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
		 */
		@Override
		public boolean accept(final File file) {
			if(file.isDirectory()) {
				return true;
			}
			else {
				return false;
			}
		}
	}
	/**
	 * The filter for getting the sub-directories from a directory.
	 */
	private static final FileFilter DIRECTORY_FILTER = new DirectoryFilter();
	
	/**
	 * A filter that will be sure to only return files that end with ".json".
	 *
	 * @author John Jenkins
	 */
	private static final class JsonFilenameFilter implements FilenameFilter {
		/*
		 * (non-Javadoc)
		 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
		 */
		@Override
		public boolean accept(final File directory, final String name) {
			if(name.endsWith(".json")) {
				return true;
			}
			else {
				return false;
			}
		}
	}
	/**
	 * The filter for getting the JSON files from a directory.
	 */
	private static final FilenameFilter JSON_FILENAME_FILTER =
		new JsonFilenameFilter();
	
	/**
	 * The JSON factory to use to parse the data to be validated.
	 */
	private static final JsonFactory JSON_FACTORY = new MappingJsonFactory();
	
	/**
	 * The local server used to create remote schemas.
	 */
	private static StaticServer server;
	
	/**
	 * Create the testing environment.
	 */
	@BeforeClass
	public static void init() throws ConcordiaException {
		// Start up the server we are going to use.
		server = new StaticServer(StaticServer.DEFAULT_PORT);
	}
	
	/**
	 * Shut down the testing environment.
	 */
	@AfterClass
	public static void shutdown() {
		// Stop the server.
		server.shutdown();
	}

	/**
	 * Tests the valid schemas to ensure that a proper object is created.
	 */
	@Test
	public void testSchemasValid() {
		// Get the valid schemas.
		File rootDirectory = new File("./test/definition/valid");
		// Verify that all of the schemas can be created.
		testSchemas(
			Arrays.asList(rootDirectory.listFiles(JSON_FILENAME_FILTER)),
			true);
	}
	
	/**
	 * Tests the invalid schemas to ensure that they are caught.
	 */
	@Test
	public void testSchemasInvalid() {
		// Get the directory listing of invalid schema directories.
		File rootDirectory = new File("./test/definition/invalid");
		File[] invalidDirectories =
			rootDirectory.listFiles(DIRECTORY_FILTER);
		
		// Get all of the invalid schemas from the invalid schema directories.
		List<File> invalidFiles = new ArrayList<File>();
		for(int i = 0; i < invalidDirectories.length; i++) {
			invalidFiles
				.addAll(
					Arrays
						.asList(
							invalidDirectories[i]
								.listFiles(JSON_FILENAME_FILTER)));
		}
		
		// Verify that all of the schemas fail validation.
		testSchemas(invalidFiles, false);
	}
	
	/**
	 * Verify that the decorated schemas are valid and that their decorated
	 * field is also deserialized.
	 */
	@Test
	public void testSchemaDecorators() throws ConcordiaException {
		testDecorator(
			"./test/definition/decorator/boolean.json",
			BooleanSchema.class);
		testDecorator(
			"./test/definition/decorator/number.json",
			NumberSchema.class);
		testDecorator(
			"./test/definition/decorator/string.json",
			StringSchema.class);
		testDecorator(
			"./test/definition/decorator/object.json",
			ObjectSchema.class);
		testDecorator(
			"./test/definition/decorator/array.json",
			ArraySchema.class);
	}
	
	/**
	 * Verify that a referenced schema is properly retrieved and validated.
	 */
	@Test
	public void testReferences()
		throws ConcordiaException, FileNotFoundException, IOException {
		
		// Test with a file-not-found status code.
		server.setResponseCode(404);
		try {
			new Concordia(
				new FileInputStream(
					new File(
						"./test/definition/reference/base_local.json")),
				ValidationController.BASIC_CONTROLLER);
			
			fail(
				"The server returned a 404 status code, but the Concordia " +
					"object was still built.");
		}
		catch(ConcordiaException e) {
			// Pass
		}
		
		// Test with no response text.
		server.setResponseCode(200);
		server.setResponse("");
		try {
			new Concordia(
				new FileInputStream(
					new File(
						"./test/definition/reference/base_local.json")),
				ValidationController.BASIC_CONTROLLER);
			
			fail(
				"The server returned an empty response, but the Concordia " +
					"object was still built.");
		}
		catch(ConcordiaException e) {
			// Pass
		}
		
		// Test with no response text.
		server.setResponseCode(200);
		try {
			new Concordia(
				new FileInputStream(
					new File(
						"./test/definition/reference/base_local.json")),
				ValidationController.BASIC_CONTROLLER);
			
			fail(
				"The server returned an empty response, but the Concordia " +
					"object was still built.");
		}
		catch(ConcordiaException e) {
			// Pass
		}
		
		// Test with a non-JSON response.
		server.setResponse("Hello, world!");
		try {
			new Concordia(
				new FileInputStream(
					new File(
						"./test/definition/reference/base_local.json")),
				ValidationController.BASIC_CONTROLLER);
			
			fail(
				"The server returned a non-JSON response, but the Concordia " +
					"object was still built.");
		}
		catch(ConcordiaException e) {
			// Pass
		}
		
		// Test with valid local and remote schemas.
		server
			.setResponse(
				getFileContents(
					"./test/definition/reference/base_remote.json"));
		try {
			new Concordia(
				new FileInputStream(
					new File(
						"./test/definition/reference/base_local.json")),
				ValidationController.BASIC_CONTROLLER);
		}
		catch(ConcordiaException e) {
			fail(
				"Valid local and remote schemas were used, but an exception " +
					"was still thrown: " +
					e.toString());
		}
		
		// Test with valid schemas where the local schema has a sub-object that
        // is defined by a remote schema.
		server
			.setResponse(
				getFileContents("./test/definition/reference/base_remote.json"));
        try {
            new Concordia(
            	new FileInputStream(
					new File(
						"./test/definition/reference/object_sub_local.json")),
				ValidationController.BASIC_CONTROLLER);
        }
		catch(ConcordiaException e) {
			fail(
                "A valid local schema with a sub-schema and valid remote " +
                    "schema were given, but an exception was still thrown: " +
                    e.toString());
        }
        
        // Test with valid schemas where the extended schema has a field with a
        // different name but the same type.
        server
			.setResponse(
				getFileContents(
					"./test/definition/reference/object_extend_remote.json"));
        try {
        	new Concordia(
            	new FileInputStream(
					new File("./test/definition/reference/object_extend_local.json")),
				ValidationController.BASIC_CONTROLLER);
        }
		catch(ConcordiaException e) {
			fail(
                "A valid local and remote schema were given with different " +
                    "field names but the same type, and an exception was " +
                    "thrown: " +
                    e.toString());
        }
        
        // Test with valid schemas where the extended schema has a field with
        // the same name as the base schema.
        server
			.setResponse(
				getFileContents(
                    "./test/definition/reference/object_extend_remote_duplicate_name.json"));
        try {
            new Concordia(
            	new FileInputStream(
					new File("./test/definition/reference/object_extend_local.json")),
				ValidationController.BASIC_CONTROLLER);
        
            fail(
                "A remote schema had an identical name to the schema " +
                    "extending it, but the Concordia object was built " +
                    "nevertheless.");
        }
		catch(ConcordiaException e) {
			// Pass.
        }
        
        // Test with valid schemas where the extended schema is an object and
        // the base schema is an array.
        server
			.setResponse(
				getFileContents(
                    "./test/definition/reference/object_extend_remote_not_object.json"));
        try { 
            new Concordia(
            	new FileInputStream(
					new File("./test/definition/reference/object_extend_local.json")),
				ValidationController.BASIC_CONTROLLER);
            
            fail(
                "A remote schema was an array while the local schema was " +
                    "an object, but the Concordia object was built " +
                    "nevertheless.");
        }
		catch(ConcordiaException e) {
			// Pass.
        }
        
        // Test with valid schemas where the local schema is an array. It
        // shouldn't matter what the sub-schema is.
        server
			.setResponse(
				getFileContents("./test/definition/reference/base_remote.json"));
        try { 
            new Concordia(
            	new FileInputStream(
					new File("./test/definition/reference/array_const_length.json")),
				ValidationController.BASIC_CONTROLLER);
        }
		catch(ConcordiaException e) {
			fail(
                "An exception was thrown for a base schema whose type was " +
                    "a constant length array, so the remote type shouldn't " +
                    "matter: " +
                    e.toString());
        }
        
        // Test with valid schemas where the local schema is an array. It
        // shouldn't matter what the sub-schema is.
        server
			.setResponse(
				getFileContents("./test/definition/reference/base_remote.json"));
        try { 
            new Concordia(
            	new FileInputStream(
					new File("./test/definition/reference/array_const_type.json")),
				ValidationController.BASIC_CONTROLLER);
        }
		catch(ConcordiaException e) {
			fail(
                "An exception was thrown for a base schema whose type was " +
                    "a constant type array, so the remote type shouldn't " +
                    "matter: " +
                    e.toString());
        }
	}
	
	/**
	 * Test all of the valid and invalid boolean data.
	 */
	@Test
	public void testDataBoolean() throws ConcordiaException {
		testDataFiles("./test/data/boolean/");
	}

	/**
	 * Test all of the valid and invalid number data.
	 */
	@Test
	public void testDataNumber() throws ConcordiaException {
		testDataFiles("./test/data/number/");
	}

	/**
	 * Test all of the valid and invalid string data.
	 */
	@Test
	public void testDataString() throws ConcordiaException {
		testDataFiles("./test/data/string/");
	}

	/**
	 * Test all of the valid and invalid object data.
	 */
	@Test
	public void testDataObject() throws ConcordiaException {
		testDataFiles("./test/data/object/");
	}

	/**
	 * Test all of the valid and invalid constant-length array data.
	 */
	@Test
	public void testDataArrayConstLength() throws ConcordiaException {
		testDataFiles("./test/data/const_length_array/");
	}

	/**
	 * Test all of the valid and invalid constant-type array data where the
	 * type is boolean.
	 */
	@Test
	public void testDataArrayConstTypeBoolean() throws ConcordiaException {
		testDataFiles("./test/data/const_type_array_boolean/");
	}

	/**
	 * Test all of the valid and invalid constant-type array data where the
	 * type is number.
	 */
	@Test
	public void testDataArrayConstTypeNumber() throws ConcordiaException {
		testDataFiles("./test/data/const_type_array_number/");
	}

	/**
	 * Test all of the valid and invalid constant-type array data where the
	 * type is string.
	 */
	@Test
	public void testDataArrayConstTypeString() throws ConcordiaException {
		testDataFiles("./test/data/const_type_array_string/");
	}

	/**
	 * Test all of the valid and invalid constant-type array data where the
	 * type is object.
	 */
	@Test
	public void testDataArrayConstTypeObject() throws ConcordiaException {
		testDataFiles("./test/data/const_type_array_object/");
	}

	/**
	 * Test all of the valid and invalid constant-type array data where the
	 * type is array.
	 */
	@Test
	public void testDataArrayConstTypeArray() throws ConcordiaException {
		testDataFiles("./test/data/const_type_array_array/");
	}
	
	/**
	 * Test the optional field.
	 */
	@Test
	public void testDataOptional() throws ConcordiaException {
		// Build the schema object.
		Concordia concordia;
		try {
			concordia =
				new Concordia(
					new FileInputStream(
						new File("./test/data/optional/definition.json")),
					ValidationController.BASIC_CONTROLLER);
		}
		// The testing structure is not valid.
		catch(FileNotFoundException e) {
			throw new ConcordiaException("The definition file is missing.", e);
		}
		// The definition could not be read.
		catch(IOException e) {
			throw
				new ConcordiaException(
					"Error reading the definition file.", 
					e);
		}
		// The definition is broken.
		catch(ConcordiaException e) {
			throw
				new ConcordiaException(
					"A valid definition file was deemed invalid.",
					e);
		}
		
		// Test the valid data.
		testData(
			concordia,
			Arrays
				.asList(
					(new File("./test/data/optional/valid/"))
						.listFiles(JSON_FILENAME_FILTER)),
			true,
			null);
	}
	
	/**
	 * Test data against referenced fields.
	 */
	@Test
	public void testDataReference() throws ConcordiaException {
		// Set the remote schema.
		server
			.setResponse(
				getFileContents("./test/data/reference/referenced.json"));
		
		// Test all of the data.
		testDataFiles("./test/data/reference/");
	}
	
	/**
	 * Test that custom validation for data runs.
	 */
	@Test
	public void testDataDecorators() throws ConcordiaException{
		testDecoratorData(
			"./test/definition/decorator/boolean.json",
			"./test/data/decorator/boolean.json",
			BooleanSchema.class);
		testDecoratorData(
			"./test/definition/decorator/number.json",
			"./test/data/decorator/number.json",
			NumberSchema.class);
		testDecoratorData(
			"./test/definition/decorator/string.json",
			"./test/data/decorator/string.json",
			StringSchema.class);
		testDecoratorData(
			"./test/definition/decorator/object.json",
			"./test/data/decorator/object.json",
			ObjectSchema.class);
		testDecoratorData(
			"./test/definition/decorator/array.json",
			"./test/data/decorator/array.json",
			ArraySchema.class);
	}

	/**
	 * Validates that all files do or don't pass validation.
	 * 
	 * @param files The set of files to check.
	 * 
	 * @param shouldPass Whether or not they should pass validation.
	 */
	@Ignore
	private void testSchemas(
		final List<File> files,
		final boolean shouldPass) {

		// For each schema, make sure a Concordia object cannot be created.
		for(File file : files) {
			try {
				new Concordia(
					new FileInputStream(file),
					ValidationController.BASIC_CONTROLLER);
			
				if(! shouldPass) {
					fail(
						"An invalid definition was created: " +
							file.getAbsolutePath());
				}
			}
			catch(FileNotFoundException e) {
				fail(
					"A file that was supposed to exist, '" +
						file.getAbsolutePath() +
						"', did not: " +
						e.toString());
			}
			catch(IOException e) {
				fail(
					"A file, '" +
						file.getAbsolutePath() +
						"', could not be read: " +
						e.toString());
			}
			catch(ConcordiaException e) {
				if(shouldPass) {
					fail(
						"A valid definition, '" +
							file.getAbsolutePath() +
							"', could not be created: " +
							e.toString());
				}
			}
		}
	}
	
	/**
	 * Using the standard testing structure, this creates a definition from the
	 * file that follows the testing convention, and then reads all of the
	 * valid and invalid data files and ensures that they pass or fail the
	 * tests, respectively.
	 * 
	 * @param directory
	 *        The core testing directory that should contain a "defintion.json"
	 *        file and two subdirectories, "valid" and "invalid".
	 */
	@Ignore
	private void testDataFiles(
		final String directory)
		throws ConcordiaException {
		
		// Build the schema object.
		Concordia concordia;
		try {
			concordia =
				new Concordia(
					new FileInputStream(
						new File(directory + "/definition.json")),
					ValidationController.BASIC_CONTROLLER);
		}
		// The testing structure is not valid.
		catch(FileNotFoundException e) {
			throw new ConcordiaException("The definition file is missing.", e);
		}
		// The definition could not be read.
		catch(IOException e) {
			throw
				new ConcordiaException(
					"Error reading the definition file.", 
					e);
		}
		// The definition is broken.
		catch(ConcordiaException e) {
			throw
				new ConcordiaException(
					"A valid definition file was deemed invalid.",
					e);
		}
		
		// Test the valid data.
		testData(
			concordia,
			Arrays
				.asList(
					(new File(directory + "/valid/"))
						.listFiles(JSON_FILENAME_FILTER)),
			true,
			null);
		
		// Test the invalid data.
		testData(
			concordia,
			Arrays
				.asList(
					(new File(directory + "/invalid/"))
						.listFiles(JSON_FILENAME_FILTER)),
			false,
			null);
	}

	/**
	 * Tests all of the data in a set of files against a schema and may or may
	 * not fail the test based on whether or not the data should or should not
	 * have been valid.
	 * 
	 * @param schema
	 *        The schema to use to validate the data.
	 * 
	 * @param dataFiles
	 *        The list of data files.
	 * 
	 * @param shouldPass
	 *        Whether or not the data in the data files should pass the tests.
	 */
	@Ignore
	private void testData(
		final Concordia schema,
		final List<File> dataFiles,
		final boolean shouldPass,
		final String expectedMessage)
		throws ConcordiaException {
		
		// Cycle through the files testing each one.
		for(File dataFile : dataFiles) {
			try {
				// Validate the data.
				schema
					.validateData(
						JSON_FACTORY
							.createParser(dataFile)
								.readValueAs(JsonNode.class));
				
				// If the data passed validation but it should have been
				// invalid, throw an exception.
				if(!shouldPass) {
					fail(
						"Invalid data ('" +
							dataFile.getAbsolutePath() +
							"') passed validation ('" +
							schema.toString() +
							"').");
				}
			}
			// There is a bug in a test regarding the syntax of some data.
			catch(JsonParseException e) {
				fail(
					"The data ('" +
						dataFile.getAbsolutePath() +
						"') was not well formed JSON: " +
						e.toString());
			}
			// There is a bug in a text regarding the syntax of some data.
			catch(JsonProcessingException e) {
				fail(
					"The data ('" +
						dataFile.getAbsolutePath() +
						"') was not well formed JSON: " +
						e.toString());
			}
			// The file could not be read!?
			catch(IOException e) {
				fail(
					"Error reading the data file ('" +
						dataFile.getAbsolutePath() +
						"'): " +
						e.toString());
			}
			// The exception thrown if the data was invalid. Check if it should
			// have failed and, if not, fail the test.
			catch(ConcordiaException e) {
				if(shouldPass) {
					fail(
						"Valid data ('" +
							dataFile.getAbsolutePath() +
							"') failed validation ('" +
							schema.toString() +
							"'): " +
							e.toString());
				}
			}
		}
	}
	
	/**
	 * Creates a custom validator for a specific type then validates some
	 * definition that implements that type.
	 * 
	 * @param definitionFile The file containing the definition.
	 * 
	 * @param type The sub-type of {@link Schema} that the definition should
	 * test.
	 * 
	 * @throws ConcordiaException There was an error creating the validator.
	 */
	@Ignore
	private void testDecorator(
		final String definitionFile, 
		final Class<? extends Schema> type)
		throws ConcordiaException {
		
		// Get the decorated definition.
		File definition = new File(definitionFile);
		
		// Create the controller and add the schema decorator for this type.
		ValidationController.Builder controllerBuilder =
			new ValidationController.Builder();
		controllerBuilder.addSchemaValidator(type, new CustomValidator());
		ValidationController controller = controllerBuilder.build();
		
		// Test to make sure the decorator did run.
		try {
			new Concordia(new FileInputStream(definition), controller);

			fail(
				"The custom validator did not run: " +
					definition.getAbsolutePath());
		}
		catch(FileNotFoundException e) {
			fail(
				"A file that was supposed to exist, '" +
					definition.getAbsolutePath() +
					"', did not: " +
					e.toString());
		}
		catch(IOException e) {
			fail(
				"A file, '" +
					definition.getAbsolutePath() +
					"', could not be read: " +
					e.toString());
		}
		catch(ConcordiaException e) {
			if(! CustomValidator.PASS.equals(e.getMessage())) {
				fail(
					"An unexpected exception was thrown for file '" +
						definition.getAbsolutePath() +
						"': " +
						e.toString());
			}
		}
	}

	/**
	 * Creates a custom data validator for a type and then uses the schema and
	 * data files to test that custom validator.
	 * 
	 * @param schemaFile
	 *        The filename of the file that contains the schema.
	 * 
	 * @param dataFile
	 *        The filename of the file that contains the data.
	 * 
	 * @param schemaClass
	 *        The schema class being tested, one of {@link Schema}.
	 */
	@Ignore
	private void testDecoratorData(
		final String schemaFile,
		final String dataFile,
		final Class<? extends Schema> schemaClass)
		throws ConcordiaException {
		
		// Get the decorated definition.
		File definition = new File(schemaFile);
		
		// Create the controller and add the schema decorator for this type.
		ValidationController.Builder controllerBuilder =
			new ValidationController.Builder();
		controllerBuilder.addDataValidator(schemaClass, new CustomValidator());
		ValidationController controller = controllerBuilder.build();
		
		// Test to make sure the decorator did run.
		Concordia schema = null;
		try {
			schema = 
				new Concordia(new FileInputStream(definition), controller);
		}
		catch(FileNotFoundException e) {
			fail(
				"A file that was supposed to exist, '" +
					definition.getAbsolutePath() +
					"', did not: " +
					e.toString());
		}
		catch(IOException e) {
			fail(
				"A file, '" +
					definition.getAbsolutePath() +
					"', could not be read: " +
					e.toString());
		}
		catch(ConcordiaException e) {
			fail(
				"An unexpected exception was thrown for file '" +
					definition.getAbsolutePath() +
					"': " +
					e.toString());
		}
		
		// Test the data.
		try {
			schema
				.validateData(
					JSON_FACTORY
						.createParser(new File(dataFile))
							.readValueAs(JsonNode.class));
			
			// If the data passed validation but it should have been invalid,
			// throw an exception.
			fail(
				"The custom data validator ('" +
					dataFile +
					"') did not run: '" +
					schema.toString());
		}
		// There is a bug in a test regarding the syntax of some data.
		catch(JsonParseException e) {
			fail(
				"The data ('" +
					dataFile +
					"') was not well formed JSON: " +
					e.toString());
		}
		// There is a bug in a text regarding the syntax of some data.
		catch(JsonProcessingException e) {
			fail(
				"The data ('" +
					dataFile +
					"') was not well formed JSON: " +
					e.toString());
		}
		// The file could not be read!?
		catch(IOException e) {
			fail(
				"Error reading the data file ('" +
					dataFile +
					"'): " +
					e.toString());
		}
		// The exception thrown if the data was invalid. Check if it si the
		// expected exception and, if not, fail the test.
		catch(ConcordiaException e) {
			if(! CustomValidator.PASS.equals(e.getMessage())) {
				fail(
					"An unexpected exception was thrown for file '" +
						dataFile +
						"': " +
						e.toString());
			}
		}
	}
	
	/**
	 * Returns the contents of a file.
	 * 
	 * @param filename
	 *        The filename of the file.
	 * 
	 * @return The contents of the file.
	 * 
	 * @throws IllegalArgumentException
	 *         The filename was null or it didn't exist.
	 * 
	 * @throws IllegalStateException
	 *         The file could not be read.
	 */
	@Ignore
	private String getFileContents(
		final String filename)
		throws IllegalArgumentException, IllegalStateException {
		
		// Get the reader for the file.
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(new File(filename)));
		}
		catch(FileNotFoundException e) {
			throw new IllegalArgumentException("The file does not exist.", e);
		}
		
		// Create the result.
		StringBuilder resultBuilder = new StringBuilder();
		
		// Read the file's contents.
		try {
			String currLine;
			while((currLine = reader.readLine()) != null) {
				resultBuilder.append(currLine).append('\n');
			}
		}
		catch(IOException e) {
			throw new IllegalStateException("Could not read the file.", e);
		}
		finally {
			try {
				reader.close();
			}
			catch(IOException e) {
				throw
					new IllegalStateException(
						"Could not close the reader.",
						e);
			}
		}
		
		// Return the trimmed result.
		return resultBuilder.toString().trim();
	}
}