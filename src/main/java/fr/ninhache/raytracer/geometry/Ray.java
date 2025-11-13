package fr.ninhache.raytracer.geometry;

import fr.ninhache.raytracer.math.Epsilon;
import fr.ninhache.raytracer.math.Point;
import fr.ninhache.raytracer.math.Vector;

/**
 * Représente un rayon semi-infini pour le raytracing.
 *
 * <p>Un rayon est défini par :
 * <ul>
 *   <li>Une origine {@link Point} (position de départ)</li>
 *   <li>Une direction {@link Vector} (normalisée)</li>
 * </ul>
 *
 * <p>L'équation paramétrique du rayon est :
 * <pre>
 *   R(t) = origin + t * direction, avec t >= 0
 * </pre>
 */
public final class Ray {

    private final Point origin;
    private final Vector direction;

    /**
     * Crée un rayon à partir d'une origine et d'une direction.
     *
     * <p>La direction est automatiquement normalisée si nécessaire.
     *
     * @param origin origine du rayon (ne doit pas être null)
     * @param direction direction du rayon (ne doit pas être nulle ni quasi nulle)
     */
    public Ray(Point origin, Vector direction) {
        if (origin == null) {
            throw new IllegalArgumentException("origin ne peut pas être null");
        }
        if (direction == null) {
            throw new IllegalArgumentException("direction ne peut pas être null");
        }
        if (direction.isZero(1e-12)) {
            throw new IllegalArgumentException("direction ne peut pas être le vecteur nul");
        }

        this.origin = origin;
        // Si la direction n'est pas déjà unitaire, on la normalise
        Vector dir = direction;
        if (!direction.isUnit(Epsilon.EPS)) {
            dir = direction.normalized();
        }
        this.direction = dir;
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

    /**
     * Calcule le point situé à la distance paramétrique t le long du rayon.
     *
     * <p>Pour t = 0, on obtient l'origine. Pour t > 0, on se déplace
     * dans le sens de la direction.
     *
     * @param t paramètre le long du rayon
     * @return le point origin + t * direction
     */
    public Point at(double t) {
        return origin.add(direction.mul(t));
    }

    @Override
    public String toString() {
        return "Ray(origin=" + origin + ", direction=" + direction + ")";
    }
}
