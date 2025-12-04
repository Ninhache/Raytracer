package fr.ninhache.ui.model;

import fr.ninhache.raytracer.geometry.shape.Triangle;
import fr.ninhache.raytracer.math.Point;
import fr.ninhache.raytracer.scene.Material;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public final class EditableTriangle implements EditableShape {

    private double x1, y1, z1;
    private double x2, y2, z2;
    private double x3, y3, z3;
    private EditableMaterial material;

    public EditableTriangle(double x1, double y1, double z1,
                            double x2, double y2, double z2,
                            double x3, double y3, double z3,
                            EditableMaterial material) {
        this.x1 = x1; this.y1 = y1; this.z1 = z1;
        this.x2 = x2; this.y2 = y2; this.z2 = z2;
        this.x3 = x3; this.y3 = y3; this.z3 = z3;
        this.material = material;
    }

    public static EditableTriangle from(Triangle triangle) {
        Point p1 = triangle.getV1();
        Point p2 = triangle.getV2();
        Point p3 = triangle.getV3();
        Material m = triangle.getMaterial();
        return new EditableTriangle(
                p1.x, p1.y, p1.z,
                p2.x, p2.y, p2.z,
                p3.x, p3.y, p3.z,
                EditableMaterial.from(m)
        );
    }

    @Override
    public String name() {
        return "Triangle (" + x1 + ", " + y1 + ", " + z1 + "…)";
    }

    @Override
    public EditableMaterial getMaterial() {
        return material;
    }

    @Override
    public void setMaterial(EditableMaterial mat) {
        this.material = mat;
    }

    @Override
    public Triangle toShape() {
        Triangle t = new Triangle(new Point(x1, y1, z1), new Point(x2, y2, z2), new Point(x3, y3, z3));
        t.setMaterial(material != null ? material.toMaterial() : new Material());
        return t;
    }

    @Override
    public Node createEditorPane() {
        VBox root = new VBox(8);
        root.setPadding(new Insets(8));

        GridPane geo = new GridPane();
        geo.setHgap(5);
        geo.setVgap(4);

        TextField fx1 = new TextField(Double.toString(x1));
        TextField fy1 = new TextField(Double.toString(y1));
        TextField fz1 = new TextField(Double.toString(z1));
        TextField fx2 = new TextField(Double.toString(x2));
        TextField fy2 = new TextField(Double.toString(y2));
        TextField fz2 = new TextField(Double.toString(z2));
        TextField fx3 = new TextField(Double.toString(x3));
        TextField fy3 = new TextField(Double.toString(y3));
        TextField fz3 = new TextField(Double.toString(z3));

        geo.addRow(0, new Label("v1 x:"), fx1);
        geo.addRow(1, new Label("v1 y:"), fy1);
        geo.addRow(2, new Label("v1 z:"), fz1);
        geo.addRow(3, new Label("v2 x:"), fx2);
        geo.addRow(4, new Label("v2 y:"), fy2);
        geo.addRow(5, new Label("v2 z:"), fz2);
        geo.addRow(6, new Label("v3 x:"), fx3);
        geo.addRow(7, new Label("v3 y:"), fy3);
        geo.addRow(8, new Label("v3 z:"), fz3);

        ChangeListener<String> geoListener = (obs, old, val) -> {
            try {
                x1 = Double.parseDouble(fx1.getText());
                y1 = Double.parseDouble(fy1.getText());
                z1 = Double.parseDouble(fz1.getText());
                x2 = Double.parseDouble(fx2.getText());
                y2 = Double.parseDouble(fy2.getText());
                z2 = Double.parseDouble(fz2.getText());
                x3 = Double.parseDouble(fx3.getText());
                y3 = Double.parseDouble(fy3.getText());
                z3 = Double.parseDouble(fz3.getText());
            } catch (NumberFormatException ignored) {
            }
        };

        fx1.textProperty().addListener(geoListener);
        fy1.textProperty().addListener(geoListener);
        fz1.textProperty().addListener(geoListener);
        fx2.textProperty().addListener(geoListener);
        fy2.textProperty().addListener(geoListener);
        fz2.textProperty().addListener(geoListener);
        fx3.textProperty().addListener(geoListener);
        fy3.textProperty().addListener(geoListener);
        fz3.textProperty().addListener(geoListener);

        root.getChildren().add(new TitledPane("Géométrie", geo));
        root.getChildren().add(MaterialEditorBuilder.build(material));
        return root;
    }
}
