package fr.ninhache.raytracer.math;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static fr.ninhache.raytracer.math.TestUtils.EPS;
import static fr.ninhache.raytracer.math.TestUtils.assertAlmost;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour {@link AbstractVec3}.
 *
 * <p>Ces tests vérifient le comportement des opérations mathématiques de base
 * sur les vecteurs 3D : normes, normalisation, comparaisons, etc.
 */
@DisplayName("Tests de la classe AbstractVec3")
class AbstractVec3Test {

    @Test
    void length_computesEuclideanNorm() {
        TestVec v = new TestVec(3, 4, 12);
        assertEquals(169.0, v.lengthSquared(), 0.0);
        assertEquals(13.0, v.length(), 0.0);
    }

    @Test
    void lengthSquared_avoidsSquareRootComputation() {
        TestVec v = new TestVec(1, 2, 2);
        assertAlmost(v.lengthSquared(), 9.0);
    }

    @Test
    void normalized_nonZeroVector_returnsUnitVector() {
        TestVec v = new TestVec(3, 4, 0);
        TestVec normalized = (TestVec) v.normalized();

        assertTrue(normalized.almostEquals(new TestVec(0.6, 0.8, 0.0), EPS),
                "Le vecteur normalisé devrait être (0.6, 0.8, 0.0)");
        assertAlmost(normalized.length(), 1.0);
    }

    @Test
    void normalized_zeroVector_returnsZeroVector() {
        TestVec zero = new TestVec(0, 0, 0);
        TestVec normalized = (TestVec) zero.normalized();

        assertTrue(normalized.almostEquals(new TestVec(0, 0, 0), EPS),
                "La normalisation du vecteur nul devrait retourner le vecteur nul");
        assertEquals(0.0, normalized.length(), 0.0);
    }

    @Test
    void schur_computesComponentwiseProduct() {
        TestVec a = new TestVec(2, 3, 4);
        TestVec b = new TestVec(10, 0.5, 2);
        TestVec result = a.schurPublic(b);

        assertTrue(result.almostEquals(new TestVec(20, 1.5, 8), EPS),
                "Le produit de Schur devrait être (20, 1.5, 8)");
    }

    @Test
    void isZero_zeroVector_returnsTrue() {
        assertTrue(new TestVec(0, 0, 0).isZero(EPS));
    }

    @Test
    void isZero_verySmallVector_returnsTrueWithSufficientEpsilon() {
        assertTrue(new TestVec(1e-12, -1e-12, 0).isZero(1e-9),
                "Un vecteur très petit devrait être considéré comme nul avec epsilon = 1e-9");
    }

    @Test
    void isZero_smallButSignificantVector_returnsFalse() {
        assertFalse(new TestVec(1e-6, 0, 0).isZero(1e-9),
                "Un vecteur de norme 1e-6 ne devrait pas être nul avec epsilon = 1e-9");
    }

    @Test
    void equals_sameValues_returnsTrue() {
        TestVec a = new TestVec(1, 2, 3);
        TestVec b = new TestVec(1, 2, 3);

        assertEquals(a, b, "Deux vecteurs avec les mêmes valeurs devraient être égaux");
        assertEquals(a.hashCode(), b.hashCode(),
                "Deux vecteurs égaux devraient avoir le même hashCode");
    }

    @Test
    void equals_slightDifference_returnsFalseButAlmostEqualsReturnsTrue() {
        TestVec a = new TestVec(1, 2, 3);
        TestVec c = new TestVec(1 + 1e-12, 2, 3);

        assertNotEquals(a, c,
                "equals() doit être strict : une différence minime rend les vecteurs différents");
        assertTrue(a.almostEquals(c, 1e-9),
                "almostEquals() doit tolérer les petites différences numériques");
    }

    @Test
    void equals_differentType_returnsFalse() {
        TestVec testVec = new TestVec(1, 2, 3);
        Vector vector = new Vector(1, 2, 3);

        assertNotEquals(testVec, vector,
                "Deux vecteurs de types différents ne doivent jamais être égaux");
    }

    @Test
    void almostEquals_null_returnsFalse() {
        TestVec v = new TestVec(1, 2, 3);
        assertFalse(v.almostEquals(null, EPS),
                "almostEquals avec null devrait retourner false");
    }

    @Test
    void almostEquals_differentType_returnsFalse() {
        TestVec testVec = new TestVec(1, 2, 3);
        Vector vector = new Vector(1, 2, 3);

        assertFalse(testVec.almostEquals(vector, EPS),
                "almostEquals ne devrait pas accepter des types différents");
    }

    @Test
    void toString_containsClassNameAndComponents() {
        String representation = new TestVec(1, 2, 3).toString();

        assertTrue(representation.contains("TestVec"),
                "toString() devrait contenir le nom de la classe");
        assertTrue(representation.contains("1.0"), "toString() devrait contenir x");
        assertTrue(representation.contains("2.0"), "toString() devrait contenir y");
        assertTrue(representation.contains("3.0"), "toString() devrait contenir z");
    }
}