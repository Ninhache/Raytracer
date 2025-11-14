package fr.ninhache.raytracer.geometry.shape;

import fr.ninhache.raytracer.geometry.Intersection;
import fr.ninhache.raytracer.geometry.Ray;
import fr.ninhache.raytracer.math.Point;
import fr.ninhache.raytracer.math.Vector;

import java.util.Optional;

import static fr.ninhache.raytracer.math.Epsilon.EPS;

/**
 * Représente une sphère dans l'espace 3D.
 *
 * <p>Une sphère est définie par :
 * <ul>
 *   <li>Un <strong>centre</strong> (point dans l'espace)</li>
 *   <li>Un <strong>rayon</strong> (distance positive)</li>
 * </ul>
 *
 * <h2>Équation implicite</h2>
 * <p>Un point P est sur la sphère si : ||P - Centre||² = rayon²
 *
 * <h2>Exemple</h2>
 * <pre>{@code
 * // Sphère centrée à l'origine, rayon 1
 * Sphere unitSphere = new Sphere(new Point(0, 0, 0), 1.0);
 *
 * // Sphère au-dessus de l'origine
 * Sphere elevated = new Sphere(new Point(0, 2, 0), 0.5);
 * }</pre>
 */
public final class Sphere extends AbstractShape {

    private final Point center;
    private final double radius;

    /**
     * Crée une sphère.
     *
     * @param center le centre de la sphère
     * @param radius le rayon (doit être > 0)
     * @throws IllegalArgumentException si le rayon est <= 0
     */
    public Sphere(Point center, double radius) {
        super();

        if (center == null) {
            throw new IllegalArgumentException("Le centre ne peut pas être null");
        }
        if (radius <= 0) {
            throw new IllegalArgumentException("Le rayon doit être > 0 : " + radius);
        }

        this.center = center;
        this.radius = radius;
    }

    /**
     * @return le centre de la sphère
     */
    public Point getCenter() {
        return center;
    }

    /**
     * @return le rayon de la sphère
     */
    public double getRadius() {
        return radius;
    }

    @Override
    public String describe() {
        return String.format("Sphere[center=%s, radius=%.3f, material=%s]",
                center, radius, material);
    }

    @Override
    public String toString() {
        return describe();
    }

    @Override
    public Optional<Intersection> intersect(Ray ray) {
        if (ray == null) {
            return Optional.empty();
        }

        // (o - c) vector
        Vector oc = ray.getOrigin().sub(center); // Point - Point = Vector
        Vector d = ray.getDirection();

        double a = d.dot(d); // =1 si d est unitaire
        double b = 2.0 * oc.dot(d);
        double c = oc.dot(oc) - radius * radius;
        double disc = b*b - 4*a*c;
        if (disc < 0.0) {
            return Optional.empty();
        }

        double sqrt = Math.sqrt(disc);
        double inv2a = 1.0 / (2.0 * a);
        double t1 = (-b - sqrt) * inv2a;
        double t2 = (-b + sqrt) * inv2a;

        double t = Double.POSITIVE_INFINITY;
        if (t1 > EPS) t = t1;
        if (t2 > EPS && t2 < t) t = t2;
        if (!Double.isFinite(t) || t <= EPS) return Optional.empty();

        // construire l'intersection
        var p = ray.at(t);
        var n = p.sub(center).normalized();

        return Optional.of(new Intersection(t, p, n, this));
    }
}
