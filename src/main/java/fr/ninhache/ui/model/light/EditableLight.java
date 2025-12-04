package fr.ninhache.ui.model.light;

import fr.ninhache.raytracer.lighting.ILight;
import javafx.scene.Node;

public interface EditableLight {

    String name();

    ILight toLight();

    Node createEditorPane();
}

