package fr.ninhache.ui;

import fr.ninhache.ui.model.EditableShape;
import fr.ninhache.ui.model.EditableSphere;
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

        if (shape instanceof EditableSphere sphere) {
            SphereEditorPane pane = new SphereEditorPane();
            pane.setShape(sphere);
            return pane;
        }

        Label unsupported = new Label("Type d'objet non supporté pour l'édition");
        unsupported.setWrapText(true);
        return unsupported;
    }
}