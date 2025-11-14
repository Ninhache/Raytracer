package fr.ninhache.raytracer.geometry.shape;

import fr.ninhache.raytracer.geometry.Intersection;
import fr.ninhache.raytracer.geometry.Ray;
import fr.ninhache.raytracer.math.Point;
import fr.ninhache.raytracer.math.Vector;

import java.util.Optional;

import static fr.ninhache.raytracer.math.Epsilon.EPS;

/**
 * Représente un plan infini dans l'espace 3D.
 *
 * <p>Un plan est défini par :
 * <ul>
 *   <li>Un <strong>point</strong> appartenant au plan</li>
 *   <li>Une <strong>normale</strong> (vecteur perpendiculaire au plan)</li>
 * </ul>
 *
 * <h2>Équation implicite</h2>
 * <p>Un point P est sur le plan si : (P - point) · normale = 0
 *
 * <h2>Exemple</h2>
 * <pre>{@code
 * // Plan horizontal au niveau y=0 (sol)
 * Plane ground = new Plane(
 *     new Point(0, 0, 0),    // Point de référence
 *     new Vector(0, 1, 0)     // Normale vers le haut
 * );
 *
 * // Plan vertical face à la caméra
 * Plane wall = new Plane(
 *     new Point(0, 0, -5),
 *     new Vector(0, 0, 1)
 * );
 * }</pre>
 */
public final class Plane extends AbstractShape {

    private final Point point;
    private final Vector normal;

    /**
     * Crée un plan.
     *
     * @param point un point appartenant au plan
     * @param normal la normale au plan (sera normalisée automatiquement)
     * @throws IllegalArgumentException si point ou normal est null, ou si normal est nul
     */
    public Plane(Point point, Vector normal) {
        super();

        if (point == null) {
            throw new IllegalArgumentException("Le point ne peut pas être null");
        }
        if (normal == null) {
            throw new IllegalArgumentException("La normale ne peut pas être null");
        }
        if (normal.isZero(1e-10)) {
            throw new IllegalArgumentException("La normale ne peut pas être le vecteur nul");
        }

        this.point = point;
        this.normal = normal.normalized(); // Garantir une normale unitaire
    }

    /**
     * @return un point appartenant au plan
     */
    public Point getPoint() {
        return point;
    }

    /**
     * @return la normale au plan (vecteur unitaire)
     */
    public Vector getNormal() {
        return normal;
    }

    /**
     * Calcule la distance signée d'un point au plan.
     *
     * <p>Distance positive si le point est du côté de la normale,
     * négative sinon, nulle si le point est sur le plan.
     *
     * @param p le point dont on calcule la distance
     * @return la distance signée au plan
     */
    public double signedDistance(Point p) {
        Vector toPoint = p.sub(point);
        return toPoint.dot(normal);
    }

    @Override
    public String describe() {
        return String.format("Plane[point=%s, normal=%s, material=%s]",
                point, normal, material);
    }

    @Override
    public String toString() {
        return describe();
    }

    /**
     * Calcule l'intersection entre ce rayon et ce plan infini.
     *
     * <p>On résout (O + tD - P0) · n = 0, soit :
     * t = (P0 - O) · n / (D · n)
     *
     * @param ray le rayon incident
     * @return la plus proche intersection positive, ou {@code Optional.empty()} si aucune
     */
    @Override
    public Optional<Intersection> intersect(Ray ray) {
        Vector dir = ray.getDirection();
        Point origin = ray.getOrigin();

        double denom = dir.dot(normal);

        // Si denom est proche de 0, le rayon est (presque) parallèle au plan
        if (Math.abs(denom) < EPS) {
            return Optional.empty();
        }

        // vecteur de l'origine du rayon vers le point de référence du plan
        Vector p0l0 = point.sub(origin);
        double t = p0l0.dot(normal) / denom;

        if (t <= EPS) {
            return Optional.empty();
        }

        Point hitPoint = ray.at(t);

        return Optional.of(new Intersection(t, hitPoint, normal, this));
    }
}