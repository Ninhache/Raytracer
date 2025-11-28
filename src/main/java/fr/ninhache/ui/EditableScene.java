package fr.ninhache.ui;

import fr.ninhache.raytracer.geometry.IShape;

import fr.ninhache.raytracer.geometry.shape.Sphere;
import fr.ninhache.raytracer.lighting.ILight;
import fr.ninhache.raytracer.scene.Camera;
import fr.ninhache.raytracer.scene.Scene;
import fr.ninhache.raytracer.scene.SceneBuilder;
import fr.ninhache.raytracer.scene.exception.ParseException;
import fr.ninhache.ui.model.EditableMaterial;
import fr.ninhache.ui.model.EditableShape;
import fr.ninhache.ui.model.EditableSphere;
import javafx.scene.Node;

import java.util.ArrayList;
import java.util.List;

public final class EditableScene {

    private final int width;
    private final int height;
    private final Camera camera;
    private final List<EditableShape> shapes = new ArrayList<>();
    private final List<ILight> lights = new ArrayList<>();
    private final int maxDepth;

    public EditableScene(int width, int height, Camera camera, int maxDepth) {
        this.width = width;
        this.height = height;
        this.camera = camera;
        this.maxDepth = maxDepth;
    }

    public static EditableScene fromScene(Scene scene) {
        EditableScene editable = new EditableScene(scene.getWidth(), scene.getHeight(), scene.getCamera(), scene.getMaxDepth());

        for (IShape s : scene.getShapes()) {
            if (s instanceof Sphere sphere) {
                editable.shapes.add(EditableSphere.from(sphere));
            } else {
                editable.shapes.add(new EditableShape() {
                    @Override
                    public String name() {
                        return "Placeholder Shape";
                    }

                    @Override
                    public EditableMaterial getMaterial() {
                        return null;
                    }

                    @Override
                    public void setMaterial(EditableMaterial mat) {

                    }

                    @Override
                    public IShape toShape() {
                        return null;
                    }

                    @Override
                    public Node createEditorPane() {
                        return null;
                    }
                });
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
        builder.setSize(width, height).setCamera(camera);

        // System.out.println("Matériau shape: diffuse=" + mat.getDiffuse() + ", specular=" + mat.getSpecular() + ", shininess=" + mat.getShininess());


        for (ILight light : lights) {
            builder.addLight(light);
        }

        for (EditableShape es : shapes) {
            builder.addShape(es.toShape());
        }

        return builder.build();
    }

    public int getMaxDepth() {
        return maxDepth;
    }
}
