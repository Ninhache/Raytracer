package fr.ninhache.ui.model;

import fr.ninhache.raytracer.geometry.shape.Sphere;
import fr.ninhache.raytracer.math.Point;
import fr.ninhache.raytracer.scene.Material;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public final class EditableSphere implements EditableShape {

    private double cx, cy, cz;
    private double radius;
    private EditableMaterial material;

    public EditableSphere(double cx, double cy, double cz, double radius, EditableMaterial material) {
        this.cx = cx;
        this.cy = cy;
        this.cz = cz;
        this.radius = radius;
        this.material = material;
    }

    public static EditableSphere from(Sphere s) {
        Point c = s.getCenter();
        Material m = s.getMaterial();
        return new EditableSphere(
                c.x, c.y, c.z,
                s.getRadius(),
                EditableMaterial.from(m)
        );
    }

    @Override
    public String name() {
        return "Sphere (" + cx + ", " + cy + ", " + cz + ")";
    }

    @Override
    public EditableMaterial getMaterial() {
        return material;
    }

    @Override
    public void setMaterial(EditableMaterial material) {
        this.material = material;
    }

    @Override
    public Sphere toShape() {
        return new Sphere(new Point(cx, cy, cz), radius);
    }

    @Override
    public Node createEditorPane() {
        VBox root = new VBox(8);
        root.setPadding(new Insets(8));

        GridPane geoGrid = new GridPane();
        geoGrid.setHgap(5);
        geoGrid.setVgap(4);

        TextField fx = new TextField(Double.toString(cx));
        TextField fy = new TextField(Double.toString(cy));
        TextField fz = new TextField(Double.toString(cz));
        TextField fr = new TextField(Double.toString(radius));

        geoGrid.addRow(0, new Label("cx:"), fx);
        geoGrid.addRow(1, new Label("cy:"), fy);
        geoGrid.addRow(2, new Label("cz:"), fz);
        geoGrid.addRow(3, new Label("rayon:"), fr);

        ChangeListener<String> geoListener = (obs, old, val) -> {
            try {
                cx = Double.parseDouble(fx.getText());
                cy = Double.parseDouble(fy.getText());
                cz = Double.parseDouble(fz.getText());
                radius = Double.parseDouble(fr.getText());
            } catch (NumberFormatException ignored) {
            }
        };
        fx.textProperty().addListener(geoListener);
        fy.textProperty().addListener(geoListener);
        fz.textProperty().addListener(geoListener);
        fr.textProperty().addListener(geoListener);

        root.getChildren().add(new TitledPane("Géométrie", geoGrid));

        root.getChildren().add(buildMaterialPane());

        return root;
    }

    private Node buildMaterialPane() {
        EditableMaterial mat = this.material;

        ComboBox<MaterialPreset> presetBox = new ComboBox<>();
        presetBox.getItems().addAll(MaterialPreset.values());
        presetBox.setValue(mat.getPreset());

        TextField diffR = new TextField(Double.toString(mat.getDiffuse().r()));
        TextField diffG = new TextField(Double.toString(mat.getDiffuse().g()));
        TextField diffB = new TextField(Double.toString(mat.getDiffuse().b()));

        TextField specR = new TextField(Double.toString(mat.getSpecular().r()));
        TextField specG = new TextField(Double.toString(mat.getSpecular().g()));
        TextField specB = new TextField(Double.toString(mat.getSpecular().b()));

        TextField shininess = new TextField(Double.toString(mat.getShininess()));

        GridPane matGrid = new GridPane();
        matGrid.setHgap(5);
        matGrid.setVgap(4);

        matGrid.addRow(0, new Label("Preset:"), presetBox);
        matGrid.addRow(1, new Label("Diffuse r:"), diffR);
        matGrid.addRow(2, new Label("Diffuse g:"), diffG);
        matGrid.addRow(3, new Label("Diffuse b:"), diffB);
        matGrid.addRow(4, new Label("Spec r:"), specR);
        matGrid.addRow(5, new Label("Spec g:"), specG);
        matGrid.addRow(6, new Label("Spec b:"), specB);
        matGrid.addRow(7, new Label("Shininess:"), shininess);

        // Gestion custom / preset
        Runnable syncEnabledState = () -> {
            boolean custom = presetBox.getValue() == MaterialPreset.CUSTOM;
            diffR.setDisable(!custom);
            diffG.setDisable(!custom);
            diffB.setDisable(!custom);
            specR.setDisable(!custom);
            specG.setDisable(!custom);
            specB.setDisable(!custom);
            shininess.setDisable(!custom);
        };

        presetBox.valueProperty().addListener((obs, old, val) -> {
            mat.setPreset(val);
            if (val != MaterialPreset.CUSTOM) {
                var m = val.toMaterial();
                mat.setDiffuse(m.getDiffuse());
                mat.setSpecular(m.getSpecular());
                mat.setShininess(m.getShininess());

                diffR.setText(Double.toString(m.getDiffuse().r()));
                diffG.setText(Double.toString(m.getDiffuse().g()));
                diffB.setText(Double.toString(m.getDiffuse().b()));
                specR.setText(Double.toString(m.getSpecular().r()));
                specG.setText(Double.toString(m.getSpecular().g()));
                specB.setText(Double.toString(m.getSpecular().b()));
                shininess.setText(Double.toString(m.getShininess()));
            }
            syncEnabledState.run();
        });

        // Édition manuelle = preset CUSTOM
        ChangeListener<String> matListener = (obs, old, val) -> {
            try {
                mat.setDiffuse(new fr.ninhache.raytracer.math.Color(
                        Double.parseDouble(diffR.getText()),
                        Double.parseDouble(diffG.getText()),
                        Double.parseDouble(diffB.getText())
                ));
                mat.setSpecular(new fr.ninhache.raytracer.math.Color(
                        Double.parseDouble(specR.getText()),
                        Double.parseDouble(specG.getText()),
                        Double.parseDouble(specB.getText())
                ));
                mat.setShininess(Double.parseDouble(shininess.getText()));

                mat.setPreset(MaterialPreset.CUSTOM);
                presetBox.setValue(MaterialPreset.CUSTOM);
            } catch (NumberFormatException ignored) {
            }
        };
        diffR.textProperty().addListener(matListener);
        diffG.textProperty().addListener(matListener);
        diffB.textProperty().addListener(matListener);
        specR.textProperty().addListener(matListener);
        specG.textProperty().addListener(matListener);
        specB.textProperty().addListener(matListener);
        shininess.textProperty().addListener(matListener);

        syncEnabledState.run();

        return new TitledPane("Matériau", matGrid);
    }

    public double getCx() {
        return cx;
    }

    public void setCx(double cx) {
        this.cx = cx;
    }

    public double getCy() {
        return cy;
    }

    public void setCy(double cy) {
        this.cy = cy;
    }

    public double getCz() {
        return cz;
    }

    public void setCz(double cz) {
        this.cz = cz;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }
}
