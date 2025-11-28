package fr.ninhache.ui;

import fr.ninhache.raytracer.math.Color;
import fr.ninhache.ui.model.EditableMaterial;
import fr.ninhache.ui.model.EditableSphere;
import fr.ninhache.ui.model.MaterialPreset;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;

public class SphereEditorPane extends ShapeEditorPane {

    @Override
    protected void rebuildUI() {
        getChildren().clear();

        if (!(shape instanceof EditableSphere sphere)) {
            getChildren().add(new Label("Type non supporté pour cet éditeur."));
            return;
        }

        setPadding(new Insets(8));
        setSpacing(8);

        // Position & rayon
        GridPane geoGrid = new GridPane();
        geoGrid.setHgap(5);
        geoGrid.setVgap(4);

        TextField fx = new TextField(Double.toString(sphere.getCx()));
        TextField fy = new TextField(Double.toString(sphere.getCy()));
        TextField fz = new TextField(Double.toString(sphere.getCz()));
        TextField fr = new TextField(Double.toString(sphere.getRadius()));

        geoGrid.addRow(0, new Label("cx:"), fx);
        geoGrid.addRow(1, new Label("cy:"), fy);
        geoGrid.addRow(2, new Label("cz:"), fz);
        geoGrid.addRow(3, new Label("rayon:"), fr);

        ChangeListener<String> geoListener = (obs, old, val) -> {
            try {

                sphere.setCx(Double.parseDouble(fx.getText()));
                sphere.setCy(Double.parseDouble(fy.getText()));
                sphere.setCz(Double.parseDouble(fz.getText()));
                sphere.setRadius(Double.parseDouble(fr.getText()));
            } catch (NumberFormatException ignored) {
            }
        };
        fx.textProperty().addListener(geoListener);
        fy.textProperty().addListener(geoListener);
        fz.textProperty().addListener(geoListener);
        fr.textProperty().addListener(geoListener);

        getChildren().add(new TitledPane("Géométrie", geoGrid));

        // Matériau
        EditableMaterial mat = sphere.getMaterial();

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

        // Gestion preset -> disable ou non
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
                // update champs texte pour refléter les valeurs
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

        // Quand on édite à la main (preset CUSTOM)
        ChangeListener<String> matListener = (obs, old, val) -> {
            try {
                mat.setDiffuse(new Color(
                        Double.parseDouble(diffR.getText()),
                        Double.parseDouble(diffG.getText()),
                        Double.parseDouble(diffB.getText())
                ));
                mat.setSpecular(new Color(
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

        getChildren().add(new TitledPane("Matériau", matGrid));
    }
}
