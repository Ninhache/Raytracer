package fr.ninhache.ui.model.light;

import fr.ninhache.raytracer.lighting.ILight;
import fr.ninhache.raytracer.lighting.PointLight;
import fr.ninhache.raytracer.math.Color;
import fr.ninhache.raytracer.math.Point;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public final class EditablePointLight implements EditableLight {

    private double x, y, z;
    private Color color;

    public EditablePointLight(double x, double y, double z, Color color) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.color = color;
    }

    public static EditablePointLight from(PointLight light) {
        Point p = light.getPosition();
        return new EditablePointLight(p.x, p.y, p.z, light.getColor());
    }

    @Override
    public String name() {
        return "PointLight (" + x + ", " + y + ", " + z + ")";
    }

    @Override
    public ILight toLight() {
        return new PointLight(new Point(x, y, z), color);
    }

    @Override
    public Node createEditorPane() {
        VBox root = new VBox(8);
        root.setPadding(new Insets(8));

        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(4);

        TextField fx = new TextField(Double.toString(x));
        TextField fy = new TextField(Double.toString(y));
        TextField fz = new TextField(Double.toString(z));

        grid.addRow(0, new Label("x:"), fx);
        grid.addRow(1, new Label("y:"), fy);
        grid.addRow(2, new Label("z:"), fz);

        ChangeListener<String> listener = (obs, old, val) -> {
            try {
                x = Double.parseDouble(fx.getText());
                y = Double.parseDouble(fy.getText());
                z = Double.parseDouble(fz.getText());
            } catch (NumberFormatException ignored) {
            }
        };

        fx.textProperty().addListener(listener);
        fy.textProperty().addListener(listener);
        fz.textProperty().addListener(listener);

        ColorPicker picker = new ColorPicker(toFx(color));
        picker.valueProperty().addListener((obs, old, val) -> {
            color = new Color(val.getRed(), val.getGreen(), val.getBlue());
        });

        root.getChildren().add(new TitledPane("Position", grid));
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