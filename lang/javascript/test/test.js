// Load the Java components needed to read files.
importClass(java.io.File);
importClass(Packages.org.apache.tools.ant.util.FileUtils);
importClass(java.io.FileReader);

// Load JSON
eval(loadFile("lib/json2/json2.js"))

// Load Concordia.
eval(loadFile("src/Concordia.js"));

/**
 * Retrieves a File object from a file string.
 * 
 * @param testFileString The file path relative to the test directory 
 * 						 referencing the desired file.
 * 
 * @returns The File object for the file.
 */
function getFile(testFileString) {
	// Get the directory relative to the root of this directory.
	var testFile = 
		new File(
			project.getProperty("basedir"), 
			"../../test/" + testFileString);
	// If the directory doesn't exist, fail the test.
	if(! testFile.exists()) {
		self.fail("The test file is missing: " + testFileString);
	}
	if(! testFile.isFile()) {
		self.fail("The test file is not a file: " + testFileString);
	}
	
	// Get all of the files.
	return testFile;
}

/**
 * Retrieves the set of files in a directory.
 * 
 * @param testDirString The directory relative to the test directory containing
 * 						all of the valid tests.
 * 
 * @returns The set of files in the directory.
 */
function getFiles(testDirString) {
	// Get the directory relative to the root of this directory.
	var testDir = 
		new File(
			project.getProperty("basedir"), 
			"../../test/" + testDirString);
	// If the directory doesn't exist, fail the test.
	if(! testDir.exists()) {
		self.fail("The test directory is missing: " + testDirString);
	}
	if(! testDir.isDirectory()) {
		self.fail("The test directory is not a directory: " + testDirString);
	}
	
	// Get all of the files.
	return testDir.listFiles();
}

/**
 * Retrieves the contents of the file.
 * 
 * @param file A File object to read.
 * 
 * @returns The contents of the file.
 */
function getContents(file) {
	// Ensure that the file's name is not null.
	if(file === null) {
		self.fail("The File object is null.");
	}
	if(! file.exists()) {
		self.fail("The file does not exist: " + file);
	}
	
	var fileReader;
	try {
		fileReader = new FileReader(file);
	}
	catch(e) {
		self.fail("Could not open the file: " + e);
	}
	
	return new String(FileUtils.readFully(fileReader));
}

/**
 * Validates that a set of invalid definitions fail validation.
 * 
 * @param definitionDir The directory containing the definition files.
 */
function checkInvalidConcordiaDefinitions(definitionDir) {
	// Get all of the files.
	var files = getFiles(definitionDir);
	for (var i = 0; i < files.length; i++) {
		// Get the file's contents.
		var fileContents = getContents(files[i]);

		// Attempt to create a new instance of Concordia with the invalid
		// definition.
		var concordia = null;
		try {
			concordia = new Concordia(fileContents);
		}
		catch(e) {
			// This is what is supposed to happen.
		}
		
		if(concordia !== null) {
			self.fail(
				"An invalid definition passed validation: " + 
					fileContents);
		}
	}
}

/**
 * Checks that a set of files, each with their own data, are valid or invalid,
 * based on the 'areValid' parameter, for the parameterized 'concordia' object.
 * 
 * @param concordia An existing Concordia object.
 * 
 * @param dataDirectoryString The string representing the directory which 
 * 							  contains the data files to be tested.
 * 
 * @param areValid Whether or not the files should or should not pass 
 * 				   validation.
 */
function checkDataDefinitions(concordia, dataDirectoryString, areValid) {
	// Get all of the files.
	var files = getFiles(dataDirectoryString);
	for (var i = 0; i < files.length; i++) {
		// Get the file's contents.
		var fileContents = getContents(files[i]);
		
		// Check the data against the definition.
		try {
			concordia.validateData(fileContents);
			
			if(! areValid) {
				self.fail("Invalid data passed validation: " + fileContents);
			}
		}
		catch(e) {
			if(areValid) {
				self.fail(
					"Valid data failed validation:\n\n" + 
						fileContents +
						"\n\n" +
						e);
			}
		}
	}
}

/**
 * Creates a new instance of Concordia from the definition. Then, tests that
 * the data in all of the files in the 'invalidFileDirString' are invalid for
 * that definition and that the data in all of the files in the
 * 'validFileDirString' are valid.
 * 
 * @param definitionFileString The path and name of the file that contains the
 * 							   Concordia definition relative to the root test
 * 							   directory.
 *  
 * @param invalidDataDirString The path of the directory that contains the 
 * 							   files that contain invalid data for this schema
 * 							   relative to the root test directory. If there
 * 							   are no data files to test, this may be null.
 * 
 * @param validDataDirString The path of the directory that contains the files
 * 							 that contain invalid data for this schema relative
 * 							 to the root test directory. If there are no data
 * 							 files to test, this may be null.
 */
function checkData(definitionFileString, invalidDataDirString, validDataDirString) {
	// Get the definition of the data.
	var definition = getContents(getFile(definitionFileString));
	
	// Create the Concordia object.
	var concordia;
	try {
		concordia = new Concordia(definition);
	}
	catch(e) {
		self.fail("A valid Concordia definition was rejected:\n" + e);
	}
	
	// Test all of the invalid data.
	if(invalidDataDirString !== null) {
		checkDataDefinitions(concordia, invalidDataDirString, false);
	}
	
	// Test all of the valid data.
	if(validDataDirString !== null) {
		checkDataDefinitions(concordia, validDataDirString, true);
	}
}

// Create the test cases.
testCases(
	// RhinoUnit requires this but doesn't document what it is or does.
	test,
	
	/**
	 * Setup the environment for the tests.
	 */
	function setUp() {
		// Do nothing.
	},
	
	/**
	 * Tests all invalid definitions. These definitions can be found in the
	 * ${test/definition/*} directories.
	 */
	function testInvalidDefinitions() {
		// Check the definitions where the root object is not a JSON object.
		checkInvalidConcordiaDefinitions("definition/root/");

		// Check the definitions where the type of the 'type' field is not a
		// string.
		checkInvalidConcordiaDefinitions("definition/type/");
		
		// Check the definitions of objects.
		checkInvalidConcordiaDefinitions("definition/object/");
		
		// Check the definitions of arrays.
		checkInvalidConcordiaDefinitions("definition/array/");
		
		// Check the definitions of constant type arrays.
		checkInvalidConcordiaDefinitions("definition/const_type_array/");
		
		// Check the definitions of constant length arrays.
		checkInvalidConcordiaDefinitions("definition/const_length_array/");
		
		// Check the definitions of documentation, "doc", tags.
		checkInvalidConcordiaDefinitions("definition/doc/");
		
		// Check the definitions of the "optional" tag.
		checkInvalidConcordiaDefinitions("definition/doc/");
	},
	
	/**
	 * Tests valid and invalid data against their valid definitions.
	 * 
	 * The definition can be found in the ${test}/data/${type} directory and 
	 * the test data can be found in the "valid" and "invalid" sub-directories.
	 */
	function testInvalidData() {
		// Check the boolean data.
		checkData(
			"data/boolean/definition.json",
			"data/boolean/invalid/",
			"data/boolean/valid/");
		
		// Check the number data.
		checkData(
			"data/number/definition.json",
			"data/number/invalid/",
			"data/number/valid/");
		
		// Check the string data.
		checkData(
			"data/string/definition.json",
			"data/string/invalid/",
			"data/string/valid/");
		
		// Check the object data.
		checkData(
			"data/object/definition.json",
			"data/object/invalid/",
			"data/object/valid/");
		
		// Check the constant-length array data.
		checkData(
			"data/const_length_array/definition.json",
			"data/const_length_array/invalid/",
			"data/const_length_array/valid/");
		
		// Check all of the constant-type array data.
		checkData(
			"data/const_type_array_boolean/definition.json",
			"data/const_type_array_boolean/invalid/",
			"data/const_type_array_boolean/valid/");
		checkData(
			"data/const_type_array_number/definition.json",
			"data/const_type_array_number/invalid/",
			"data/const_type_array_number/valid/");
		checkData(
			"data/const_type_array_string/definition.json",
			"data/const_type_array_string/invalid/",
			"data/const_type_array_string/valid/");
		checkData(
			"data/const_type_array_object/definition.json",
			"data/const_type_array_object/invalid/",
			"data/const_type_array_object/valid/");
		checkData(
			"data/const_type_array_array/definition.json",
			"data/const_type_array_array/invalid/",
			"data/const_type_array_array/valid/");
		
		// Check the documentation, "doc", tag.
		checkData(
			"data/doc/definition.json",
			null,
			null);
		
		// Check the "optional" tag.
		checkData(
			"data/optional/definition.json",
			null,
			"data/optional/valid/");
	}
);