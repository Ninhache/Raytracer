package fr.ninhache.raytracer.lighting;

import fr.ninhache.raytracer.math.Color;
import fr.ninhache.raytracer.math.Point;
import fr.ninhache.raytracer.math.Vector;

/** Lumière ponctuelle. */
public final class PointLight extends AbstractLight {
    private final Point position;

    public PointLight(Point position, Color color) {
        super(color);
        if (position == null) throw new IllegalArgumentException("position ne peut pas être null");
        this.position = position;
    }

    public Point getPosition() { return position; }


    public Vector incidentFrom(Point hitPoint) {
        return position.sub(hitPoint).normalized();
    }

    @Override
    public String describe() {
        return String.format("PointLight[position=%s, color=%s]",
                position, color);
    }

    @Override
    public String toString() {
        return describe();
    }
}
