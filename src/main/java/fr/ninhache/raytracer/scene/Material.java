package fr.ninhache.raytracer.scene;

import fr.ninhache.raytracer.math.Color;

/**
 * Représente les propriétés matérielles d'une surface (diffuse, spéculaire)
 *
 * <p>Le matériau définit comment une surface réfléchit la lumière :
 * <ul>
 *   <li><strong>Diffuse</strong> : réflexion diffuse (surface mate, loi de Lambert)</li>
 *   <li><strong>Spéculaire</strong> : réflexion spéculaire (miroir, reflets brillants)</li>
 * </ul>
 */
public class Material {

    private final Color diffuse;
    private final Color specular;

    /**
     * Crée un matériau noir (par défaut).
     */
    public Material() {
        this(Color.BLACK, Color.BLACK);
    }

    /**
     * Crée un matériau avec les propriétés spécifiées.
     *
     * @param diffuse couleur diffuse
     * @param specular couleur spéculaire
     */
    public Material(Color diffuse, Color specular) {
        this.diffuse = diffuse;
        this.specular = specular;
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

    @Override
    public String toString() {
        return String.format("Material[diffuse=%s, specular=%s]", diffuse, specular);
    }
}