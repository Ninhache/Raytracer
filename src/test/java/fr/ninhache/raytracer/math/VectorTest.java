package fr.ninhache.raytracer.math;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static fr.ninhache.raytracer.math.TestUtils.EPS;
import static fr.ninhache.raytracer.math.TestUtils.assertAlmost;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de la classe Vector")
class VectorTest {

    @Test
    @DisplayName("add() - additionne deux vecteurs")
    void add_twoVectors_returnsSum() {
        Vector v1 = new Vector(1, 2, 3);
        Vector v2 = new Vector(4, 5, 6);

        Vector result = v1.add(v2);

        assertTrue(result.almostEquals(new Vector(5, 7, 9), EPS));
    }

    @Test
    @DisplayName("add() - est commutative")
    void add_isCommutative() {
        Vector v1 = new Vector(1, 2, 3);
        Vector v2 = new Vector(7, 8, 9);

        assertTrue(v1.add(v2).almostEquals(v2.add(v1), EPS));
    }

    @Test
    @DisplayName("sub() - soustrait deux vecteurs")
    void sub_twoVectors_returnsDifference() {
        Vector v1 = new Vector(10, 20, 30);
        Vector v2 = new Vector(1, 2, 3);

        Vector result = v1.sub(v2);

        assertTrue(result.almostEquals(new Vector(9, 18, 27), EPS));
    }

    @Test
    @DisplayName("sub() - n'est pas commutative")
    void sub_isNotCommutative() {
        Vector v1 = new Vector(5, 5, 5);
        Vector v2 = new Vector(3, 2, 1);

        Vector forward = v1.sub(v2);   // (2, 3, 4)
        Vector backward = v2.sub(v1);  // (-2, -3, -4)

        assertTrue(forward.almostEquals(backward.negate(), EPS),
                "v1.sub(v2) devrait être égal à -(v2.sub(v1))");
    }

    @Test
    @DisplayName("mul() - multiplie par un scalaire")
    void mul_positiveScalar_scalesVector() {
        Vector v = new Vector(1, 2, 3);

        Vector result = v.mul(2.5);

        assertTrue(result.almostEquals(new Vector(2.5, 5, 7.5), EPS));
    }

    @Test
    @DisplayName("mul(0) - retourne le vecteur nul")
    void mul_zero_returnsZeroVector() {
        Vector v = new Vector(10, 20, 30);

        Vector result = v.mul(0);

        assertTrue(result.isZero(EPS));
    }

    @Test
    @DisplayName("mul(-1) - inverse la direction")
    void mul_negativeOne_negatesVector() {
        Vector v = new Vector(1, 2, 3);

        Vector result = v.mul(-1);

        assertTrue(result.almostEquals(new Vector(-1, -2, -3), EPS));
    }

    @Test
    @DisplayName("div() - divise par un scalaire")
    void div_scalar_dividesVector() {
        Vector v = new Vector(10, 20, 30);

        Vector result = v.div(2);

        assertTrue(result.almostEquals(new Vector(5, 10, 15), EPS));
    }

    @Test
    @DisplayName("dot() - calcule le produit scalaire")
    void dot_twoVectors_returnsScalarProduct() {
        Vector v1 = new Vector(1, 2, 3);
        Vector v2 = new Vector(4, 5, 6);

        double result = v1.dot(v2);

        // 1×4 + 2×5 + 3×6 = 4 + 10 + 18 = 32
        assertAlmost(result, 32.0);
    }

    @Test
    @DisplayName("dot() - est commutatif")
    void dot_isCommutative() {
        Vector v1 = new Vector(3, 4, 5);
        Vector v2 = new Vector(6, 7, 8);

        assertAlmost(v1.dot(v2), v2.dot(v1));
    }

    @Test
    @DisplayName("dot() - vecteurs orthogonaux donnent 0")
    void dot_orthogonalVectors_returnsZero() {
        Vector v1 = new Vector(1, 0, 0);
        Vector v2 = new Vector(0, 1, 0);

        double result = v1.dot(v2);

        assertAlmost(result, 0.0);
    }

    @Test
    @DisplayName("dot() - vecteurs parallèles donnent produit des normes")
    void dot_parallelVectors_returnsProductOfLengths() {
        Vector v1 = new Vector(3, 0, 0);
        Vector v2 = new Vector(4, 0, 0);

        double result = v1.dot(v2);

        assertAlmost(result, 12.0);  // 3 × 4
    }

    @Test
    @DisplayName("cross() - calcule le produit vectoriel")
    void cross_twoVectors_returnsPerpendicularVector() {
        Vector v1 = new Vector(1, 0, 0);
        Vector v2 = new Vector(0, 1, 0);

        Vector result = v1.cross(v2);

        assertTrue(result.almostEquals(new Vector(0, 0, 1), EPS),
                "Le produit vectoriel de X et Y devrait donner Z");
    }

    @Test
    @DisplayName("cross() - n'est PAS commutatif")
    void cross_isAntiCommutative() {
        Vector v1 = new Vector(1, 2, 3);
        Vector v2 = new Vector(4, 5, 6);

        Vector forward = v1.cross(v2);
        Vector backward = v2.cross(v1);

        assertTrue(forward.almostEquals(backward.negate(), EPS),
                "v1.cross(v2) devrait être égal à -(v2.cross(v1))");
    }

    @Test
    @DisplayName("cross() - vecteurs colinéaires donnent vecteur nul")
    void cross_collinearVectors_returnsZeroVector() {
        Vector v1 = new Vector(2, 4, 6);
        Vector v2 = new Vector(1, 2, 3);  // v1 = 2 × v2

        Vector result = v1.cross(v2);

        assertTrue(result.isZero(EPS),
                "Le produit vectoriel de vecteurs colinéaires devrait être nul");
    }

    @Test
    @DisplayName("cross() - le résultat est orthogonal aux deux vecteurs")
    void cross_resultIsOrthogonalToInputs() {
        Vector v1 = new Vector(1, 2, 3);
        Vector v2 = new Vector(4, 5, 6);

        Vector result = v1.cross(v2);

        assertAlmost(result.dot(v1), 0.0);
        assertAlmost(result.dot(v2), 0.0);
    }

    @Test
    @DisplayName("negate() - inverse le vecteur")
    void negate_returnsOppositeVector() {
        Vector v = new Vector(1, -2, 3);

        Vector result = v.negate();

        assertTrue(result.almostEquals(new Vector(-1, 2, -3), EPS));
    }

    @Test
    @DisplayName("normalized() - retourne un vecteur unitaire")
    void normalized_nonZeroVector_returnsUnitVector() {
        Vector v = new Vector(3, 4, 0);

        Vector normalized = v.normalized();

        assertAlmost(normalized.length(), 1.0);
        assertTrue(normalized.almostEquals(new Vector(0.6, 0.8, 0), EPS));
    }

    @Test
    @DisplayName("projectOnto() - projette sur un autre vecteur")
    void projectOnto_returnsProjection() {
        Vector v = new Vector(3, 4, 0);
        Vector onto = new Vector(1, 0, 0);  // Axe X

        Vector projection = v.projectOnto(onto);

        assertTrue(projection.almostEquals(new Vector(3, 0, 0), EPS),
                "La projection de (3,4,0) sur X devrait être (3,0,0)");
    }

    @Test
    @DisplayName("projectOnto() - vecteur nul lance une exception")
    void projectOnto_zeroVector_throwsException() {
        Vector v = new Vector(1, 2, 3);
        Vector zero = new Vector(0, 0, 0);

        assertThrows(ArithmeticException.class, () -> v.projectOnto(zero),
                "Projeter sur un vecteur nul devrait lever une exception");
    }

    @Test
    @DisplayName("reflect() - réfléchit par rapport à une normale")
    void reflect_normalizedNormal_returnsReflection() {
        // Vecteur incident à 45° du sol
        Vector incident = new Vector(1, -1, 0).normalized();
        Vector normal = new Vector(0, 1, 0);  // Normale verticale

        Vector reflected = incident.reflect(normal);

        // La réflexion devrait être symétrique : (1, 1, 0) normalisé
        Vector expected = new Vector(1, 1, 0).normalized();
        assertTrue(reflected.almostEquals(expected, EPS),
                "La réflexion devrait être symétrique par rapport à la normale");
    }

    @Test
    @DisplayName("schur() - produit composante par composante")
    void schur_twoVectors_returnsComponentwiseProduct() {
        Vector v1 = new Vector(2, 3, 4);
        Vector v2 = new Vector(10, 0.5, 2);

        Vector result = v1.schur(v2);

        assertTrue(result.almostEquals(new Vector(20, 1.5, 8), EPS));
    }

    @Test
    @DisplayName("equals() - compare strictement les composantes")
    void equals_sameComponents_returnsTrue() {
        Vector v1 = new Vector(1, 2, 3);
        Vector v2 = new Vector(1, 2, 3);

        assertEquals(v1, v2);
        assertEquals(v1.hashCode(), v2.hashCode());
    }

    @Test
    @DisplayName("toString() - contient les composantes")
    void toString_containsComponents() {
        String str = new Vector(1.5, 2.5, 3.5).toString();

        assertTrue(str.contains("Vector"));
        assertTrue(str.contains("1.5"));
        assertTrue(str.contains("2.5"));
        assertTrue(str.contains("3.5"));
    }
}