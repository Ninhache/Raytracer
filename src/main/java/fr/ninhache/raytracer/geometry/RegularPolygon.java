package fr.ninhache.raytracer.geometry;

import fr.ninhache.raytracer.math.Epsilon;
import fr.ninhache.raytracer.math.Point;
import fr.ninhache.raytracer.math.Vector;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Représente un polygone régulier (futur).
 *
 * <p>Cette classe démontre l'extensibilité de la hiérarchie Shape.
 * Un polygone régulier peut être automatiquement décomposé en triangles.
 *
 * <p><strong>Note</strong> : implémentation à compléter pour le raytracing.
 */
public final class RegularPolygon extends AbstractShape {

    private final Point center;
    private final double radius;
    private final int sides;
    private final Vector normal;
    private final List<Triangle> triangles;

    /**
     * Crée un polygone régulier.
     *
     * @param center centre du polygone
     * @param radius rayon du cercle circonscrit
     * @param sides nombre de côtés (>= 3)
     * @param normal normale au plan du polygone
     */
    public RegularPolygon(Point center, double radius, int sides, Vector normal) {
        super();

        if (sides < 3) {
            throw new IllegalArgumentException("Un polygone doit avoir au moins 3 côtés");
        }

        this.center = center;
        this.radius = radius;
        this.sides = sides;
        this.normal = normal.normalized();
        this.triangles = generateTriangles();
    }

    /**
     * Génère la décomposition en triangles.
     */
    private List<Triangle> generateTriangles() {
        List<Triangle> result = new ArrayList<>();

        // Calculer les sommets du polygone
        List<Point> vertices = new ArrayList<>();
        double angleStep = 2 * Math.PI / sides;

        // Créer un repère orthonormé dans le plan du polygone
        Vector u = findOrthogonal(normal).normalized();
        Vector v = normal.cross(u);

        for (int i = 0; i < sides; i++) {
            double angle = i * angleStep;
            double x = Math.cos(angle) * radius;
            double y = Math.sin(angle) * radius;

            // Position = centre + x*u + y*v
            Point vertex = center
                    .add(u.mul(x))
                    .add(v.mul(y));
            vertices.add(vertex);
        }

        // Créer des triangles en éventail depuis le centre
        for (int i = 0; i < sides; i++) {
            Point v1 = center;
            Point v2 = vertices.get(i);
            Point v3 = vertices.get((i + 1) % sides);
            result.add(new Triangle(v1, v2, v3));
        }

        return result;
    }

    /**
     * Trouve un vecteur orthogonal à v (méthode simplifiée).
     */
    private Vector findOrthogonal(Vector v) {
        if (Math.abs(v.x) > 0.1) {
            return new Vector(0, 1, 0).cross(v);
        } else {
            return new Vector(1, 0, 0).cross(v);
        }
    }

    /**
     * @return la liste des triangles constituant ce polygone
     */
    public List<Triangle> getTriangles() {
        return new ArrayList<>(triangles);
    }

    @Override
    public String describe() {
        return String.format("RegularPolygon[center=%s, radius=%.3f, sides=%d, material=%s]",
                center, radius, sides, material);
    }

    /**
     * Calcule l'intersection avec le polygone régulier en testant ses triangles internes.
     *
     * @param ray le rayon incident
     * @return la plus proche intersection positive, ou {@code Optional.empty()} si aucune
     */
    @Override
    public Optional<Intersection> intersect(Ray ray) {
        Intersection bestHit = null;
        double bestT = Double.POSITIVE_INFINITY;

        for (Triangle tri : triangles) {
            Optional<Intersection> hit = tri.intersect(ray);
            if (hit.isPresent()) {
                double t = hit.get().t;
                if (t > Epsilon.EPS && t < bestT) {
                    bestT = t;
                    Intersection h = hit.get();

                    // On remplace la forme par ce RegularPolygon pour l'étape de shading
                    bestHit = new Intersection(t, h.point, h.normal, this);
                }
            }
        }

        return Optional.ofNullable(bestHit);
    }
}