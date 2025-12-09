MVN ?= mvn

CLI_JAR = target/tp3-1.0-SNAPSHOT-cli.jar
GUI_JAR = target/tp3-1.0-SNAPSHOT.jar
SOURCES = $(shell find src/main/java -name '*.java')

# Everything passed after the target name (run-cli, run-gui, etc.)
ARGS = $(filter-out $@,$(MAKECMDGOALS))

.PHONY: all clean build run-cli run-gui run-gui-mvn build-docs

all: $(CLI_JAR) $(GUI_JAR)

# Build both jars when sources or pom change
$(CLI_JAR) $(GUI_JAR): $(SOURCES) pom.xml
	$(MVN) package

# Explicit "build" target if you just want to build once
build: $(CLI_JAR) $(GUI_JAR)

clean:
	$(MVN) clean

# CLI: fat JAR, forwards arguments to the Java program
run-cli: $(CLI_JAR)
	java -jar $(CLI_JAR) $(ARGS)

# GUI JavaFX: all deps (including Reflections) on module-path in target/libs
run-gui: $(GUI_JAR)
	java --enable-native-access=javafx.graphics \
	     --module-path target/libs \
	     --add-modules javafx.controls,javafx.swing,org.reflections \
	     -cp $(GUI_JAR) \
	     fr.ninhache.RaytracerApp $(ARGS)

# GUI via Maven JavaFX plugin
run-gui-mvn:
	$(MVN) javafx:run

# Build Javadocs and copy them to a top-level docs/ folder
build-docs:
	$(MVN) javadoc:javadoc
	rm -rf docs
	mkdir -p docs
	cp -r target/reports/apidocs/* docs/

# Prevent "make run-cli foo" from treating "foo" as a target
%:
	@:
