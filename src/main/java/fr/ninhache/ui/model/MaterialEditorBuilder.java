package fr.ninhache.ui.model;

import fr.ninhache.raytracer.math.Color;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;

/**
 * Utilitaire pour générer un panneau d'édition de matériau cohérent entre les différentes formes.
 */
public final class MaterialEditorBuilder {

    private MaterialEditorBuilder() {
    }

    public static TitledPane build(EditableMaterial mat) {
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
                mat.setDiffuse(m.diffuse());
                mat.setSpecular(m.specular());
                mat.setShininess(m.shininess());

                diffR.setText(Double.toString(m.diffuse().r()));
                diffG.setText(Double.toString(m.diffuse().g()));
                diffB.setText(Double.toString(m.diffuse().b()));
                specR.setText(Double.toString(m.specular().r()));
                specG.setText(Double.toString(m.specular().g()));
                specB.setText(Double.toString(m.specular().b()));
                shininess.setText(Double.toString(m.shininess()));
            }
            syncEnabledState.run();
        });

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

        return new TitledPane("Matériau", matGrid);
    }
}