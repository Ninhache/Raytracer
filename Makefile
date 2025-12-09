MVN ?= mvn

# Everything passed after the target name (run-cli, run-gui, etc.)
ARGS = $(filter-out $@,$(MAKECMDGOALS))

.PHONY: all build clean run-cli run-gui run-gui-mvn build-docs

all: build

build:
	$(MVN) clean package

clean:
	$(MVN) clean

# CLI: fat JAR, forwards arguments to the Java program
run-cli: build
	java -jar target/tp3-1.0-SNAPSHOT-cli.jar $(ARGS)

# GUI JavaFX: all deps (including Reflections) on module-path in target/libs
run-gui: build
	java --enable-native-access=javafx.graphics \
	     --module-path target/libs \
	     --add-modules javafx.controls,javafx.swing,org.reflections \
	     -cp target/tp3-1.0-SNAPSHOT.jar \
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

