package fr.ninhache.raytracer.geometry;

import fr.ninhache.raytracer.math.Point;

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
}
