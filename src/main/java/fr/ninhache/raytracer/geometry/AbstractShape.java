package fr.ninhache.raytracer.geometry;

import fr.ninhache.raytracer.scene.Material;

/**
 * Classe abstraite de base pour les formes géométriques.
 *
 * <p>Fournit l'implémentation commune de la gestion d'une forme.
 */
public abstract class AbstractShape implements IShape {

    protected Material material;

    /**
     * Crée une forme avec un matériau noir par défaut.
     */
    protected AbstractShape() {
        this.material = new Material();
    }

    @Override
    public void setMaterial(Material material) {
        this.material = material != null ? material : new Material();
    }

    @Override
    public Material getMaterial() {
        return material;
    }
}