package fr.ninhache.raytracer;

import fr.ninhache.raytracer.math.Point;
import fr.ninhache.raytracer.math.Vector;

/**
 * Représente un rayon en 3D (demi-droite).
 *
 * <p>Un rayon est défini par :
 * <ul>
 *   <li>Une <strong>origine</strong> (point de départ)</li>
 *   <li>Une <strong>direction</strong> (vecteur unitaire)</li>
 * </ul>
 *
 * <p>Équation paramétrique : <code>P(t) = origine + t × direction</code> avec t ≥ 0
 *
 * <h2>Exemple</h2>
 * <pre>{@code
 * // Rayon partant de l'origine vers +Z
 * Ray ray = new Ray(new Point(0, 0, 0), new Vector(0, 0, 1));
 *
 * // Point à distance 5 le long du rayon
 * Point p = ray.pointAt(5.0);  // (0, 0, 5)
 * }</pre>
 */
public final class Ray {

    private final Point origin;
    private final Vector direction;

    /**
     * Crée un rayon.
     *
     * @param origin point d'origine du rayon
     * @param direction direction du rayon (doit être normalisée)
     * @throws IllegalArgumentException si origin ou direction est null
     */
    public Ray(Point origin, Vector direction) {
        if (origin == null || direction == null) {
            throw new IllegalArgumentException("Origin et direction ne peuvent pas être null");
        }

        this.origin = origin;
        this.direction = direction.normalized(); // Garantir normalisation
    }

    /**
     * Calcule un point le long du rayon.
     *
     * @param t distance le long du rayon (t ≥ 0)
     * @return le point à la position origine + t × direction
     */
    public Point pointAt(double t) {
        return origin.add(direction.mul(t));
    }

    /**
     * @return l'origine du rayon
     */
    public Point getOrigin() {
        return origin;
    }

    /**
     * @return la direction du rayon (vecteur unitaire)
     */
    public Vector getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return String.format("Ray[origin=%s, direction=%s]", origin, direction);
    }
}
