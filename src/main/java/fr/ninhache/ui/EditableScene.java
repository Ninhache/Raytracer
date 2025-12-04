package fr.ninhache.ui;

import fr.ninhache.raytracer.geometry.IShape;

import fr.ninhache.raytracer.geometry.shape.*;
import fr.ninhache.raytracer.lighting.ILight;
import fr.ninhache.raytracer.math.Color;
import fr.ninhache.raytracer.math.Epsilon;
import fr.ninhache.raytracer.scene.Camera;
import fr.ninhache.raytracer.scene.Scene;
import fr.ninhache.raytracer.scene.SceneBuilder;
import fr.ninhache.raytracer.scene.exception.ParseException;
import fr.ninhache.ui.model.*;
import javafx.scene.Node;
import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.List;

public final class EditableScene {

    private final int width;
    private final int height;
    private final Camera camera;
    private final Color ambientLight;
    private final List<EditableShape> shapes = new ArrayList<>();
    private final List<ILight> lights = new ArrayList<>();
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

        // lights : pour l’instant on les garde telles quelles, on ne les édite pas encore
        editable.lights.addAll(scene.getLights());

        return editable;
    }

    public List<EditableShape> getShapes() {
        return shapes;
    }

    public List<ILight> getLights() {
        return lights;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public Camera getCamera() { return camera; }

    /**
     * Reconstruit une Scene à partir de l’état courant éditable.
     */
    public Scene toScene() throws ParseException {
        SceneBuilder builder = new SceneBuilder();
        builder
                .setSize(width, height)
                .setCamera(camera)
                .setOutputFilename(sourceScene != null ? sourceScene.getOutputFilename() : null)
                .setAmbientLight(ambientLight)
                .setMaxDepth(maxDepth);


        // System.out.println("Matériau shape: diffuse=" + mat.getDiffuse() + ", specular=" + mat.getSpecular() + ", shininess=" + mat.getShininess());


        for (ILight light : lights) {
            builder.addLight(light);
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
                .allMatch(mat -> isZero(mat.getDiffuse()) && isZero(mat.getSpecular()));

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

    private boolean isZero(Color color) {

        return Math.abs(color.r()) < Epsilon.EPS && Math.abs(color.g()) < Epsilon.EPS && Math.abs(color.b()) < Epsilon.EPS;
    }



    public int getMaxDepth() {
        return maxDepth;
    }

    private static final class UnsupportedEditableShape implements EditableShape {
        private final IShape delegate;

        private UnsupportedEditableShape(IShape delegate) {
            this.delegate = delegate;
        }

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
