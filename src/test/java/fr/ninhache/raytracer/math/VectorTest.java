package fr.ninhache.raytracer.math;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static fr.ninhache.raytracer.math.TestUtils.assertAlmost;


public class VectorTest {

    @Test
    void add_sub_scale() {
        Vector a = new Vector(1,2,3);
        Vector b = new Vector(-2,5,4);

        Vector sum = a.add(b);
        assertAlmost(sum.x, -1); assertAlmost(sum.y, 7); assertAlmost(sum.z, 7);

        Vector diff = a.sub(b);
        assertAlmost(diff.x, 3); assertAlmost(diff.y, -3); assertAlmost(diff.z, -1);

        Vector scaled = a.mul(2.5);
        assertAlmost(scaled.x, 2.5); assertAlmost(scaled.y, 5.0); assertAlmost(scaled.z, 7.5);
    }

    @Test
    void length_and_normalize() {
        Vector v = new Vector(3,4,0);
        assertAlmost(v.length(), 5.0);
        Vector n = (Vector) v.normalized();
        assertAlmost(n.length(), 1.0);
        assertAlmost(n.x, 0.6); assertAlmost(n.y, 0.8); assertAlmost(n.z, 0.0);
    }

    @Test
    void normalize_zero_vector_returns_zero() {
        Vector zero = new Vector(0,0,0);
        Vector n = (Vector) zero.normalized();
        assertAlmost(n.x, 0); assertAlmost(n.y, 0); assertAlmost(n.z, 0);
        assertAlmost(n.length(), 0);
    }

    @Test
    void dot_and_cross_properties() {
        Vector i = new Vector(1,0,0);
        Vector j = new Vector(0,1,0);

        // orthogonaux
        assertAlmost(i.dot(j), 0);

        // i x j = k
        Vector k = i.cross(j);
        assertAlmost(k.x, 0); assertAlmost(k.y, 0); assertAlmost(k.z, 1);

        // non commutatif : j x i = -k
        Vector minusK = j.cross(i);
        assertAlmost(minusK.x, 0); assertAlmost(minusK.y, 0); assertAlmost(minusK.z, -1);

        // a × a = 0
        Vector zero = i.cross(i);
        assertAlmost(zero.length(), 0);
    }

    @Test
    void dot_is_distributive_over_addition() {
        Vector a = new Vector(2,3,4);
        Vector b = new Vector(10,0.5,2);
        Vector c = new Vector(-1, 7, 0.25);

        double left = a.dot(b.add(c));
        double right = a.dot(b) + a.dot(c);
        assertAlmost(left, right);
    }

    @Test
    void schur_product_components() {
        Vector a = new Vector(2,3,4);
        Vector b = new Vector(10,0.5,2);

        Vector s = a.schur(b);
        assertAlmost(s.x, 20);
        assertAlmost(s.y, 1.5);
        assertAlmost(s.z, 8);
    }

    @Test
    void cross_is_orthogonal_to_operands() {
        Vector a = new Vector(2,3,4);
        Vector b = new Vector(-1,5,2);
        Vector c = a.cross(b);
        assertAlmost(c.dot(a), 0);
        assertAlmost(c.dot(b), 0);
    }


    @Test
    void schur_with_zero_is_zero() {
        Vector a = new Vector(2,3,4);
        Vector zero = new Vector(0,0,0);
        Vector s = a.schur(zero);
        assertAlmost(s.x, 0);
        assertAlmost(s.y, 0);
        assertAlmost(s.z, 0);
    }

    @Test
    void schur_with_ones_is_identity() {
        Vector a = new Vector(2,3,4);
        Vector ones = new Vector(1,1,1);
        Vector s = a.schur(ones);
        assertAlmost(s.x, a.x);
        assertAlmost(s.y, a.y);
        assertAlmost(s.z, a.z);
    }

    @Test
    void lengthSquared_matches_sum_of_squares() {
        Vector v = new Vector(3,4,12);
        assertAlmost(v.lengthSquared(), 9 + 16 + 144);
    }

    @Test
    void dot_is_symmetric_and_homogeneous() {
        Vector a = new Vector(2,3,4);
        Vector b = new Vector(-1, 5, 2);
        assertAlmost(a.dot(b), b.dot(a));

        double k = 2.5;
        assertAlmost(a.mul(k).dot(b), k * a.dot(b));
        assertAlmost(a.dot(b.mul(k)), k * a.dot(b));
    }


    @ParameterizedTest
    @CsvSource({
            "1,0,0,   0,1,0",
            "2,-3,4,  -2,5,4",
            "0.5,0.5,0.5,  -1,2,-3"
    })
    void addition_commutative(double ax, double ay, double az,
                              double bx, double by, double bz) {
        Vector a = new Vector(ax, ay, az);
        Vector b = new Vector(bx, by, bz);
        Vector ab = a.add(b);
        Vector ba = b.add(a);
        assertAlmost(ab.x, ba.x);
        assertAlmost(ab.y, ba.y);
        assertAlmost(ab.z, ba.z);
    }

    @ParameterizedTest
    @CsvSource({
            "1,0,0,   0,1,0,   0,0,1",
            "0,1,0,   1,0,0,   0,0,-1"
    })
    void cross_orientation(double ax, double ay, double az,
                           double bx, double by, double bz,
                           double ex, double ey, double ez) {
        Vector a = new Vector(ax, ay, az);
        Vector b = new Vector(bx, by, bz);
        Vector expected = new Vector(ex, ey, ez);
        Vector res = a.cross(b);
        assertAlmost(res.x, expected.x);
        assertAlmost(res.y, expected.y);
        assertAlmost(res.z, expected.z);
    }
}
