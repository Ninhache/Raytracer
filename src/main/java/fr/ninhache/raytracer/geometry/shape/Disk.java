package fr.ninhache.raytracer.geometry.shape;

import fr.ninhache.raytracer.geometry.Intersection;
import fr.ninhache.raytracer.geometry.Ray;
import fr.ninhache.raytracer.math.Point;
import fr.ninhache.raytracer.math.Vector;

import java.util.Optional;

import static fr.ninhache.raytracer.math.Epsilon.EPS;

/**
 * Disque fini dans un plan.
 *
 * Décrit par :
 * - un centre {@link Point}
 * - une normale {@link Vector} (orientation du disque)
 * - un rayon > 0
 *
 * Intersection :
 * 1) Intersecte le plan (centre, normale)
 * 2) On vérifie que le point d'impact est à distance <= rayon du centre.
 */
public final class Disk extends AbstractShape {

    private final Point center;
    private final Vector normal;
    private final double radius;
    private final double radiusSq;

    /**
     * Crée un disque.
     *
     * @param center centre du disque
     * @param normal normale du disque (sera normalisée)
     * @param radius rayon strictement positif
     */
    public Disk(Point center, Vector normal, double radius) {
        if (center == null) {
            throw new IllegalArgumentException("center ne peut pas être null");
        }
        if (normal == null) {
            throw new IllegalArgumentException("normal ne peut pas être null");
        }
        if (radius <= 0.0) {
            throw new IllegalArgumentException("radius doit être > 0");
        }
        this.center = center;
        this.normal = normal.normalized();
        this.radius = radius;
        this.radiusSq = radius * radius;
    }

    public Point getCenter() {
        return center;
    }

    public Vector getNormal() {
        return normal;
    }

    public double getRadius() {
        return radius;
    }

    @Override
    public Optional<Intersection> intersect(Ray ray) {
        // Intersections avec le plan du disque
        double denom = normal.dot(ray.getDirection());
        if (Math.abs(denom) < EPS) {
            // Rayon quasi parallèle au disque
            return Optional.empty();
        }

        // t = ((center - origin) • n) / (d • n)
        Vector oc = center.sub(ray.getOrigin());
        double t = oc.dot(normal) / denom;

        if (t <= EPS) {
            // Intersection derrière l'origine ou trop proche
            return Optional.empty();
        }

        Point hitPoint = ray.at(t);

        // Test de rayon : distance^2 <= radius^2
        Vector diff = hitPoint.sub(center);
        double distSq = diff.lengthSquared();
        if (distSq > radiusSq + EPS) {
            return Optional.empty();
        }

        // On garde la normale orientée "face caméra" (optionnel, mais souvent utile)
        Vector finalNormal = normal;
        if (ray.getDirection().dot(finalNormal) > 0.0) {
            finalNormal = finalNormal.negate();
        }

        return Optional.of(new Intersection(t, hitPoint, finalNormal, this));
    }

    @Override
    public String describe() {
        return String.format("Disk[center=%s, normal=%s, radius=%.3f, material=%s]",
                center, normal, radius, getMaterial());
    }

    @Override
    public String toString() {
        return describe();
    }
}
