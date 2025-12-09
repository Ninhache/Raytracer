package fr.ninhache.ui.model.shape;

import fr.ninhache.raytracer.geometry.shape.Plane;
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

public final class EditablePlane implements EditableShape {

    private double px, py, pz;
    private double nx, ny, nz;
    private EditableMaterial material;

    public EditablePlane(double px, double py, double pz, double nx, double ny, double nz, EditableMaterial material) {
        this.px = px;
        this.py = py;
        this.pz = pz;
        this.nx = nx;
        this.ny = ny;
        this.nz = nz;
        this.material = material;
    }

    public static EditablePlane from(Plane plane) {
        Point p = plane.getPoint();
        Vector n = plane.getNormal();
        Material m = plane.getMaterial();
        return new EditablePlane(p.x, p.y, p.z, n.x, n.y, n.z, EditableMaterial.from(m));
    }

    @Override
    public String name() {
        return "Plane (" + px + ", " + py + ", " + pz + ")";
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
    public Plane toShape() {
        Plane plane = new Plane(new Point(px, py, pz), new Vector(nx, ny, nz));
        plane.setMaterial(material != null ? material.toMaterial() : new Material());
        return plane;
    }

    @Override
    public Node createEditorPane() {
        VBox root = new VBox(8);
        root.setPadding(new Insets(8));

        GridPane geo = new GridPane();
        geo.setHgap(5);
        geo.setVgap(4);

        TextField fpx = new TextField(Double.toString(px));
        TextField fpy = new TextField(Double.toString(py));
        TextField fpz = new TextField(Double.toString(pz));
        TextField fnx = new TextField(Double.toString(nx));
        TextField fny = new TextField(Double.toString(ny));
        TextField fnz = new TextField(Double.toString(nz));

        geo.addRow(0, new Label("px:"), fpx);
        geo.addRow(1, new Label("py:"), fpy);
        geo.addRow(2, new Label("pz:"), fpz);
        geo.addRow(3, new Label("nx:"), fnx);
        geo.addRow(4, new Label("ny:"), fny);
        geo.addRow(5, new Label("nz:"), fnz);

        ChangeListener<String> geoListener = (obs, old, val) -> {
            try {
                px = Double.parseDouble(fpx.getText());
                py = Double.parseDouble(fpy.getText());
                pz = Double.parseDouble(fpz.getText());
                nx = Double.parseDouble(fnx.getText());
                ny = Double.parseDouble(fny.getText());
                nz = Double.parseDouble(fnz.getText());
            } catch (NumberFormatException ignored) {
            }
        };

        fpx.textProperty().addListener(geoListener);
        fpy.textProperty().addListener(geoListener);
        fpz.textProperty().addListener(geoListener);
        fnx.textProperty().addListener(geoListener);
        fny.textProperty().addListener(geoListener);
        fnz.textProperty().addListener(geoListener);

        root.getChildren().add(new TitledPane("Géométrie", geo));
        root.getChildren().add(MaterialEditorBuilder.build(material));
        return root;
    }
}