package fr.ninhache.raytracer.geometry.bvh;

import fr.ninhache.raytracer.geometry.Ray;
import fr.ninhache.raytracer.math.Point;

/**
 * Axis-aligned bounding box used by the BVH accelerator.
 */
public final class BoundingBox {

    private final Point min;
    private final Point max;

    private BoundingBox(Point min, Point max) {
        this.min = min;
        this.max = max;
    }

    public static BoundingBox of(Point min, Point max) {
        return new BoundingBox(min, max);
    }

    public static BoundingBox fromPoints(Point... pts) {
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double minZ = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        double maxZ = Double.NEGATIVE_INFINITY;

        for (Point p : pts) {
            minX = Math.min(minX, p.x);
            minY = Math.min(minY, p.y);
            minZ = Math.min(minZ, p.z);
            maxX = Math.max(maxX, p.x);
            maxY = Math.max(maxY, p.y);
            maxZ = Math.max(maxZ, p.z);
        }

        return new BoundingBox(new Point(minX, minY, minZ), new Point(maxX, maxY, maxZ));
    }

    public static BoundingBox infinite() {
        return new BoundingBox(
                new Point(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY),
                new Point(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)
        );
    }

    public BoundingBox union(BoundingBox other) {
        return new BoundingBox(
                new Point(Math.min(min.x, other.min.x), Math.min(min.y, other.min.y), Math.min(min.z, other.min.z)),
                new Point(Math.max(max.x, other.max.x), Math.max(max.y, other.max.y), Math.max(max.z, other.max.z))
        );
    }

    public boolean isInfinite() {
        return !Double.isFinite(min.x) || !Double.isFinite(min.y) || !Double.isFinite(min.z)
                || !Double.isFinite(max.x) || !Double.isFinite(max.y) || !Double.isFinite(max.z);
    }

    public boolean hit(Ray ray, double tMin, double tMax) {
        double ox = ray.origin().x;
        double oy = ray.origin().y;
        double oz = ray.origin().z;

        double dx = ray.direction().x;
        double dy = ray.direction().y;
        double dz = ray.direction().z;

        double invDx = 1.0 / dx;
        double invDy = 1.0 / dy;
        double invDz = 1.0 / dz;

        double tx1 = (min.x - ox) * invDx;
        double tx2 = (max.x - ox) * invDx;
        double tmin = Math.min(tx1, tx2);
        double tmax = Math.max(tx1, tx2);

        double ty1 = (min.y - oy) * invDy;
        double ty2 = (max.y - oy) * invDy;
        tmin = Math.max(tmin, Math.min(ty1, ty2));
        tmax = Math.min(tmax, Math.max(ty1, ty2));

        double tz1 = (min.z - oz) * invDz;
        double tz2 = (max.z - oz) * invDz;
        tmin = Math.max(tmin, Math.min(tz1, tz2));
        tmax = Math.min(tmax, Math.max(tz1, tz2));

        return tmax >= Math.max(tMin, tmin) && tmin <= tMax;
    }

    public Point getMin() {
        return min;
    }

    public Point getMax() {
        return max;
    }
}
