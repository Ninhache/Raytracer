package fr.ninhache.raytracer.geometry;

import fr.ninhache.raytracer.geometry.shape.Sphere;
import fr.ninhache.raytracer.math.Point;
import fr.ninhache.raytracer.math.Vector;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static fr.ninhache.raytracer.math.TestUtils.assertAlmost;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Intersection rayon-sphère")
class RayIntersectionTest {

    @Test
    @DisplayName("Un rayon en direction du centre touche la sphère (2 intersections, on garde la plus proche)")
    void rayHitsSphereKeepsNearestPositiveRoot() {
        // Oeil à l'origine, sphère centrée à z=-5, rayon 1
        Ray ray = new Ray(new Point(0,0,0), new Vector(0,0,-1));
        Sphere sphere = new Sphere(new Point(0,0,-5), 1.0);

        Optional<Intersection> hit = sphere.intersect(ray);
        assertTrue(hit.isPresent(), "L'intersection devrait exister");

        // Les points d'intersection sont à z=-4 et z=-6, donc t_near=4
        assertAlmost(hit.get().t, 4.0);
        assertAlmost(hit.get().point.z, -4.0);

        // Normale sortante au point le plus proche : (0,0,1)
        assertAlmost(hit.get().normal.z, 1.0);
    }
}
