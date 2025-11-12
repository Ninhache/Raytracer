package fr.ninhache.raytracer.lighting;

import fr.ninhache.raytracer.math.Color;
import fr.ninhache.raytracer.math.Point;

/**
 * Représente une source lumineuse ponctuelle (ampoule, bougie, lampe)
 */
public final class PointLight extends AbstractLight {

    private final Point position;

    /**
     * Crée une lumière ponctuelle.
     *
     * @param position position de la source lumineuse
     * @param color couleur/intensité de la lumière
     * @throws IllegalArgumentException si position est null
     */
    public PointLight(Point position, Color color) {
        super(color);

        if (position == null) {
            throw new IllegalArgumentException("La position ne peut pas être null");
        }

        this.position = position;
    }

    /**
     * @return la position de la source lumineuse
     */
    public Point getPosition() {
        return position;
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