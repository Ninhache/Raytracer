package fr.ninhache.ui.model.light;

import fr.ninhache.raytracer.lighting.DirectionalLight;
import fr.ninhache.raytracer.lighting.ILight;
import fr.ninhache.raytracer.math.Color;
import fr.ninhache.raytracer.math.Vector;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public final class EditableDirectionalLight implements EditableLight {

    private double dx, dy, dz;
    private Color color;

    public EditableDirectionalLight(double dx, double dy, double dz, Color color) {
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
        this.color = color;
    }

    public static EditableDirectionalLight from(DirectionalLight light) {
        Vector d = light.getDirection();
        return new EditableDirectionalLight(d.x, d.y, d.z, light.getColor());
    }

    @Override
    public String name() {
        return "Directional (" + dx + ", " + dy + ", " + dz + ")";
    }

    @Override
    public ILight toLight() {
        Vector dir = new Vector(dx, dy, dz);
        if (dir.isZero(1e-10)) {
            dir = new Vector(0, -1, 0);
        }
        return new DirectionalLight(dir, color);
    }

    @Override
    public Node createEditorPane() {
        VBox root = new VBox(8);
        root.setPadding(new Insets(8));

        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(4);

        TextField fdx = new TextField(Double.toString(dx));
        TextField fdy = new TextField(Double.toString(dy));
        TextField fdz = new TextField(Double.toString(dz));

        grid.addRow(0, new Label("dx:"), fdx);
        grid.addRow(1, new Label("dy:"), fdy);
        grid.addRow(2, new Label("dz:"), fdz);

        ChangeListener<String> listener = (obs, old, val) -> {
            try {
                dx = Double.parseDouble(fdx.getText());
                dy = Double.parseDouble(fdy.getText());
                dz = Double.parseDouble(fdz.getText());
            } catch (NumberFormatException ignored) {
            }
        };

        fdx.textProperty().addListener(listener);
        fdy.textProperty().addListener(listener);
        fdz.textProperty().addListener(listener);

        ColorPicker picker = new ColorPicker(toFx(color));
        picker.valueProperty().addListener((obs, old, val) -> {
            color = new Color(val.getRed(), val.getGreen(), val.getBlue());
        });

        root.getChildren().add(new TitledPane("Direction", grid));
        root.getChildren().add(new TitledPane("Couleur", picker));
        return root;
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
