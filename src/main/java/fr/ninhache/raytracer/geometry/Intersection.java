package fr.ninhache.raytracer.geometry;

import fr.ninhache.raytracer.math.Point;
import fr.ninhache.raytracer.math.Vector;

/**
 * Représente une intersection entre un rayon et une forme géométrique.
 *
 * <p>Un hit est caractérisé par :
 * <ul>
 *   <li>t : paramètre le long du rayon (distance paramétrique)</li>
 *   <li>point : point d'intersection dans l'espace</li>
 *   <li>normal : normale de la surface au point d'impact (unitaire)</li>
 *   <li>shape : forme intersectée</li>
 * </ul>
 */
public final class Intersection {

    /** Paramètre t tel que P = origin + t * direction. */
    public final double t;

    /** Point d'intersection dans l'espace. */
    public final Point point;

    /** Normale de surface au point d'intersection (vecteur unitaire). */
    public final Vector normal;

    /** Forme géométrique intersectée. */
    public final IShape shape;

    /**
     * Construit une intersection.
     *
     * @param t paramètre le long du rayon (doit être > 0 pour un hit "devant" la caméra)
     * @param point point d'intersection
     * @param normal normale de surface (sera normalisée)
     * @param shape forme intersectée
     */
    public Intersection(double t, Point point, Vector normal, IShape shape) {
        this.t = t;
        this.point = point;
        this.normal = normal != null ? normal.normalized() : null;
        this.shape = shape;
    }

    @Override
    public String toString() {
        return "Intersection(t=" + t + ", point=" + point + ", normal=" + normal + ", shape=" + shape + ")";
    }
}
