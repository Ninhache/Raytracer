package fr.ninhache.raytracer.math;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestUtils {
    private TestUtils() {}
    public static final double EPS = 1e-9;

    public static void assertAlmost(double actual, double expected) {
        double diff = Math.abs(actual - expected);
        double scale = Math.max(1.0, Math.max(Math.abs(actual), Math.abs(expected)));

        assertTrue(diff <= EPS * scale, () -> "expected " + expected + " but got " + actual);
    }
}