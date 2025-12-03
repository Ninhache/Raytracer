package fr.ninhache.ui;

import fr.ninhache.raytracer.render.RenderQuality;
import fr.ninhache.raytracer.render.RenderResult;
import fr.ninhache.raytracer.render.RenderStats;
import fr.ninhache.ui.SceneInspector;
import fr.ninhache.ui.model.SceneDocument;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class SceneTab extends Tab {

    private final SceneDocument document;
    private final FxRenderService renderService;

    private final ImageView previewView;
    private final Label statusLabel;
    private final SceneInspector inspector;
    private ComboBox<RenderQuality> qualityBox;
    private final BooleanProperty rendering = new SimpleBooleanProperty(false);

    public SceneTab(SceneDocument document, FxRenderService renderService) {
        this.document = document;
        this.renderService = renderService;

        setText(document.getDisplayName());

        BorderPane root = new BorderPane();

        // Toolbar locale de l’onglet
        HBox topBar = createTopBar();
        root.setTop(topBar);

        // Preview centre avec overlay de progression
        previewView = new ImageView();
        previewView.setPreserveRatio(true);
        previewView.setFitWidth(900);
        previewView.setFitHeight(700);

        StackPane previewContainer = createPreviewPane();

        // Inspecteur à droite dans une SplitPane pour ajuster l’espace
        inspector = new SceneInspector();
        inspector.setDocument(document);

        javafx.scene.control.SplitPane splitPane = new javafx.scene.control.SplitPane(
                previewContainer,
                inspector
        );
        splitPane.setDividerPositions(0.65);
        root.setCenter(splitPane);



        // Status bar
        statusLabel = new Label("Prêt à rendre");
        HBox bottom = new HBox(statusLabel);
        bottom.setPadding(new Insets(5, 10, 5, 10));
        bottom.setAlignment(Pos.CENTER_LEFT);
        root.setBottom(bottom);

        setContent(root);
    }

    private HBox createTopBar() {
        qualityBox = new ComboBox<>();
        qualityBox.getItems().addAll(RenderQuality.PREVIEW, RenderQuality.NORMAL, RenderQuality.HIGH);
        qualityBox.setValue(RenderQuality.NORMAL);
        qualityBox.setTooltip(new Tooltip("Choisissez la qualité du rendu (résolution/précision)"));

        Button renderBtn = new Button("Rendu");
        renderBtn.setOnAction(e -> doRender());
        renderBtn.disableProperty().bind(rendering);
        renderBtn.setTooltip(new Tooltip("Lancer le rendu de cette scène"));

        Button saveBtn = new Button("Enregistrer l'image");
        saveBtn.setOnAction(e -> saveLastRender());
        saveBtn.disableProperty().bind(document.getLastRenderProperty().isNull());
        saveBtn.setTooltip(new Tooltip("Exporter le dernier rendu en PNG"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label sceneName = new Label(document.getDisplayName());
        sceneName.setStyle("-fx-font-weight: bold");

        HBox box = new HBox(12, sceneName, new Separator(), qualityBox, renderBtn, saveBtn, spacer);
        box.setPadding(new Insets(5, 10, 5, 10));
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private StackPane createPreviewPane() {
        Label helper = new Label("Ouvrez une scène puis lancez un rendu pour voir l'aperçu.");
        helper.setStyle("-fx-text-fill: derive(-fx-text-base-color, -40%)");

        ProgressIndicator indicator = new ProgressIndicator();
        indicator.visibleProperty().bind(rendering);
        indicator.managedProperty().bind(rendering);

        VBox overlay = new VBox(8, indicator, new Label("Rendu en cours..."));
        overlay.setAlignment(Pos.CENTER);
        overlay.visibleProperty().bind(rendering);
        overlay.managedProperty().bind(rendering);

        StackPane previewContainer = new StackPane(helper, previewView, overlay);
        StackPane.setAlignment(helper, Pos.CENTER);
        StackPane.setAlignment(previewView, Pos.CENTER);
        previewContainer.setPadding(new Insets(10));
        return previewContainer;
    }



    private void doRender() {
        statusLabel.setText("Rendu en cours...");
        rendering.set(true);

        try {
            var sceneToRender = document.buildSceneForRender();

            renderService.renderAsync(
                    sceneToRender,
                    result -> {
                        document.setLastRender(result);
                        updatePreview(result);
                        rendering.set(false);
                    },
                    error -> {
                        statusLabel.setText("Erreur: " + error.getMessage());
                        error.printStackTrace();
                        rendering.set(false);
                    }
            );
        } catch (Exception e) {
            statusLabel.setText("Erreur de construction de la scène: " + e.getMessage());
            e.printStackTrace();
            rendering.set(false);
        }
    }


    private void updatePreview(RenderResult result) {
        var fxImg = SwingFXUtils.toFXImage(result.image(), null);
        previewView.setImage(fxImg);

        RenderStats stats = result.stats();
        statusLabel.setText(String.format(
                "OK · %dx%d, %.1f ms, %d threads, %.1f rays/pixel",
                stats.width(),
                stats.height(),
                stats.durationMillis(),
                stats.threadCount(),
                stats.raysPerPixel()
        ));
    }

    private void saveLastRender() {
        var last = document.getLastRender();
        if (last == null) {
            statusLabel.setText("Aucun rendu à sauvegarder");
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Enregistrer le rendu");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image PNG", "*.png"));
        chooser.setInitialFileName(document.getDisplayName().replaceAll("\\.[^.]+$", "") + "-render.png");

        File target = chooser.showSaveDialog(getTabPane().getScene().getWindow());
        if (target == null) {
            return;
        }

        try {
            ImageIO.write(last.image(), "png", target);
            statusLabel.setText("Image sauvegardée: " + target.getName());
        } catch (IOException e) {
            statusLabel.setText("Impossible d'enregistrer: " + e.getMessage());
        }
    }

}
