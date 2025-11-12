package fr.ninhache.raytracer.scene;

import fr.ninhache.raytracer.math.Color;

/**
 * Représente les propriétés matérielles d'une surface (diffuse, spéculaire, brillance).
 *
 * <p>Le matériau définit comment une surface réagit à la lumière :
 * <ul>
 *   <li><strong>Diffuse</strong> : réflexion diffuse selon la loi de Lambert (surface mate)</li>
 *   <li><strong>Spéculaire</strong> : réflexion spéculaire, simulant les reflets brillants</li>
 *   <li><strong>Brillance (shininess)</strong> : contrôle la concentration du reflet spéculaire
 *       - une valeur élevée produit un petit point lumineux net (surface polie),
 *       une valeur faible produit un reflet large et doux (surface rugueuse)</li>
 * </ul>
 */
public class Material {

    private final Color diffuse;
    private final Color specular;
    private final double shininess; // Contrôle la "dureté" du reflet (Basé sur Phong)
    /*
    private final double reflectivity; // Taux de réflexion (0 = mat, 1 = miroir)
    private final double transparency; // 0 = opaque, 1 = totalement transparent
    private final double refractiveIndex; // Indice de réfraction (verre ~1.5)
     */

    /**
     * Crée un matériau noir (par défaut).
     */
    public Material() {
        this(Color.BLACK, Color.BLACK, 32.0);
    }

    /**
     * Crée un matériau avec les propriétés spécifiées.
     *
     * @param diffuse couleur diffuse
     * @param specular couleur spéculaire
     */
    public Material(Color diffuse, Color specular) {
        this(diffuse, specular, 32.0);
    }

    public Material(Color diffuse, Color specular, double shininess) {
        this.diffuse = diffuse;
        this.specular = specular;
        this.shininess = shininess;
    }

    /**
     * @return la couleur diffuse
     */
    public Color getDiffuse() {
        return diffuse;
    }

    /**
     * @return la couleur spéculaire
     */
    public Color getSpecular() {
        return specular;
    }

    /**
     * @return la brillance
     */
    public double getShininess() {
        return shininess;
    }

    @Override
    public String toString() {
        return String.format("Material[diffuse=%s, specular=%s]", diffuse, specular);
    }
}