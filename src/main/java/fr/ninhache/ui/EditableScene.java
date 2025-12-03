package fr.ninhache.ui;

import fr.ninhache.raytracer.geometry.IShape;

import fr.ninhache.raytracer.geometry.shape.Sphere;
import fr.ninhache.raytracer.lighting.ILight;
import fr.ninhache.raytracer.math.Color;
import fr.ninhache.raytracer.scene.Camera;
import fr.ninhache.raytracer.scene.Scene;
import fr.ninhache.raytracer.scene.SceneBuilder;
import fr.ninhache.raytracer.scene.exception.ParseException;
import fr.ninhache.ui.model.EditableMaterial;
import fr.ninhache.ui.model.EditableShape;
import fr.ninhache.ui.model.EditableSphere;
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

    public EditableScene(int width, int height, Camera camera, int maxDepth, Color ambientLight) {
        this.width = width;
        this.height = height;
        this.camera = camera;
        this.maxDepth = maxDepth;
        this.ambientLight = ambientLight;
    }

    public static EditableScene fromScene(Scene scene) {
        EditableScene editable = new EditableScene(
                scene.getWidth(),
                scene.getHeight(),
                scene.getCamera(),
                scene.getMaxDepth(),
                scene.getAmbientLight()
        );


        for (IShape s : scene.getShapes()) {
            if (s instanceof Sphere sphere) {
                editable.shapes.add(EditableSphere.from(sphere));
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

        return builder.build();
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
