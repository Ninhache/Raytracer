package fr.ninhache.raytracer.geometry.bvh;

import fr.ninhache.raytracer.geometry.Intersection;
import fr.ninhache.raytracer.geometry.IShape;
import fr.ninhache.raytracer.geometry.Ray;
import fr.ninhache.raytracer.math.Epsilon;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Bounding Volume Hierarchy node. Holds either a single shape or two children.
 */
public final class BvhNode {

    private final BoundingBox bounds;
    private final BvhNode left;
    private final BvhNode right;
    private final IShape shape;

    private BvhNode(BoundingBox bounds, BvhNode left, BvhNode right, IShape shape) {
        this.bounds = bounds;
        this.left = left;
        this.right = right;
        this.shape = shape;
    }

    public static BvhNode build(List<ShapeBounds> shapes) {
        if (shapes == null || shapes.isEmpty()) {
            return null;
        }

        if (shapes.size() == 1) {
            ShapeBounds sb = shapes.get(0);
            return new BvhNode(sb.bounds(), null, null, sb.shape());
        }

        BoundingBox global = shapes.get(0).bounds();
        for (int i = 1; i < shapes.size(); i++) {
            global = global.union(shapes.get(i).bounds());
        }

        double dx = global.getMax().x - global.getMin().x;
        double dy = global.getMax().y - global.getMin().y;
        double dz = global.getMax().z - global.getMin().z;

        Comparator<ShapeBounds> comparator;
        if (dx >= dy && dx >= dz) {
            comparator = Comparator.comparingDouble(sb -> sb.bounds().getMin().x + sb.bounds().getMax().x);
        } else if (dy >= dx && dy >= dz) {
            comparator = Comparator.comparingDouble(sb -> sb.bounds().getMin().y + sb.bounds().getMax().y);
        } else {
            comparator = Comparator.comparingDouble(sb -> sb.bounds().getMin().z + sb.bounds().getMax().z);
        }

        List<ShapeBounds> sorted = new ArrayList<>(shapes);
        sorted.sort(comparator);
        int mid = sorted.size() / 2;

        List<ShapeBounds> leftShapes = sorted.subList(0, mid);
        List<ShapeBounds> rightShapes = sorted.subList(mid, sorted.size());

        // guard against degenerate splits
        if (leftShapes.isEmpty() || rightShapes.isEmpty()) {
            ShapeBounds sb = sorted.get(0);
            return new BvhNode(sb.bounds(), null, null, sb.shape());
        }

        BvhNode left = build(leftShapes);
        BvhNode right = build(rightShapes);
        BoundingBox nodeBounds = left.bounds.union(right.bounds);

        return new BvhNode(nodeBounds, left, right, null);
    }

    public Optional<Intersection> intersect(Ray ray, double currentBest) {
        if (bounds != null && !bounds.hit(ray, Epsilon.EPS, currentBest)) {
            return Optional.empty();
        }

        if (shape != null) {
            return shape.intersect(ray);
        }

        Intersection hit = null;
        double bestT = currentBest;

        if (left != null) {
            Optional<Intersection> lh = left.intersect(ray, bestT);
            if (lh.isPresent()) {
                hit = lh.get();
                bestT = hit.t();
            }
        }

        if (right != null) {
            Optional<Intersection> rh = right.intersect(ray, bestT);
            if (rh.isPresent()) {
                Intersection rHit = rh.get();
                if (hit == null || rHit.t() < bestT) {
                    hit = rHit;
                    bestT = rHit.t();
                }
            }
        }

        return Optional.ofNullable(hit);
    }

    public BoundingBox getBounds() {
        return bounds;
    }

    /**
     * Holder for a shape and its bounding box.
     */
    public record ShapeBounds(IShape shape, BoundingBox bounds) {
    }
}
