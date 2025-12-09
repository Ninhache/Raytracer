package fr.ninhache.ui;

import fr.ninhache.raytracer.geometry.IShape;

import fr.ninhache.raytracer.geometry.shape.*;
import fr.ninhache.raytracer.lighting.ILight;
import fr.ninhache.raytracer.math.Color;
import fr.ninhache.raytracer.math.Epsilon;
import fr.ninhache.raytracer.render.RenderQuality;
import fr.ninhache.raytracer.scene.Camera;
import fr.ninhache.raytracer.scene.Scene;
import fr.ninhache.raytracer.scene.SceneBuilder;
import fr.ninhache.raytracer.scene.exception.ParseException;
import fr.ninhache.ui.model.light.EditableDirectionalLight;
import fr.ninhache.ui.model.light.EditableLight;
import fr.ninhache.ui.model.light.EditablePointLight;
import fr.ninhache.ui.model.light.EditableSpotLight;
import fr.ninhache.ui.model.shape.*;
import javafx.scene.Node;
import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.List;

public final class EditableScene {

    private final int width;
    private final int height;
    private final Camera camera;
    private Color ambientLight;
    private final List<EditableShape> shapes = new ArrayList<>();
    private final List<EditableLight> lights = new ArrayList<>();
    private final int maxDepth;
    private final Scene sourceScene;

    public EditableScene(int width, int height, Camera camera, int maxDepth, Color ambientLight, Scene sourceScene) {
        this.width = width;
        this.height = height;
        this.camera = camera;
        this.maxDepth = maxDepth;
        this.ambientLight = ambientLight;
        this.sourceScene = sourceScene;
    }


    public static EditableScene fromScene(Scene scene) {
        EditableScene editable = new EditableScene(
                scene.getWidth(),
                scene.getHeight(),
                scene.getCamera(),
                scene.getMaxDepth(),
                scene.getAmbientLight(),
                scene
        );

        for (IShape s : scene.getShapes()) {
            // todo: trouver une meilleure manière de faire ça, le if me donne la gerbe
            if (s instanceof Sphere sphere) {
                editable.shapes.add(EditableSphere.from(sphere));
            } else if (s instanceof Plane plane) {
                editable.shapes.add(EditablePlane.from(plane));
            } else if (s instanceof Triangle triangle) {
                editable.shapes.add(EditableTriangle.from(triangle));
            } else if (s instanceof Disk disk) {
                editable.shapes.add(EditableDisk.from(disk));
            } else if (s instanceof RegularPolygon polygon) {
                editable.shapes.add(EditableRegularPolygon.from(polygon));
            } else {
                editable.shapes.add(new UnsupportedEditableShape(s));
            }
        }

        editable.lights.add(new fr.ninhache.ui.model.light.EditableAmbientLight(
                scene.getAmbientLight(),
                editable::setAmbientLight
        ));


        for (ILight light : scene.getLights()) {
            if (light instanceof fr.ninhache.raytracer.lighting.PointLight pointLight) {
                editable.lights.add(EditablePointLight.from(pointLight));
            } else if (light instanceof fr.ninhache.raytracer.lighting.DirectionalLight dirLight) {
                editable.lights.add(EditableDirectionalLight.from(dirLight));
            } else if (light instanceof fr.ninhache.raytracer.lighting.SpotLight spotLight) {
                editable.lights.add(EditableSpotLight.from(spotLight));
            } else {
                editable.lights.add(new UnsupportedEditableLight(light));
            }
        }

        return editable;
    }

    public List<EditableShape> getShapes() {
        return shapes;
    }

    public List<EditableLight> getLights() {
        return lights;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public Camera getCamera() { return camera; }

    /**
     * Reconstruit une Scene à partir de l’état courant éditable.
     */
    public Scene toScene() throws ParseException {
        return toScene(RenderQuality.NORMAL);
    }

    public Scene toScene(RenderQuality quality) throws ParseException {
        int baseWidth = sourceScene != null ? sourceScene.getWidth() : width;
        int baseHeight = sourceScene != null ? sourceScene.getHeight() : height;

        int targetWidth = scaleDimension(baseWidth, quality);
        int targetHeight = scaleDimension(baseHeight, quality);


        SceneBuilder builder = new SceneBuilder();

        builder
            .setSize(targetWidth, targetHeight)
            .setCamera(camera)
            .setOutputFilename(sourceScene != null ? sourceScene.getOutputFilename() : null)
            .setAmbientLight(ambientLight)
            .setMaxDepth(maxDepth);

        for (EditableLight light : lights) {
            ILight built = light.toLight();
            if (built != null) {
                builder.addLight(built);
            }
        }

        for (EditableShape es : shapes) {
            IShape shape = es.toShape();
            if (shape != null) {
                builder.addShape(shape);
            }

        }

        Scene candidate = builder.build();

        boolean missingLights = candidate.getLights().isEmpty() && isZero(candidate.getAmbientLight());
        boolean missingShapes = candidate.getShapeCount() == 0;

        boolean noLightEnergy = candidate.getLights().stream()
                .allMatch(l -> isZero(l.getColor()))
                && isZero(candidate.getAmbientLight());

        boolean allMaterialsBlack = candidate.getShapes().stream()
                .map(IShape::getMaterial)
                .filter(mat -> mat != null)
                .allMatch(mat -> isZero(mat.diffuse()) && isZero(mat.specular()));

        if (missingShapes) {
            throw new ParseException("La scène reconstruite ne contient aucun objet : impossible de rendre.");
        }
        if (missingLights) {
            throw new ParseException("Aucune source lumineuse ou lumière ambiante définie : le rendu serait noir.");
        }
        if (allMaterialsBlack) {
            throw new ParseException("Tous les matériaux sont noirs (diffuse et specular à 0). Ajoutez ou restaurez des matériaux.");
        }
        if (noLightEnergy) {
            throw new ParseException("Toutes les lumières et la lumière ambiante sont nulles : le rendu serait noir.");
        }

        return candidate;
    }

    public void setAmbientLight(Color ambientLight) {
        this.ambientLight = ambientLight;
    }


    private boolean isZero(Color color) {

        return Math.abs(color.r()) < Epsilon.EPS && Math.abs(color.g()) < Epsilon.EPS && Math.abs(color.b()) < Epsilon.EPS;
    }

    private int scaleDimension(int value, RenderQuality quality) {
        double factor = quality != null ? quality.scaleFactor() : 1.0;
        int scaled = (int) Math.round(value * factor);
        if (scaled <= 0) {
            scaled = 1;
        }
        return scaled;
    }



    public int getMaxDepth() {
        return maxDepth;
    }

    private record UnsupportedEditableLight(ILight delegate) implements EditableLight {

        @Override
            public String name() {
                return delegate.getClass().getSimpleName() + " (non éditable)";
            }

            @Override
            public ILight toLight() {
                return delegate;
            }

            @Override
            public Node createEditorPane() {
                return new Label("Cette lumière n'est pas encore éditable.");
            }
        }

    private record UnsupportedEditableShape(IShape delegate) implements EditableShape {

        @Override
            public String name() {
                return delegate.getClass().getSimpleName() + " (non éditable)";
            }

            @Override
            public EditableMaterial getMaterial() {
                return null;
            }

            @Override
            public void setMaterial(EditableMaterial mat) {
                // non editable
            }

            @Override
            public IShape toShape() {
                return delegate; // ignorée lors de la reconstruction
            }

            @Override
            public Node createEditorPane() {
                return new Label("Cette forme n'est pas encore supportée par l'éditeur.");
            }
        }
}
