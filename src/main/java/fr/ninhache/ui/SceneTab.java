package fr.ninhache.ui;

import fr.ninhache.raytracer.render.RenderQuality;
import fr.ninhache.raytracer.render.RenderResult;
import fr.ninhache.raytracer.render.RenderStats;
import fr.ninhache.ui.SceneInspector;
import fr.ninhache.ui.model.SceneDocument;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

public class SceneTab extends Tab {

    private final SceneDocument document;
    private final FxRenderService renderService;

    private final ImageView previewView;
    private final Label statusLabel;
    private final SceneInspector inspector;

    public SceneTab(SceneDocument document, FxRenderService renderService) {
        this.document = document;
        this.renderService = renderService;

        setText(document.getDisplayName());

        BorderPane root = new BorderPane();

        // Toolbar locale de l’onglet
        HBox topBar = createTopBar();
        root.setTop(topBar);

        // Preview centre
        previewView = new ImageView();
        previewView.setPreserveRatio(true);
        previewView.setFitWidth(900);
        previewView.setFitHeight(700);

        StackPane previewContainer = new StackPane(previewView);
        previewContainer.setPadding(new Insets(10));
        root.setCenter(previewContainer);

        // Inspecteur à droite
        inspector = new SceneInspector();
        inspector.setDocument(document);
        root.setRight(inspector);

        // Status bar
        statusLabel = new Label("Prêt à rendre");
        HBox bottom = new HBox(statusLabel);
        bottom.setPadding(new Insets(5, 10, 5, 10));
        bottom.setAlignment(Pos.CENTER_LEFT);
        root.setBottom(bottom);

        setContent(root);
    }

    private HBox createTopBar() {
        var qualityBox = new ComboBox<RenderQuality>();
        qualityBox.getItems().addAll(RenderQuality.PREVIEW, RenderQuality.NORMAL, RenderQuality.HIGH);
        qualityBox.setValue(RenderQuality.NORMAL);

        Button renderBtn = new Button("Render");
        renderBtn.setOnAction(e -> doRender(/* plus tard: qualityBox.getValue() */));

        HBox box = new HBox(10, qualityBox, renderBtn);
        box.setPadding(new Insets(5, 10, 5, 10));
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private void doRender() {
        statusLabel.setText("Rendu en cours...");
        setDisable(true);

        try {
            var sceneToRender = document.buildSceneForRender();

            renderService.renderAsync(
                    document.debugScene,
                    result -> {
//                        document.setLastRender(result);

                        updatePreview(result);
                        setDisable(false);
                    },
                    error -> {
                        statusLabel.setText("Erreur: " + error.getMessage());
                        error.printStackTrace();
                        setDisable(false);
                    }
            );
        } catch (Exception e) {
            statusLabel.setText("Erreur de construction de la scène: " + e.getMessage());
            e.printStackTrace();
            setDisable(false);
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
}
