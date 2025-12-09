package fr.ninhache.ui;

import fr.ninhache.raytracer.scene.Scene;
import fr.ninhache.raytracer.scene.SceneLoader;
import fr.ninhache.ui.model.SceneDocument;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

public class MainView extends BorderPane {

    private final FxRenderService renderService = new FxRenderService();
    private final TabPane tabPane = new TabPane();
    private final VBox emptyState = createEmptyState();

    public MainView() {
        setPadding(new Insets(10));

        StackPane center = new StackPane(emptyState, tabPane);
        StackPane.setAlignment(emptyState, Pos.CENTER);
        emptyState.visibleProperty().bind(Bindings.isEmpty(tabPane.getTabs()));
        emptyState.managedProperty().bind(emptyState.visibleProperty());
        tabPane.visibleProperty().bind(Bindings.isNotEmpty(tabPane.getTabs()));
        tabPane.managedProperty().bind(tabPane.visibleProperty());
        setCenter(center);

    }

    private Button createOpenSceneButton() {
        Button openSceneBtn = new Button("Ouvrir une scène...");
        openSceneBtn.setOnAction(e -> openSceneDialog());
        return openSceneBtn;
    }

    private VBox createEmptyState() {
        Label title = new Label("Aucune scène ouverte");
        title.getStyleClass().add("empty-title");

        Label hint = new Label("Chargez un fichier pour commencer à explorer et rendre une scène.");
        hint.getStyleClass().add("empty-hint");

        Button openButton = createOpenSceneButton();

        VBox box = new VBox(10, title, hint, openButton);
        box.setPadding(new Insets(20));
        box.setMaxWidth(360);
        box.getStyleClass().add("empty-state");
        return box;
    }


    private void openSceneDialog() {
        Window window = getScene() != null ? getScene().getWindow() : null;

        FileChooser fc = new FileChooser();
        fc.setTitle("Ouvrir un fichier de scène");
        fc.getExtensionFilters().addAll(
                // todo: reflection pour charger les types pris en compte
                new FileChooser.ExtensionFilter("Fichiers de scène", "*.txt", "*.test", "*.scene"),
                new FileChooser.ExtensionFilter("Tous les fichiers", "*.*")
        );

        File file = fc.showOpenDialog(window);
        if (file == null) {
            return;
        }

        openSceneFromFile(file);
    }

    private void openSceneFromFile(File file) {
        try {
            SceneLoader loader = new SceneLoader();
            Scene scene = loader.load(file.getAbsolutePath());

            String displayName = file.getName();
            SceneDocument doc = new SceneDocument(file.getAbsolutePath(), displayName, scene);

            SceneTab tab = new SceneTab(doc, renderService);
            tabPane.getTabs().add(tab);
            tabPane.getSelectionModel().select(tab);

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de chargement");
            alert.setHeaderText("Impossible de charger la scène");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}