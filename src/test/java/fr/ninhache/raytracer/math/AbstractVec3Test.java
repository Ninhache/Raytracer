package fr.ninhache.raytracer.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AbstractVec3Test {

    private static final double EPS = 1e-9;

    @Test
    void length_and_lengthSquared() {
        TestVec v = new TestVec(3,4,12);
        assertEquals(9 + 16 + 144, v.lengthSquared(), 0.0);
        assertEquals(Math.sqrt(169), v.length(), 0.0);
    }

    @Test
    void normalized_non_zero_and_zero() {
        TestVec v = new TestVec(3,4,0);
        TestVec n = (TestVec) v.normalized();
        assertTrue(n.almostEquals(new TestVec(0.6, 0.8, 0.0), EPS));
        assertEquals(1.0, n.length(), 1e-12);

        TestVec zero = new TestVec(0,0,0);
        TestVec nz = (TestVec) zero.normalized();
        assertTrue(nz.almostEquals(new TestVec(0,0,0), EPS));
        assertEquals(0.0, nz.length(), 0.0);
    }

    @Test
    void schur_product() {
        TestVec a = new TestVec(2,3,4);
        TestVec b = new TestVec(10,0.5,2);
        TestVec s = a.schurPublic(b);
        assertTrue(s.almostEquals(new TestVec(20, 1.5, 8), EPS));
    }

    @Test
    void isZero_with_epsilon() {
        assertTrue(new TestVec(0,0,0).isZero(EPS));
        assertTrue(new TestVec(1e-12, -1e-12, 0).isZero(1e-9));
        assertFalse(new TestVec(1e-6, 0, 0).isZero(1e-9));
    }

    @Test
    void equals_is_exact_and_type_sensitive() {
        TestVec a = new TestVec(1,2,3);
        TestVec b = new TestVec(1,2,3);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        // petite différence → equals false, almostEquals true
        TestVec c = new TestVec(1 + 1e-12, 2, 3);
        assertNotEquals(a, c);
        assertTrue(a.almostEquals(c, 1e-9));

        // type différent → equals false
        Vector v = new Vector(1,2,3);
        assertNotEquals(a, v);
    }

    @Test
    void toString_contains_class_and_components() {
        String s = new TestVec(1,2,3).toString();
        assertTrue(s.contains("TestVec"));
        assertTrue(s.contains("1.0"));
        assertTrue(s.contains("2.0"));
        assertTrue(s.contains("3.0"));
    }
}
