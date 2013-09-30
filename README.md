Concordia
=========

Concordia is a JSON schema language.

## Building

To build, you will need [http://ant.apache.org/](ant). Every 'dist' target will also test the code.

### Building Everything

Run:

`ant clean dist`

This will create a folder, 'dist', which will have a similar structure to the 'lang' directory. Each language folder will have a 'dist' folder that will contain all of the distribution build targets for that language.

### Building for a specific language.

Go into the language-specific directory under the 'lang' directory. Each language has its own build file; once there, run:

`ant clean dist`

This will create a folder, 'dist', which will have all of the distribution build targets.

### Language-Specific Build Targets

Some languages have specific targets for that language.

##### JavaScript

* `lint` runs the [http://www.javascriptlint.com/](JSON Lint) checker. Note: We recognize the need for code quality, but we do not fully adhere to everything the lint checker recommends.

##### Java

* `javadoc` will build the JavaDoc JAR file.
* `sources` will build the sources JAR file. 