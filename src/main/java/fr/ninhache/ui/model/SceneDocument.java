package fr.ninhache.ui.model;

import fr.ninhache.raytracer.render.RenderResult;
import fr.ninhache.raytracer.scene.Scene;
import fr.ninhache.raytracer.scene.exception.ParseException;
import fr.ninhache.ui.EditableScene;

/**
 * Représente une scène ouverte dans un onglet.
 */
public final class SceneDocument {

    private final String filePath;      // chemin du fichier sur disque (peut être null)
    private final String displayName;   // nom à afficher dans l'onglet
//    private final Scene scene;
    private final EditableScene editableScene;

    public Scene debugScene;

    private RenderResult lastRender;    // dernier rendu (image + stats), optionnel

    public SceneDocument(String filePath, String displayName, Scene scene) {
        this.filePath = filePath;
        this.displayName = displayName;
        this.editableScene = EditableScene.fromScene(scene);
        this.debugScene = scene;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getDisplayName() {
        return displayName;
    }

    public RenderResult getLastRender() {
        return lastRender;
    }

    public void setLastRender(RenderResult lastRender) {
        this.lastRender = lastRender;
    }

    public Scene buildSceneForRender() throws ParseException {
        return editableScene.toScene();
    }

    public EditableScene getEditableScene() {
        return editableScene;
    }

    public int getMaxDepth() {
        return this.editableScene.getMaxDepth();
    }
}