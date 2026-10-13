.PHONY: compile test package clean

compile:
	mvn compile

test:
	mvn test

# Builds the jar without running the tests.
package:
	mvn package -DskipTests

clean:
	mvn clean
