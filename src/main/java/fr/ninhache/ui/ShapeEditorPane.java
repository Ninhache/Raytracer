package fr.ninhache.ui;

import fr.ninhache.ui.model.EditableShape;
import javafx.scene.layout.VBox;

/**
 * Base polymorphe pour l’éditeur d’une forme.
 */
public abstract class ShapeEditorPane extends VBox {

    protected EditableShape shape;

    public void setShape(EditableShape shape) {
        this.shape = shape;
        rebuildUI();
    }

    /**
     * Reconstruit le contenu de l’UI en fonction du shape.
     */
    protected abstract void rebuildUI();
}
