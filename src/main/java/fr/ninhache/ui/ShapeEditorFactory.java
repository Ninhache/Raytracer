package fr.ninhache.ui;

import fr.ninhache.ui.model.EditableShape;
import javafx.scene.Node;
import javafx.scene.control.Label;

public class ShapeEditorFactory {

    /**
     * Retourne un panneau d’édition adapté à la forme fournie.
     */
    public Node createEditor(EditableShape shape) {
        if (shape == null) {
            return new Label("Aucun objet sélectionné");
        }

        Node editor = shape.createEditorPane();
        return editor != null ? editor : new Label("Type d'objet non supporté pour l'édition");
    }
}
