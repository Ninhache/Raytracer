package fr.ninhache.ui.model.shape;

import fr.ninhache.raytracer.geometry.shape.Disk;
import fr.ninhache.raytracer.math.Point;
import fr.ninhache.raytracer.math.Vector;
import fr.ninhache.raytracer.scene.Material;
import fr.ninhache.ui.model.MaterialEditorBuilder;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public final class EditableDisk implements EditableShape {

    private double cx, cy, cz;
    private double nx, ny, nz;
    private double radius;
    private EditableMaterial material;

    public EditableDisk(double cx, double cy, double cz, double nx, double ny, double nz, double radius, EditableMaterial material) {
        this.cx = cx;
        this.cy = cy;
        this.cz = cz;
        this.nx = nx;
        this.ny = ny;
        this.nz = nz;
        this.radius = radius;
        this.material = material;
    }

    public static EditableDisk from(Disk disk) {
        Point c = disk.getCenter();
        Vector n = disk.getNormal();
        Material m = disk.getMaterial();
        return new EditableDisk(c.x, c.y, c.z, n.x, n.y, n.z, disk.getRadius(), EditableMaterial.from(m));
    }

    @Override
    public String name() {
        return "Disk (" + cx + ", " + cy + ", " + cz + ")";
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
    public Disk toShape() {
        Disk disk = new Disk(new Point(cx, cy, cz), new Vector(nx, ny, nz), radius);
        disk.setMaterial(material != null ? material.toMaterial() : new Material());
        return disk;
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

        geo.addRow(0, new Label("cx:"), fcx);
        geo.addRow(1, new Label("cy:"), fcy);
        geo.addRow(2, new Label("cz:"), fcz);
        geo.addRow(3, new Label("nx:"), fnx);
        geo.addRow(4, new Label("ny:"), fny);
        geo.addRow(5, new Label("nz:"), fnz);
        geo.addRow(6, new Label("rayon:"), fr);

        ChangeListener<String> geoListener = (obs, old, val) -> {
            try {
                cx = Double.parseDouble(fcx.getText());
                cy = Double.parseDouble(fcy.getText());
                cz = Double.parseDouble(fcz.getText());
                nx = Double.parseDouble(fnx.getText());
                ny = Double.parseDouble(fny.getText());
                nz = Double.parseDouble(fnz.getText());
                radius = Double.parseDouble(fr.getText());
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

        root.getChildren().add(new TitledPane("Géométrie", geo));
        root.getChildren().add(MaterialEditorBuilder.build(material));
        return root;
    }
}