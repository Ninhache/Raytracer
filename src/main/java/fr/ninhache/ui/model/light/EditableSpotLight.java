package fr.ninhache.ui.model.light;

import fr.ninhache.raytracer.lighting.ILight;
import fr.ninhache.raytracer.lighting.SpotLight;
import fr.ninhache.raytracer.math.Color;
import fr.ninhache.raytracer.math.Point;
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

public final class EditableSpotLight implements EditableLight {

    private double px, py, pz;
    private double dx, dy, dz;
    private double cone;
    private double penumbra;
    private Color color;

    public EditableSpotLight(double px, double py, double pz,
                             double dx, double dy, double dz,
                             double cone, double penumbra,
                             Color color) {
        this.px = px;
        this.py = py;
        this.pz = pz;
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
        this.cone = cone;
        this.penumbra = penumbra;
        this.color = color;
    }

    public static EditableSpotLight from(SpotLight light) {
        Point p = light.getPosition();
        Vector d = light.getDirection();
        return new EditableSpotLight(
                p.x, p.y, p.z,
                d.x, d.y, d.z,
                light.getConeAngleDegrees(),
                light.getPenumbraAngleDegrees(),
                light.getColor()
        );
    }

    @Override
    public String name() {
        return "SpotLight (" + px + ", " + py + ", " + pz + ")";
    }

    @Override
    public ILight toLight() {
        Vector dir = new Vector(dx, dy, dz);
        if (dir.isZero(1e-10)) {
            dir = new Vector(0, -1, 0);
        }
        double safeCone = cone <= 0 ? 30 : Math.min(cone, 180);
        double safePenumbra = Math.min(Math.max(0, penumbra), safeCone);
        return new SpotLight(new Point(px, py, pz), dir, safeCone, safePenumbra, color);
    }

    @Override
    public Node createEditorPane() {
        VBox root = new VBox(8);
        root.setPadding(new Insets(8));

        GridPane pos = new GridPane();
        pos.setHgap(5);
        pos.setVgap(4);

        TextField fpx = new TextField(Double.toString(px));
        TextField fpy = new TextField(Double.toString(py));
        TextField fpz = new TextField(Double.toString(pz));

        pos.addRow(0, new Label("px:"), fpx);
        pos.addRow(1, new Label("py:"), fpy);
        pos.addRow(2, new Label("pz:"), fpz);

        GridPane dirPane = new GridPane();
        dirPane.setHgap(5);
        dirPane.setVgap(4);

        TextField fdx = new TextField(Double.toString(dx));
        TextField fdy = new TextField(Double.toString(dy));
        TextField fdz = new TextField(Double.toString(dz));

        dirPane.addRow(0, new Label("dx:"), fdx);
        dirPane.addRow(1, new Label("dy:"), fdy);
        dirPane.addRow(2, new Label("dz:"), fdz);

        GridPane conePane = new GridPane();
        conePane.setHgap(5);
        conePane.setVgap(4);

        TextField fCone = new TextField(Double.toString(cone));
        TextField fPenumbra = new TextField(Double.toString(penumbra));

        conePane.addRow(0, new Label("Cone (deg):"), fCone);
        conePane.addRow(1, new Label("Pénombre:"), fPenumbra);

        ChangeListener<String> positionListener = (obs, old, val) -> {
            try {
                px = Double.parseDouble(fpx.getText());
                py = Double.parseDouble(fpy.getText());
                pz = Double.parseDouble(fpz.getText());
            } catch (NumberFormatException ignored) {
            }
        };

        fpx.textProperty().addListener(positionListener);
        fpy.textProperty().addListener(positionListener);
        fpz.textProperty().addListener(positionListener);

        ChangeListener<String> dirListener = (obs, old, val) -> {
            try {
                dx = Double.parseDouble(fdx.getText());
                dy = Double.parseDouble(fdy.getText());
                dz = Double.parseDouble(fdz.getText());
            } catch (NumberFormatException ignored) {
            }
        };

        fdx.textProperty().addListener(dirListener);
        fdy.textProperty().addListener(dirListener);
        fdz.textProperty().addListener(dirListener);

        ChangeListener<String> angleListener = (obs, old, val) -> {
            try {
                cone = Double.parseDouble(fCone.getText());
                penumbra = Double.parseDouble(fPenumbra.getText());
            } catch (NumberFormatException ignored) {
            }
        };

        fCone.textProperty().addListener(angleListener);
        fPenumbra.textProperty().addListener(angleListener);

        ColorPicker picker = new ColorPicker(toFx(color));
        picker.valueProperty().addListener((obs, old, val) -> {
            color = new Color(val.getRed(), val.getGreen(), val.getBlue());
        });

        root.getChildren().add(new TitledPane("Position", pos));
        root.getChildren().add(new TitledPane("Direction", dirPane));
        root.getChildren().add(new TitledPane("Angles", conePane));
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
