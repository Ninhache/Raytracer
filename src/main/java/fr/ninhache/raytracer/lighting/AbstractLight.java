package fr.ninhache.raytracer.lighting;

import fr.ninhache.raytracer.math.Color;

/**
 * Classe abstraite de base pour les sources lumineuses
 */
public abstract class AbstractLight implements ILight {

    protected final Color color;

    /**
     * Crée une source lumineuse avec la couleur/intensité spécifiée.
     *
     * @param color la couleur de la lumière (composantes entre 0 et 1)
     * @throws IllegalArgumentException si color est null
     */
    protected AbstractLight(Color color) {
        if (color == null) {
            throw new IllegalArgumentException("La couleur ne peut pas être null");
        }
        this.color = color;
    }

    @Override
    public Color getColor() {
        return color;
    }
}
