package fr.ninhache.ui;

import fr.ninhache.raytracer.scene.Scene;
import fr.ninhache.raytracer.scene.SceneLoader;
import fr.ninhache.ui.model.SceneDocument;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

public class MainView extends BorderPane {

    private final FxRenderService renderService = new FxRenderService();
    private final TabPane tabPane = new TabPane();

    public MainView() {
        setPadding(new Insets(10));

        // Top toolbar global
        var topBar = createGlobalToolbar();
        setTop(topBar);

        // Centre : TabPane
        setCenter(tabPane);
    }

    private HBox createGlobalToolbar() {
        Button openSceneBtn = new Button("Ouvrir une scène...");
        openSceneBtn.setOnAction(e -> openSceneDialog());

        HBox box = new HBox(10, openSceneBtn);
        box.setPadding(new Insets(5, 10, 5, 10));
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
            // tu utilises déjà loader.load(String path) dans ton Main
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