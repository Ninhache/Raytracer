package fr.ninhache.ui.model;

import fr.ninhache.raytracer.render.RenderQuality;
import fr.ninhache.raytracer.render.RenderResult;
import fr.ninhache.raytracer.scene.Scene;
import fr.ninhache.raytracer.scene.exception.ParseException;
import fr.ninhache.ui.EditableScene;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Représente une scène ouverte dans un onglet.
 */
public final class SceneDocument {

    private final String filePath;
    private final String displayName;
    private final EditableScene editableScene;
    private final ObjectProperty<RenderResult> lastRender = new SimpleObjectProperty<>();

    public SceneDocument(String filePath, String displayName, Scene scene) {
        this.filePath = filePath;
        this.displayName = displayName;
        this.editableScene = EditableScene.fromScene(scene);
    }

    public String getFilePath() {
        return filePath;
    }

    public String getDisplayName() {
        return displayName;
    }

    public RenderResult getLastRender() {
        return lastRender.get();
    }

    public void setLastRender(RenderResult lastRender) {
        this.lastRender.set(lastRender);
    }

    public ObjectProperty<RenderResult> getLastRenderProperty() {
        return lastRender;
    }

    public Scene buildSceneForRender(RenderQuality quality) throws ParseException {
        return editableScene.toScene(quality);
    }

    public EditableScene getEditableScene() {
        return editableScene;
    }

    public int getMaxDepth() {
        return this.editableScene.getMaxDepth();
    }
}