package fr.ninhache.ui.model.light;

import fr.ninhache.raytracer.lighting.ILight;
import fr.ninhache.raytracer.math.Color;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Permet d'éditer la lumière ambiante globale de la scène via l'inspecteur.
 */
public final class EditableAmbientLight implements EditableLight {

    private Color color;
    private final Consumer<Color> onColorChanged;

    public EditableAmbientLight(Color color, Consumer<Color> onColorChanged) {
        this.color = color;
        this.onColorChanged = Objects.requireNonNull(onColorChanged);
    }

    @Override
    public String name() {
        return "Lumière ambiante";
    }

    @Override
    public ILight toLight() {
        // La lumière ambiante n'est pas une lumière directionnelle/ponctuelle :
        // elle est appliquée séparément par SceneBuilder.
        return null;
    }

    @Override
    public Node createEditorPane() {
        VBox box = new VBox(8);
        box.setPadding(new Insets(8));

        ColorPicker picker = new ColorPicker(toFx(color));
        picker.valueProperty().addListener((obs, old, val) -> {
            color = new Color(val.getRed(), val.getGreen(), val.getBlue());
            onColorChanged.accept(color);
        });

        box.getChildren().addAll(new Label("Couleur"), picker);
        return box;
    }

    private static javafx.scene.paint.Color toFx(Color c) {
        return new javafx.scene.paint.Color(
                clamp01(c.r()),
                clamp01(c.g()),
                clamp01(c.b()),
                1.0
        );
    }

    private static double clamp01(double v) {
        return Math.max(0, Math.min(1, v));
    }
}
