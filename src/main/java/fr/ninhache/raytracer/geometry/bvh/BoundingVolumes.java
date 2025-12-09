package fr.ninhache.raytracer.geometry.bvh;

import fr.ninhache.raytracer.geometry.IShape;
import fr.ninhache.raytracer.geometry.shape.Disk;
import fr.ninhache.raytracer.geometry.shape.Plane;
import fr.ninhache.raytracer.geometry.shape.RegularPolygon;
import fr.ninhache.raytracer.geometry.shape.Sphere;
import fr.ninhache.raytracer.geometry.shape.Triangle;
import fr.ninhache.raytracer.math.Point;

/**
 * Utility methods to create axis-aligned bounding boxes for known shapes.
 */
public final class BoundingVolumes {
    private BoundingVolumes() {}

    public static BoundingBox forShape(IShape shape) {
        if (shape instanceof Sphere sphere) {
            Point c = sphere.getCenter();
            double r = sphere.getRadius();
            Point min = new Point(c.x - r, c.y - r, c.z - r);
            Point max = new Point(c.x + r, c.y + r, c.z + r);
            return BoundingBox.of(min, max);
        }

        if (shape instanceof Triangle tri) {
            Point v1 = tri.getV1();
            Point v2 = tri.getV2();
            Point v3 = tri.getV3();
            return BoundingBox.fromPoints(v1, v2, v3);
        }

        if (shape instanceof Disk disk) {
            Point c = disk.getCenter();
            double r = disk.getRadius();
            Point min = new Point(c.x - r, c.y - r, c.z - r);
            Point max = new Point(c.x + r, c.y + r, c.z + r);
            return BoundingBox.of(min, max);
        }

        if (shape instanceof RegularPolygon poly) {
            Point c = poly.getCenter();
            double r = poly.getRadius();
            Point min = new Point(c.x - r, c.y - r, c.z - r);
            Point max = new Point(c.x + r, c.y + r, c.z + r);
            return BoundingBox.of(min, max);
        }

        if (shape instanceof Plane) {
            return BoundingBox.infinite();
        }

        // Fallback : indiquer qu'on ne sait pas borner proprement
        return BoundingBox.infinite();
    }
}
