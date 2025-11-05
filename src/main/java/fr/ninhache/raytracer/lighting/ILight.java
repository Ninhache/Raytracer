package fr.ninhache.raytracer.lighting;

import fr.ninhache.raytracer.math.Color;

/**
 * Interface représentant une source lumineuse dans la scène
 */
public interface ILight {

    /**
     * @return la couleur/intensité de cette source lumineuse
     */
    Color getColor();

    /**
     * @return une description textuelle de la lumière (pour débogage)
     */
    String describe();
}
