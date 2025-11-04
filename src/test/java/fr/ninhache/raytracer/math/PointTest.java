package fr.ninhache.raytracer.math;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static fr.ninhache.raytracer.math.TestUtils.EPS;
import static fr.ninhache.raytracer.math.TestUtils.assertAlmost;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de la classe Point")
class PointTest {

    @Test
    @DisplayName("sub(Point) - calcule le vecteur entre deux points")
    void sub_twoPoints_returnsDisplacementVector() {
        Point p1 = new Point(1, 2, 3);
        Point p2 = new Point(4, 6, 9);

        Vector result = p2.sub(p1);

        assertTrue(result.almostEquals(new Vector(3, 4, 6), EPS),
                "Le vecteur de p1 vers p2 devrait être (3, 4, 6)");
    }

    @Test
    @DisplayName("sub(Point) - points identiques donnent vecteur nul")
    void sub_identicalPoints_returnsZeroVector() {
        Point p = new Point(5, 10, 15);

        Vector result = p.sub(p);

        assertTrue(result.isZero(EPS),
                "La différence d'un point avec lui-même devrait être le vecteur nul");
    }

    @Test
    @DisplayName("add(Vector) - translate un point")
    void add_pointAndVector_returnsTranslatedPoint() {
        Point origine = new Point(1, 2, 3);
        Vector deplacement = new Vector(10, 20, 30);

        Point result = origine.add(deplacement);

        assertTrue(result.almostEquals(new Point(11, 22, 33), EPS),
                "La translation devrait donner (11, 22, 33)");
    }

    @Test
    @DisplayName("add(Vector) - vecteur nul ne change pas le point")
    void add_zeroVector_returnsSamePoint() {
        Point p = new Point(5, 10, 15);
        Vector zero = new Vector(0, 0, 0);

        Point result = p.add(zero);

        assertTrue(result.almostEquals(p, EPS),
                "Ajouter le vecteur nul ne devrait pas changer le point");
    }

    @Test
    @DisplayName("sub(Vector) - translate dans la direction opposée")
    void sub_pointAndVector_returnsTranslatedPoint() {
        Point p = new Point(10, 20, 30);
        Vector v = new Vector(1, 2, 3);

        Point result = p.sub(v);

        assertTrue(result.almostEquals(new Point(9, 18, 27), EPS),
                "La soustraction devrait donner (9, 18, 27)");
    }

    @Test
    @DisplayName("mul(scalar) - mise à l'échelle depuis l'origine")
    void mul_positiveScalar_scalesPoint() {
        Point p = new Point(2, 3, 4);

        Point result = p.mul(2.5);

        assertTrue(result.almostEquals(new Point(5, 7.5, 10), EPS),
                "La multiplication par 2.5 devrait donner (5, 7.5, 10)");
    }

    @Test
    @DisplayName("mul(0) - retourne l'origine")
    void mul_zero_returnsOrigin() {
        Point p = new Point(10, 20, 30);

        Point result = p.mul(0);

        assertTrue(result.almostEquals(new Point(0, 0, 0), EPS),
                "Multiplier par 0 devrait donner l'origine");
    }

    @Test
    @DisplayName("mul(negative) - réflexion par rapport à l'origine")
    void mul_negativeScalar_reflectsPoint() {
        Point p = new Point(1, 2, 3);

        Point result = p.mul(-1);

        assertTrue(result.almostEquals(new Point(-1, -2, -3), EPS),
                "Multiplier par -1 devrait réfléchir le point");
    }

    @Test
    @DisplayName("distance() - calcule la distance euclidienne")
    void distance_twoPoints_returnsEuclideanDistance() {
        Point p1 = new Point(0, 0, 0);
        Point p2 = new Point(3, 4, 0);

        double distance = p1.distance(p2);

        assertAlmost(distance, 5.0);
    }

    @Test
    @DisplayName("distance() - distance d'un point à lui-même est nulle")
    void distance_samePoint_returnsZero() {
        Point p = new Point(10, 20, 30);

        double distance = p.distance(p);

        assertEquals(0.0, distance, 1e-15,
                "La distance d'un point à lui-même devrait être nulle");
    }

    @Test
    @DisplayName("distanceSquared() - plus efficace que distance()")
    void distanceSquared_twoPoints_returnsSquaredDistance() {
        Point p1 = new Point(1, 2, 3);
        Point p2 = new Point(4, 6, 9);

        double distSq = p1.distanceSquared(p2);

        // (4-1)² + (6-2)² + (9-3)² = 9 + 16 + 36 = 61
        assertAlmost(distSq, 61.0);
    }

    @Test
    @DisplayName("Interpolation linéaire entre deux points")
    void lerp_twoPoints_interpolatesCorrectly() {
        Point p1 = new Point(0, 0, 0);
        Point p2 = new Point(10, 20, 30);
        double t = 0.5;

        // Lerp: p1 * (1-t) + p2 * t
        Point milieu = p1.mul(1 - t).add(p2.sub(p1).mul(t));

        assertTrue(milieu.almostEquals(new Point(5, 10, 15), EPS),
                "Le point milieu devrait être (5, 10, 15)");
    }

    @Test
    @DisplayName("equals() - stricte égalité des composantes")
    void equals_sameCoordinates_returnsTrue() {
        Point p1 = new Point(1, 2, 3);
        Point p2 = new Point(1, 2, 3);

        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    @DisplayName("equals() - Point vs Vector avec mêmes valeurs")
    void equals_pointVsVector_returnsFalse() {
        Point point = new Point(1, 2, 3);
        Vector vector = new Vector(1, 2, 3);

        assertNotEquals(point, vector,
                "Un Point et un Vector ne sont jamais égaux même avec les mêmes valeurs");
    }

    @Test
    @DisplayName("toString() - contient les coordonnées")
    void toString_containsCoordinates() {
        String str = new Point(1.5, 2.5, 3.5).toString();

        assertTrue(str.contains("Point"));
        assertTrue(str.contains("1.5"));
        assertTrue(str.contains("2.5"));
        assertTrue(str.contains("3.5"));
    }
}