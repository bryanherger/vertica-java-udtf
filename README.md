# vertica-java-udtf
Maven project example UDTF for Java.  Demonstrates some unusual applications.

## Setup, build, test
Copy your version of VerticaSDK.jar and BuildInfo.java into the project.

Edit pom.xml for Vertica version and VerticaSDK.jar path.

Buld with "mvn clean package assmebly:single"

Copy JavaFunctions.sql and the target JAR to your Vertica node.

Check JavaFunctions.sql to ensure the library name matches your target JAR and make any other adjustments to the tests.

Run "vsql -f JavaFunctions.sql" to test the library.

## Disclaimer
This is an unsupported proof of concept! 
