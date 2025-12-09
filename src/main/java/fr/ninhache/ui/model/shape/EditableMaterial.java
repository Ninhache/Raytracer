package fr.ninhache.ui.model.shape;

import fr.ninhache.raytracer.math.Color;
import fr.ninhache.raytracer.scene.Material;
import fr.ninhache.ui.model.MaterialPreset;

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
        if (mat == null) {
            return new EditableMaterial(new Color(0.2, 0.2, 0.2), new Color(0.2, 0.2, 0.2), 32.0, MaterialPreset.CUSTOM);
        }

        // par défaut on considère que ça vient d’un custom
        return new EditableMaterial(
                mat.diffuse(),
                mat.specular(),
                mat.shininess(),
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
