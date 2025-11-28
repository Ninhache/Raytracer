package fr.ninhache.ui.model;

import fr.ninhache.raytracer.math.Color;
import fr.ninhache.raytracer.scene.Material;

public final class EditableMaterial {
    private Color diffuse;
    private Color specular;
    private double shininess;
    private MaterialPreset preset;

    public EditableMaterial(Color diffuse, Color specular, double shininess, MaterialPreset preset) {
        this.diffuse = diffuse;
        this.specular = specular;
        this.shininess = shininess;
        this.preset = preset;
    }

    public static EditableMaterial from(Material mat) {
        // par défaut on considère que ça vient d’un custom
        return new EditableMaterial(
                mat.getDiffuse(),
                mat.getSpecular(),
                mat.getShininess(),
                MaterialPreset.CUSTOM
        );
    }

    public Material toMaterial() {
        return new Material(diffuse, specular, shininess);
    }

    public Color getDiffuse() {
        return diffuse;
    }

    public void setDiffuse(Color diffuse) {
        this.diffuse = diffuse;
    }

    public Color getSpecular() {
        return specular;
    }

    public void setSpecular(Color specular) {
        this.specular = specular;
    }

    public double getShininess() {
        return shininess;
    }

    public void setShininess(double shininess) {
        this.shininess = shininess;
    }

    public MaterialPreset getPreset() {
        return preset;
    }

    public void setPreset(MaterialPreset preset) {
        this.preset = preset;
    }
}
