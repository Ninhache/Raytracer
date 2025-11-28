package fr.ninhache.ui.model;

import fr.ninhache.raytracer.geometry.IShape;
import javafx.scene.Node;

public interface EditableShape {

    String name();
    EditableMaterial getMaterial();
    void setMaterial(EditableMaterial mat);

    IShape toShape();

    Node createEditorPane();
}
