package fr.ninhache.raytracer.math;

public final class Epsilon {
    private Epsilon() {}
    public static final double EPS = 1e-9;

    public static boolean almostEqual(double a, double b) {
        if (Double.isNaN(a) || Double.isNaN(b)) return false;
        double diff = Math.abs(a - b);
        double scale = Math.max(1.0, Math.max(Math.abs(a), Math.abs(b)));
        return diff <= EPS * scale;
    }
}
