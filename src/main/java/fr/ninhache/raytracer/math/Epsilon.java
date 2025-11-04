package fr.ninhache.raytracer.math;

public final class Epsilon {
    public static final double EPS = 1e-9;
    private Epsilon() {}

    /**
     * Compare deux valeurs flottantes avec une tolérance relative
     *
     * @param a première valeur
     * @param b seconde valeur
     * @param epsilon tolérance relative
     * @return {@code true} si les valeurs sont proches selon la formule relative, {@code false} sinon ou si l'une est NaN
     */
    public static boolean almostEqual(double a, double b, double epsilon) {
        if (Double.isNaN(a) || Double.isNaN(b)) {
            return false;
        }
        double diff = Math.abs(a - b);
        double scale = Math.max(1.0, Math.max(Math.abs(a), Math.abs(b)));
        return diff <= epsilon * scale;
    }
}
