package fr.ninhache.ui;

import fr.ninhache.ui.model.light.EditableLight;
import javafx.scene.Node;
import javafx.scene.control.Label;

public class LightEditorFactory {

    public Node createEditor(EditableLight light) {
        if (light == null) {
            return new Label("Aucune lumière sélectionnée");
        }

        Node editor = light.createEditorPane();
        return editor != null ? editor : new Label("Type de lumière non supporté pour l'édition");
    }
}