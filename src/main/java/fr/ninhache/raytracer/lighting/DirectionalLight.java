package fr.ninhache.raytracer.lighting;

import fr.ninhache.raytracer.math.Color;
import fr.ninhache.raytracer.math.Vector;

/**
 * Représente une source lumineuse directionnelle (soleil, lune)
 */
public final class DirectionalLight extends AbstractLight {

    private final Vector direction;

    /**
     * Crée une lumière directionnelle.
     *
     * @param direction direction des rayons lumineux (sera normalisée)
     * @param color couleur/intensité de la lumière
     * @throws IllegalArgumentException si direction est null ou nulle
     */
    public DirectionalLight(Vector direction, Color color) {
        super(color);

        if (direction == null) {
            throw new IllegalArgumentException("La direction ne peut pas être null");
        }
        if (direction.isZero(1e-10)) {
            throw new IllegalArgumentException("La direction ne peut pas être le vecteur nul");
        }

        this.direction = direction.normalized();
    }

    /**
     * @return la direction des rayons lumineux (vecteur unitaire)
     */
    public Vector getDirection() {
        return direction;
    }

    @Override
    public String describe() {
        return String.format("DirectionalLight[direction=%s, color=%s]",
                direction, color);
    }

    @Override
    public String toString() {
        return describe();
    }
}