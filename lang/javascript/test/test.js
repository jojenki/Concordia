// Load the Java components needed to read files.
importClass(java.io.File);
importClass(Packages.org.apache.tools.ant.util.FileUtils);
importClass(java.io.FileReader);

// Load JSON
eval(loadFile("lib/json2/json2.js"));

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
	
	// Get the files.
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
 * Validates that a set of definitions pass or fail validation based on what
 * they are defined to do.
 * 
 * @param definitionDir The directory containing the definition files.
 * 
 * @param shouldPass Whether or not the definitions should pass validation.
 */
function checkDefinitions(definitionDir, shouldPass) {
	// Get all of the files.
	var files = getFiles(definitionDir);
	for (var i = 0; i < files.length; i++) {
		// Get the file's contents.
		var fileContents = getContents(files[i]);

		// Attempt to create a new instance of Concordia with the invalid
		// definition.
		var concordia;
		try {
			concordia = new Concordia(fileContents);
			
			if(! shouldPass) {
				self.fail(
					"An invalid definition passed validation: " +
						fileContents);
			}
		}
		catch(e) {
			if(shouldPass) {
				self.fail(
					"A valid definition failed validation: " + 
						fileContents);
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

/**
 * Checks that a set of files, each with their own data, are valid or invalid,
 * based on the 'areValid' parameter, for the parameterized 'concordia' object.
 * 
 * @param concordia An existing Concordia object.
 * 
 * @param dataDirectoryString The string representing the directory which 
 * 							  contains the data files to be tested.
 * 
 * @param shouldPass Whether or not the data in the files should or should not 
 * 					 pass validation.
 */
function checkDataDefinitions(concordia, dataDirectoryString, shouldPass) {
	// Get all of the files.
	var files = getFiles(dataDirectoryString);
	for (var i = 0; i < files.length; i++) {
		// Get the file's contents.
		var fileContents;
		try {
		    fileContents = getContents(files[i]);
		}
		catch(e) {
		    throw "One of the tests is invalid JSON: " + getContents(files[i]);
		}
		
		// Check the data against the definition.
		try {
			concordia.validateData(fileContents);
			
			if(! shouldPass) {
				self.fail("Invalid data passed validation: " + fileContents);
			}
		}
		catch(e) {
			if(shouldPass) {
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
 * The exception message that will be thrown when an extension's type checker
 * is executed. It ends with a colon so that the actual type checker can ensure
 * that the correct extension was performed.
 */
var EXTENSION_TYPE_SUCCESS = "Type Extension: ";
/**
 * The function that will be assigned as the type checker for all types. It
 * will always thrown an exception which is the {@link #EXTENSION_TYPE_SUCCESS}
 * text with the type of the given object appended.
 * 
 * @param obj The JSON representing the type being validated.
 * 
 * @throws EXTENSION_TYPE_SUCCESS {@link #EXTENSION_TYPE_SUCCESS} appended with
 *         the type being validated.
 */
function typeExtension(obj) {
	// Each definition should have an extra field, 'extra', which we should be
	// able to extract.
	var extra = obj['extra'];
	
	// Extra should not be null.
	if(extra === null) {
		self.fail("'extra' is null.");
	}
	// If it's not null, get its type.
	var extraType = Object.prototype.toString.call(extra);
	// If it's undefined, then it wasn't present, which is an error.
	if(extraType === "undefined") {
		self.fail("'extra' is missing.");
	}
	// If it's not a number, that is an error.
	else if(extraType !== "[object Number]") {
		self.fail("'extra' is not a number: " + extraType);
	}
	
	// If everything succeeded, return the success message with this object's
	// type.
	throw EXTENSION_TYPE_SUCCESS + obj['type'];
}

/**
 * After Concordia's definition has been extended, use this function with a
 * definition for a specific type to guarantee that that type's extension
 * function will be executed.
 * 
 * @param typeDefinitionFile
 *        The definition file whose contents will be used to use to build a
 *        Concordia object which should test a specific type's extension.
 */
function checkExtensionType(typeDefinitionFile, type) {
	// There are multiple flows that will result in an error, so we use a flag
	// that we check at the end to determine if it failed.
	var pass = false;
	
	// Get the file's contents.
	var fileContents = getContents(getFile(typeDefinitionFile));
	
	// Get the custom exception for this type.
	var exceptionText = EXTENSION_TYPE_SUCCESS + type;
	
	// Create the Concordia object.
	var concordia;
	try {
		concordia = new Concordia(fileContents);
	}
	// If it throws an exception and, if it's the exception we expect, set the
	// test as passed.
	catch(e) {
		if(e.toString() === exceptionText) {
			pass = true;
		}
	}
	
	// If the test did not pass, fail.
	if(! pass) {
		self.fail("The type did not execute its type extension code: " + type);
	}
}

/**
 * The exception message that will be thrown when an extension's data checker
 * is executed. It ends with a colon so that the actual data checker can ensure
 * that the correct extension was performed.
 */
var EXTENSION_DATA_SUCCESS = "Data Extension: ";
/**
 * The function that will always be assigned as the data checker for all types.
 * It will always thrown an exception with is the
 * {@link #EXTENSION_DATA_SUCCESS} text with the type of the given object
 * appended.
 * 
 * @param schema The schema being validated.
 * 
 * @param data The data to validate.
 * 
 * @throws EXTENSION_DATA_SUCCESS {@link #EXTENSION_DATA_SUCCESS} appended with
 *         the type being validated.
 */
function dataExtension(schema, data) {
	throw EXTENSION_DATA_SUCCESS + schema['type'];
}

/**
 * After Concordia's definition has been extended, use this function with data
 * for a specific type to guarantee that that type's extension function for the
 * data will be executed.
 * 
 * @param typeDefinitionFile
 *        The definition file whose data should be used to use to build a 
 *        Concordia object which should test a
 *        specific type's extension.
 */
function checkExtensionData(definitionFile, dataFile, type) {
	// There are multiple flows that will result in an error, so we use a flag
	// that we check at the end to determine if it failed.
	var pass = false;
	
	// Get the definition.
	var definitionFileContents = getContents(getFile(definitionFile));
	
	// Create the Concordia object.
	var concordia = null;
	try {
		concordia = new Concordia(definitionFileContents);
	}
	// If it throws an exception and, if it's the exception we expect, set the
	// test as passed.
	catch(e) {
		self.fail(
			"A valid definition was deemed invalid: " +
				e.toString() +
				"\n" +
				definitionFileContents);
	}
	
	// Get the data.
	var dataFileContents = getContents(getFile(dataFile));
	
	// Get the custom exception for this type.
	var exceptionText = EXTENSION_DATA_SUCCESS + type;
	
	// Validate the data which should cause the custom validation code to run
	// which should throw an exception.
	try {
		concordia.validateData(dataFileContents);
	}
	// If it throws an exception and, if it's the exception we expect, set the
	// test as passed.
	catch(e) {
		if(e.toString() === exceptionText) {
			pass = true;
		}
	}
	
	// If the test did not pass, fail.
	if(! pass) {
		self.fail(
			"The type did not execute its data-checking extension code: " + 
			    type);
	}
}

/**
 * <p>A mock object of the standard XMLHttpRequest object.</p>
 * 
 * <p>Because this testing suite does not come with a full implementation of
 * the JavaScript environment, there is no pre-defined XMLHttpRequest object.
 * This is used to our advantage by allowing us to mock the object to prevent
 * actual network requests and, instead, to hijack the requests to return
 * whatever we want.</p>
 * 
 * <p>For tests, modify the XMLHttpRequest.prototype.remote object. This object
 * will not exist in the prototype, so you must first invoke:</p>
 * 
 * <code>
 * XMLHttpRequest.prototype.remote = {}
 * </code>
 * 
 * <p>All of the fields in this object should shadow their counterparts in a 
 * real-world XMLHttpRequest object, but they will not be set until the 
 * appropriate function is called. For instance, {@link XMLHttpRequest#send()}
 * will set the status and response text.</p>
 * 
 * <p>This is not a feature-complete implementation. If Concordia is modified
 * to use additional features of XMLHttpRequest, it is probably necessary that
 * this be updated as well.</p>
 */
function XMLHttpRequest() {
    var DEFAULT_STATUS = 0;
    var DEFAULT_RESPONSE_TEXT = "";
    
    // Inherit the prototype's remote object.
    this.remote = XMLHttpRequest.prototype.remote;
    
    // If there was no remote object in the prototype or if it was not an
    // object, create one.
    if(
        (typeof this.remote === "undefined") ||
        (this.remote === null) ||
        (Object.prototype.toString.call(this.remote) !== "[object Object]")) {
        
        this.remote = {};
    }
    
    // If no status was given, set it to the default.
    if(typeof this.remote.status === "undefined") {
        this.remote.status = DEFAULT_STATUS;
    }
    
    // If no response text was given, set it to the default.
    if(typeof this.remote.responseText === "undefined") {
        this.remote.responseText = DEFAULT_RESPONSE_TEXT;
    }
    
    // Set the internals of this object to their defaults.
    this.status = DEFAULT_STATUS;
    this.responseText = DEFAULT_RESPONSE_TEXT;
    
    /**
     * The open function is not being utilized, so its implementation is empty.
     */
    this.open = function() {}
    
    /**
     * The send function sets the status and the response text.
     */
    this.send = 
        function() {
            this.status = this.remote.status;
            this.responseText = this.remote.responseText;
        }
}

/**
 * Gets the local and remote definitions, builds the Concordia object, and
 * tests the valid and invalid data.
 */
function checkDataReference(localDefinition, remoteDefinition, invalidDataDirString, validDataDirString) {
    // Create the valid, complex, referenced schema and use it for 
    // validating some data.
    XMLHttpRequest.prototype.remote.responseText =
        getContents(getFile(remoteDefinition));
    
    // Build the Concordia object.
    var concordia;
    try {
        concordia = new Concordia(getContents(getFile(localDefinition)));
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
	 * ${test/definition/invalid/*} directories.
	 */
	function testInvalidDefinitions() {
		// Check the definitions where the root object is not a JSON object.
		checkDefinitions("definition/invalid/root/", false);

		// Check the definitions where the type of the 'type' field is not a
		// string.
		checkDefinitions("definition/invalid/type/", false);
		
		// Check the definitions of objects.
		checkDefinitions("definition/invalid/object/", false);
		
		// Check the definitions of arrays.
		checkDefinitions("definition/invalid/array/", false);
		
		// Check the definitions of constant type arrays.
		checkDefinitions("definition/invalid/const_type_array/", false);
		
		// Check the definitions of constant length arrays.
		checkDefinitions("definition/invalid/const_length_array/", false);
		
		// Check the definitions of documentation, "doc", tags.
		checkDefinitions("definition/invalid/doc/", false);
		
		// Check the definitions of the "optional" tag.
		checkDefinitions("definition/invalid/doc/", false);
	},
	
	/**
	 * Tests basic, valid definitions to ensure that they pass.
	 */
	function testValidDefinitions() {
		// Check that all of the valid definitions pass.
		checkDefinitions("definition/valid/", true);
		
		// Check that all of the extended definitions pass despite having
		// additional, unknown keys.
		checkDefinitions("definition/extended/", true);
	},
	
	/**
	 * Tests valid and invalid data against their valid definitions.
	 * 
	 * The definition can be found in the ${test}/data/${type} directory and 
	 * the test data can be found in the "valid" and "invalid" sub-directories.
	 */
	function testData() {
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
	},
	
	/**
	 * Tests the extensions onto the type definitions. Types are allowed to
	 * extend their definition. This will add those extended definitions and
	 * then check to be sure they are run.
	 */
	function testValidDefinitionExtensions() {
		// Extend the prototype to include a check for boolean extensions.
		Concordia.prototype.validateSchemaExtensionBoolean = typeExtension;
		checkExtensionType("definition/extended/boolean.json", "boolean");
		delete Concordia.prototype.validateSchemaExtensionBoolean;
		
		// Extend the prototype to include a check for number extensions.
		Concordia.prototype.validateSchemaExtensionNumber = typeExtension;
		checkExtensionType("definition/extended/number.json", "number");
		delete Concordia.prototype.validateSchemaExtensionNumber;
		
		// Extend the prototype to include a check for string extensions.
		Concordia.prototype.validateSchemaExtensionString = typeExtension;
		checkExtensionType("definition/extended/string.json", "string");
		delete Concordia.prototype.validateSchemaExtensionString;
		
		// Extend the prototype to include a check for object extensions.
		Concordia.prototype.validateSchemaExtensionObject = typeExtension;
		checkExtensionType("definition/extended/object.json", "object");
		delete Concordia.prototype.validateSchemaExtensionObject;
		
		// Extend the prototype to include a check for array extensions.
		Concordia.prototype.validateSchemaExtensionArray = typeExtension;
		checkExtensionType("definition/extended/array.json", "array");
		delete Concordia.prototype.validateSchemaExtensionArray;
	},
	
	/**
	 * Tests the extensions onto the data. Concordia may be extended to include
	 * additional validation of data, generally this is associated with 
	 * extending type definitions. This will only add the data-checking
	 * extensions and not the type-checking extensions.
	 */
	function testValidDataExtensions() {
		// Extend the prototype to include a data check for boolean data.
		Concordia.prototype.validateDataExtensionBoolean = dataExtension;
		checkExtensionData(
			"definition/extended/boolean.json",
			"data/extended/boolean.json",
			"boolean");
		delete Concordia.prototype.validateDataExtensionBoolean;

		// Extend the prototype to include a data check for number data.
		Concordia.prototype.validateDataExtensionNumber = dataExtension;
		checkExtensionData(
			"definition/extended/number.json",
			"data/extended/number.json",
			"number");
		delete Concordia.prototype.validateDataExtensionNumber;

		// Extend the prototype to include a data check for number data.
		Concordia.prototype.validateDataExtensionString = dataExtension;
		checkExtensionData(
			"definition/extended/string.json",
			"data/extended/string.json",
			"string");
		delete Concordia.prototype.validateDataExtensionString;

		// Extend the prototype to include a data check for object data.
		Concordia.prototype.validateDataExtensionObject = dataExtension;
		checkExtensionData(
			"definition/extended/object.json",
			"data/extended/object.json",
			"object");
		delete Concordia.prototype.validateDataExtensionObject;

		// Extend the prototype to include a data check for array data.
		Concordia.prototype.validateDataExtensionArray = dataExtension;
		checkExtensionData(
			"definition/extended/array.json",
			"data/extended/array.json",
			"array");
		delete Concordia.prototype.validateDataExtensionArray;
	},
	
	/**
	 * Tests that remote schemas can correctly be pulled into a schema as it is
	 * being validated.
	 */
	function testRemoteSchemas() {
	    // A temporary testing object.
	    var concordia;
	    
	    // Setup the XMLHttpRequest object.
	    XMLHttpRequest.prototype.remote = {};
	    
	    // Test with an invalid status.
	    XMLHttpRequest.prototype.remote.status = 404;
	    try {
    	    concordia = 
    	        new Concordia(
    	            getContents(
    	                getFile("definition/reference/base_local.json")));
    	    
    	    self.fail(
    	        "A remote schema could not be retrieved, but the Concordia " +
    	            "object was built nevertheless.");
	    }
	    catch(e) {
	        if(e.toString().indexOf("404") === -1) {
	            self.fail(
	                "An exception was thrown as expected due to the remote " +
	                    "server returning a 404 status code, but the thrown " +
	                    "exception did not indicate this: " +
	                    e);
	        }
	    }
	    
	    // Test with no response text.
	    XMLHttpRequest.prototype.remote.status = 200;
	    XMLHttpRequest.prototype.remote.responseText = null;
        try {
            concordia = 
                new Concordia(
                    getContents(
                        getFile("definition/reference/base_local.json")));
            
            self.fail(
                "A remote schema had null returned, but the Concordia " +
                    "object was built nevertheless.");
        }
        catch(e) {
            if(e.toString().indexOf("not returned") === -1) {
                self.fail(
                    "An exception was thrown as expected due to the remote " +
                        "server returning null, but the thrown exception did" +
                        "not indicate this: " +
                        e);
            }
        }
	    
	    // Test with an empty response.
        XMLHttpRequest.prototype.remote.responseText = "";
        try {
            concordia = 
                new Concordia(
                    getContents(
                        getFile("definition/reference/base_local.json")));
            
            self.fail(
                "A remote schema had nothing returned, but the Concordia " +
                    "object was built nevertheless.");
        }
        catch(e) {
            if(e.toString().indexOf("not returned") === -1) {
                self.fail(
                    "An exception was thrown as expected due to the remote " +
                        "server returning nothing, but the thrown exception " +
                        "did not indicate this: " +
                        e);
            }
        }
	    
	    // Test with a non-JSON response.
        XMLHttpRequest.prototype.remote.responseText = "Not JSON";
        try {
            concordia = 
                new Concordia(
                    getContents(
                        getFile("definition/reference/base_local.json")));
            
            self.fail(
                "A remote schema had nothing returned, but the Concordia " +
                    "object was built nevertheless.");
        }
        catch(e) {
            if(e.toString().indexOf("SyntaxError") === -1) {
                self.fail(
                    "An exception was thrown as expected due to the remote " +
                        "server returning nothing, but the thrown exception " +
                        "did not indicate this: " +
                        e);
            }
        }
        
        // Test with a valid schema and a valid remote schema.
        XMLHttpRequest.prototype.remote.responseText =
            getContents(getFile("definition/reference/base_remote.json"));
        try {
            concordia = 
                new Concordia(
                    getContents(
                        getFile("definition/reference/base_local.json")));
        }
        catch(e) {
            self.fail(
                "A valid local and remote schema were given, but an " +
                    "exception was still thrown: " +
                    e);
        }
        
        // Test with valid schemas where the local schema has a sub-object that
        // is defined by a remote schema.
        XMLHttpRequest.prototype.remote.responseText =
            getContents(getFile("definition/reference/base_remote.json"));
        try {
            concordia = 
                new Concordia(
                    getContents(
                        getFile(
                            "definition/reference/object_sub_local.json")));
        }
        catch(e) {
            self.fail(
                "A valid local schema with a sub-schema and valid remote " +
                    "schema were given, but an exception was still thrown: " +
                    e);
        }
        
        // Test with valid schemas where the extended schema has a field with a
        // different name but the same type.
        XMLHttpRequest.prototype.remote.responseText =
            getContents(
                getFile("definition/reference/object_extend_remote.json"));
        try {
            concordia = 
                new Concordia(
                    getContents(
                        getFile(
                            "definition/reference/object_extend_local.json")));
        }
        catch(e) {
            self.fail(
                "A valid local and remote schema were given with different " +
                    "field names but the same type, and an exception was " +
                    "thrown: " +
                    e);
        }
        
        // Test with valid schemas where the extended schema has a field with
        // the same name as the base schema.
        XMLHttpRequest.prototype.remote.responseText =
            getContents(
                getFile(
                    "definition/reference/object_extend_remote_duplicate_name.json"));
        try {
            concordia = 
                new Concordia(
                    getContents(
                        getFile(
                            "definition/reference/object_extend_local.json")));
            
            self.fail(
                "A remote schema had an identical name to the schema " +
                    "extending it, but the Concordia object was built " +
                    "nevertheless.");
        }
        catch(e) {
            if(e.toString().indexOf("identical name") === -1) {
                self.fail(
                    "An exception was thrown as expected due to the remote " +
                        "schema having an identical name as the local " +
                        "schema, but the thrown exception did not indicate " +
                        "this: " +
                        e);
            }
        }
        
        // Test with valid schemas where the extended schema is an object and
        // the base schema is an array.
        XMLHttpRequest.prototype.remote.responseText =
            getContents(
                getFile(
                    "definition/reference/object_extend_remote_not_object.json"));
        try {
            concordia = 
                new Concordia(
                    getContents(
                        getFile(
                            "definition/reference/object_extend_local.json")));
            
            self.fail(
                "A remote schema was an array while the local schema was " +
                    "an object, but the Concordia object was built " +
                    "nevertheless.");
        }
        catch(e) {
            if(e.toString().indexOf("root type") === -1) {
                self.fail(
                    "An exception was thrown as expected due to the remote " +
                        "schema being an array and the local schema being " +
                        "an object, but the thrown exception did not " +
                        "indicate this: " +
                        e);
            }
        }
        
        // Test with valid schemas where the local schema is an array. It
        // shouldn't matter what the sub-schema is.
        XMLHttpRequest.prototype.remote.responseText =
            getContents(
                getFile("definition/reference/base_remote.json"));
        try {
            concordia = 
                new Concordia(
                    getContents(
                        getFile(
                            "definition/reference/array_const_length.json")));
        }
        catch(e) {
            self.fail(
                "An exception was thrown for a base schema whose type was " +
                    "a constant length array, so the remote type shouldn't " +
                    "matter: " +
                    e);
        }
        
        // Test with valid schemas where the local schema is an array. It
        // shouldn't matter what the sub-schema is.
        XMLHttpRequest.prototype.remote.responseText =
            getContents(
                getFile("definition/reference/base_remote.json"));
        try {
            concordia = 
                new Concordia(
                    getContents(
                        getFile(
                            "definition/reference/array_const_type.json")));
        }
        catch(e) {
            self.fail(
                "An exception was thrown for a base schema whose type was " +
                    "a constant type array, so the remote type shouldn't " +
                    "matter: " +
                    e);
        }
        
        // Use a set of valid schemas to test valid and invalid data.
        checkDataReference(
            "data/reference/local.json",
            "data/reference/referenced.json",
            "data/reference/invalid/",
            "data/reference/valid/");
	}
);