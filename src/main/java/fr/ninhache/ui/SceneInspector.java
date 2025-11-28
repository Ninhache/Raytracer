package fr.ninhache.ui;

import fr.ninhache.raytracer.lighting.ILight;
import fr.ninhache.raytracer.scene.Camera;
import fr.ninhache.raytracer.scene.Scene;
import fr.ninhache.ui.model.EditableShape;
import fr.ninhache.ui.model.SceneDocument;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class SceneInspector extends TabPane {

    private final Label lblSize = new Label();
    private final Label lblFov = new Label();
    private final Label lblMaxDepth = new Label();
    private final Label lblObjects = new Label();
    private final Label lblLights = new Label();

    private final ListView<EditableShape> shapeList = new ListView<>();
    private final ListView<ILight> lightsList = new ListView<>();

    BorderPane editorContainer = new BorderPane();

    public SceneInspector() {
        Tab sceneTab = new Tab("Scène", createSceneInfoPane());
        sceneTab.setClosable(false);

        Tab objectsTab = new Tab("Objets", createObjectsPane());
        objectsTab.setClosable(false);

        Tab lightsTab = new Tab("Lumières", createLightsPane());
        lightsTab.setClosable(false);

        getTabs().addAll(sceneTab, objectsTab, lightsTab);
    }

    private VBox createSceneInfoPane() {
        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(4);
        grid.setPadding(new Insets(8));

        int row = 0;
        grid.add(new Label("Taille :"), 0, row);
        grid.add(lblSize, 1, row++);

        grid.add(new Label("FOV :"), 0, row);
        grid.add(lblFov, 1, row++);

        grid.add(new Label("Max depth :"), 0, row);
        grid.add(lblMaxDepth, 1, row++);

        grid.add(new Label("Objets :"), 0, row);
        grid.add(lblObjects, 1, row++);

        grid.add(new Label("Lumières :"), 0, row);
        grid.add(lblLights, 1, row++);

        VBox box = new VBox(grid);
        box.setPadding(new Insets(4));
        return box;
    }

    private VBox createObjectsPane() {
        shapeList.setPlaceholder(new Label("Aucun objet"));
        VBox box = new VBox(shapeList);
        box.setPadding(new Insets(4));
        return box;
    }

    private VBox createLightsPane() {
        lightsList.setPlaceholder(new Label("Aucune lumière"));
        VBox box = new VBox(lightsList);
        box.setPadding(new Insets(4));
        return box;
    }

    /**
     * Met à jour l’inspecteur à partir du document courant.
     */
    public void setDocument(SceneDocument doc) {
        if (doc == null) {
            clear();
            return;
        }

        EditableScene escene = doc.getEditableScene();
        if (escene == null) {
            clear();
            return;
        }

        var cam = escene.getCamera();

        lblSize.setText(escene.getWidth() + " x " + escene.getHeight());
        lblFov.setText(String.format("%.1f°", Math.toDegrees(cam.getFovRadians())));
        lblMaxDepth.setText(Integer.toString(escene.getMaxDepth()));
        lblObjects.setText(Integer.toString(escene.getShapes().size()));
        lblLights.setText(Integer.toString(escene.getLights().size()));

        // ✅ types cohérents maintenant
        shapeList.setItems(FXCollections.observableArrayList(escene.getShapes()));   // List<EditableShape>
        lightsList.setItems(FXCollections.observableArrayList(escene.getLights()));  // List<ILight>
    }


    private void clear() {
        lblSize.setText("");
        lblFov.setText("");
        lblMaxDepth.setText("");
        lblObjects.setText("");
        lblLights.setText("");
        shapeList.setItems(FXCollections.emptyObservableList());
        lightsList.setItems(FXCollections.emptyObservableList());
    }


    public void setEditableScene(EditableScene scene) {
        shapeList.setItems(FXCollections.observableArrayList(scene.getShapes()));
    }

}
