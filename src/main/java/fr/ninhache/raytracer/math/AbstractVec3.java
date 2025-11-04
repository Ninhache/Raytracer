package fr.ninhache.raytracer.math;

/**
 * Base pour un triplet 3D (x,y,z)
 * Todo
 *
 * <h2>Contrats</h2>
 * Todo
 *
 * <h2>Exemples</h2>
 * Todo
 */
public abstract class AbstractVec3 {
    public final double x;
    public final double y;
    public final double z;

    protected AbstractVec3(double x, double y, double z) {
        this.x = x; this.y = y; this.z = z;
    }

    protected abstract AbstractVec3 ofSameType(double x, double y, double z);

    public double length() {
        return Math.sqrt(lengthSquared());
    }

    public double lengthSquared() {
        return x * x + y * y + z * z;
    }

    public AbstractVec3 normalized() {
        double len2 = lengthSquared();
        if (len2 == 0.0) return ofSameType(0, 0, 0);
        double inv = 1.0 / Math.sqrt(len2);
        return ofSameType(x * inv, y * inv, z * inv);
    }

    protected AbstractVec3 schur(AbstractVec3 other) {
        return ofSameType(this.x * other.x, this.y * other.y, this.z * other.z);
    }

    public boolean isZero(double eps) {
        return Math.sqrt(lengthSquared()) <= eps;
    }

    public boolean almostEquals(AbstractVec3 other, double eps) {
        if (other == null || !getClass().equals(other.getClass())) return false;
        return almostEqual(x, other.x, eps)
                && almostEqual(y, other.y, eps)
                && almostEqual(z, other.z, eps);
    }

    private static boolean almostEqual(double a, double b, double eps) {
        if (Double.isNaN(a) || Double.isNaN(b)) return false;
        double diff = Math.abs(a - b);
        double scale = Math.max(1.0, Math.max(Math.abs(a), Math.abs(b)));
        return diff <= eps * scale;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + x + ", " + y + ", " + z + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractVec3 v)) return false;
        if (!getClass().equals(v.getClass())) return false;
        return Double.compare(x, v.x) == 0
                && Double.compare(y, v.y) == 0
                && Double.compare(z, v.z) == 0;
    }

    @Override
    public int hashCode() {
        int h = 17;
        h = 31 * h + Double.hashCode(x);
        h = 31 * h + Double.hashCode(y);
        h = 31 * h + Double.hashCode(z);
        h = 31 * h + getClass().hashCode();
        return h;
    }
}
