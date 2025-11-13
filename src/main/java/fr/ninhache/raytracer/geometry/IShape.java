package fr.ninhache.raytracer.geometry;

import fr.ninhache.raytracer.scene.Material;

/**
 * Interface représentant une forme géométrique dans la scène.
 *
 */
public interface IShape extends Intersectable {

    /**
     * Définit le matériau de cette forme.
     *
     * @param material le matériau (diffuse + specular)
     */
    void setMaterial(Material material);

    /**
     * @return le matériau de cette forme
     */
    Material getMaterial();

    /**
     * @return une description textuelle de la forme
     */
    String describe();
}


