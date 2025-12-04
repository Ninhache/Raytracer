package fr.ninhache.ui.model;

import fr.ninhache.raytracer.geometry.shape.RegularPolygon;
import fr.ninhache.raytracer.math.Point;
import fr.ninhache.raytracer.math.Vector;
import fr.ninhache.raytracer.scene.Material;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public final class EditableRegularPolygon implements EditableShape {

    private double cx, cy, cz;
    private double nx, ny, nz;
    private double radius;
    private int sides;
    private EditableMaterial material;

    public EditableRegularPolygon(double cx, double cy, double cz, double nx, double ny, double nz, double radius, int sides, EditableMaterial material) {
        this.cx = cx;
        this.cy = cy;
        this.cz = cz;
        this.nx = nx;
        this.ny = ny;
        this.nz = nz;
        this.radius = radius;
        this.sides = sides;
        this.material = material;
    }

    public static EditableRegularPolygon from(RegularPolygon polygon) {
        Point c = polygon.getTriangles().get(0).getV1();
        Vector n = polygon.getTriangles().get(0).getNormal();
        // le centre est commun à tous les triangles (vertex v1)
        Material m = polygon.getMaterial();
        return new EditableRegularPolygon(c.x, c.y, c.z, n.x, n.y, n.z, polygon.getTriangles().get(0).getEdge1().length(), polygon.getTriangles().size(), EditableMaterial.from(m));
    }

    @Override
    public String name() {
        return "RegularPolygon " + sides + " côtés";
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
    public RegularPolygon toShape() {
        RegularPolygon polygon = new RegularPolygon(new Point(cx, cy, cz), radius, sides, new Vector(nx, ny, nz));
        polygon.setMaterial(material != null ? material.toMaterial() : new Material());
        return polygon;
    }

    @Override
    public Node createEditorPane() {
        VBox root = new VBox(8);
        root.setPadding(new Insets(8));

        GridPane geo = new GridPane();
        geo.setHgap(5);
        geo.setVgap(4);

        TextField fcx = new TextField(Double.toString(cx));
        TextField fcy = new TextField(Double.toString(cy));
        TextField fcz = new TextField(Double.toString(cz));
        TextField fnx = new TextField(Double.toString(nx));
        TextField fny = new TextField(Double.toString(ny));
        TextField fnz = new TextField(Double.toString(nz));
        TextField fr = new TextField(Double.toString(radius));
        TextField fsides = new TextField(Integer.toString(sides));

        geo.addRow(0, new Label("cx:"), fcx);
        geo.addRow(1, new Label("cy:"), fcy);
        geo.addRow(2, new Label("cz:"), fcz);
        geo.addRow(3, new Label("nx:"), fnx);
        geo.addRow(4, new Label("ny:"), fny);
        geo.addRow(5, new Label("nz:"), fnz);
        geo.addRow(6, new Label("rayon:"), fr);
        geo.addRow(7, new Label("côtés:"), fsides);

        ChangeListener<String> geoListener = (obs, old, val) -> {
            try {
                cx = Double.parseDouble(fcx.getText());
                cy = Double.parseDouble(fcy.getText());
                cz = Double.parseDouble(fcz.getText());
                nx = Double.parseDouble(fnx.getText());
                ny = Double.parseDouble(fny.getText());
                nz = Double.parseDouble(fnz.getText());
                radius = Double.parseDouble(fr.getText());
                sides = Integer.parseInt(fsides.getText());
            } catch (NumberFormatException ignored) {
            }
        };

        fcx.textProperty().addListener(geoListener);
        fcy.textProperty().addListener(geoListener);
        fcz.textProperty().addListener(geoListener);
        fnx.textProperty().addListener(geoListener);
        fny.textProperty().addListener(geoListener);
        fnz.textProperty().addListener(geoListener);
        fr.textProperty().addListener(geoListener);
        fsides.textProperty().addListener(geoListener);

        root.getChildren().add(new TitledPane("Géométrie", geo));
        root.getChildren().add(MaterialEditorBuilder.build(material));
        return root;
    }
}
