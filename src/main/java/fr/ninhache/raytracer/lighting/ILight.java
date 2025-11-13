package fr.ninhache.raytracer.lighting;

import fr.ninhache.raytracer.math.Color;
import fr.ninhache.raytracer.math.Point;
import fr.ninhache.raytracer.math.Vector;

/**
 * Interface représentant une source lumineuse dans la scène
 */
public interface ILight {

    /**
     * @return la couleur/intensité de cette source lumineuse
     */
    Color getColor();

    /**
     * Retourne le vecteur unitaire L allant du point d'impact vers la lumière.
     * (C’est ce qu’on utilise dans NxL pour Lambert)
     */
    Vector incidentFrom(Point hitPoint);

    /**
     * @return une description textuelle de la lumière (pour débogage)
     */
    String describe();
}
