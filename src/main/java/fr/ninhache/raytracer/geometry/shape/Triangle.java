package fr.ninhache.raytracer.geometry.shape;

import fr.ninhache.raytracer.geometry.Intersection;
import fr.ninhache.raytracer.geometry.Ray;
import fr.ninhache.raytracer.math.Point;
import fr.ninhache.raytracer.math.Vector;

import java.util.Optional;

import static fr.ninhache.raytracer.math.Epsilon.EPS;

/**
 * Représente un triangle dans l'espace 3D.
 *
 * <p>Un triangle est défini par trois sommets (vertices).
 *
 * <h2>Propriétés</h2>
 * <ul>
 *   <li>Les sommets sont stockés dans l'ordre de déclaration</li>
 *   <li>La normale du triangle suit la règle de la main droite : (v2-v1) × (v3-v1)</li>
 *   <li>L'aire du triangle est calculée automatiquement</li>
 * </ul>
 *
 * <h2>Exemple</h2>
 * <pre>{@code
 * Point p1 = new Point(-1, -1, 0);
 * Point p2 = new Point( 1, -1, 0);
 * Point p3 = new Point( 0,  1, 0);
 *
 * Triangle triangle = new Triangle(p1, p2, p3);
 * Vector normale = triangle.getNormal(); // Pointe vers +Z
 * }</pre>
 */
public final class Triangle extends AbstractShape {

    private final Point v1;
    private final Point v2;
    private final Point v3;

    // Précalculés pour optimisation
    private final Vector edge1;  // v2 - v1
    private final Vector edge2;  // v3 - v1
    private final Vector normal;
    private final double area;

    /**
     * Crée un triangle à partir de trois sommets.
     *
     * @param v1 premier sommet
     * @param v2 deuxième sommet
     * @param v3 troisième sommet
     * @throws IllegalArgumentException si les points sont null ou colinéaires
     */
    public Triangle(Point v1, Point v2, Point v3) {
        super();

        if (v1 == null || v2 == null || v3 == null) {
            throw new IllegalArgumentException("Les sommets ne peuvent pas être null");
        }

        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;

        // Calcul des arêtes
        this.edge1 = v2.sub(v1);
        this.edge2 = v3.sub(v1);

        // Calcul de la normale (produit vectoriel)
        Vector crossProduct = edge1.cross(edge2);
        double crossLength = crossProduct.length();

        if (crossLength < EPS) {
            throw new IllegalArgumentException(
                    "Les sommets sont colinéaires, impossible de créer un triangle : " +
                            v1 + ", " + v2 + ", " + v3
            );
        }

        this.normal = crossProduct.normalized();
        this.area = crossLength / 2.0;
    }

    /**
     * @return le premier sommet
     */
    public Point getV1() {
        return v1;
    }

    /**
     * @return le deuxième sommet
     */
    public Point getV2() {
        return v2;
    }

    /**
     * @return le troisième sommet
     */
    public Point getV3() {
        return v3;
    }

    /**
     * @return la normale du triangle (vecteur unitaire)
     */
    public Vector getNormal() {
        return normal;
    }

    /**
     * @return l'aire du triangle
     */
    public double getArea() {
        return area;
    }

    /**
     * @return le vecteur arête de v1 vers v2
     */
    public Vector getEdge1() {
        return edge1;
    }

    /**
     * @return le vecteur arête de v1 vers v3
     */
    public Vector getEdge2() {
        return edge2;
    }

    @Override
    public String describe() {
        return String.format("Triangle[v1=%s, v2=%s, v3=%s, area=%.3f, material=%s]",
                v1, v2, v3, area, material);
    }

    @Override
    public String toString() {
        return describe();
    }

    /**
     * Calcule l'intersection rayon-triangle avec l'algorithme de Möller-Trumbore.
     *
     * @param ray le rayon incident
     * @return la plus proche intersection positive, ou {@code Optional.empty()} si aucune
     */
    @Override
    public Optional<Intersection> intersect(Ray ray) {
        Vector dir = ray.getDirection();
        Point origin = ray.getOrigin();

        // Vecteur perpendiculaire à dir et edge2
        Vector pvec = dir.cross(edge2);
        double det = edge1.dot(pvec);

        // Si det est proche de 0, le rayon est parallèle au plan du triangle
        if (Math.abs(det) < EPS) {
            return Optional.empty();
        }

        double invDet = 1.0 / det;

        // Vecteur de v1 vers l'origine du rayon
        Vector tvec = origin.sub(v1);

        double u = tvec.dot(pvec) * invDet;
        if (u < 0.0 || u > 1.0) {
            return Optional.empty();
        }

        Vector qvec = tvec.cross(edge1);
        double v = dir.dot(qvec) * invDet;
        if (v < 0.0 || u + v > 1.0) {
            return Optional.empty();
        }

        double t = edge2.dot(qvec) * invDet;
        if (t <= EPS) {
            return Optional.empty();
        }

        Point hitPoint = ray.at(t);

        // Normale orientée pour être opposée à la direction du rayon
        Vector hitNormal = normal;
        if (hitNormal.dot(dir) > 0.0) {
            hitNormal = hitNormal.negate();
        }

        return Optional.of(new Intersection(t, hitPoint, hitNormal, this));
    }
}