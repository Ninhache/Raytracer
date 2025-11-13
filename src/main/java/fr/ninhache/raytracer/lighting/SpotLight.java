package fr.ninhache.raytracer.lighting;

import fr.ninhache.raytracer.math.Color;
import fr.ninhache.raytracer.math.Point;
import fr.ninhache.raytracer.math.Vector;

/**
 * Représente un projecteur (spotlight)
 */
public final class SpotLight extends AbstractLight {

    private final Point position;
    private final Vector direction;
    private final double coneAngleDegrees;
    private final double penumbraAngleDegrees;

    /**
     * Crée un projecteur.
     *
     * @param position position du projecteur
     * @param direction direction d'éclairage principal
     * @param coneAngleDegrees angle du cône d'éclairage (en degrés)
     * @param penumbraAngleDegrees angle de pénombre (transition douce, en degrés)
     * @param color couleur/intensité de la lumière
     */
    public SpotLight(Point position, Vector direction,
                     double coneAngleDegrees, double penumbraAngleDegrees,
                     Color color) {
        super(color);

        if (position == null || direction == null) {
            throw new IllegalArgumentException("Position et direction requises");
        }
        if (coneAngleDegrees <= 0 || coneAngleDegrees > 180) {
            throw new IllegalArgumentException("L'angle du cône doit être dans ]0, 180]");
        }
        if (penumbraAngleDegrees < 0 || penumbraAngleDegrees > coneAngleDegrees) {
            throw new IllegalArgumentException("L'angle de pénombre doit être dans [0, coneAngle]");
        }

        this.position = position;
        this.direction = direction.normalized();
        this.coneAngleDegrees = coneAngleDegrees;
        this.penumbraAngleDegrees = penumbraAngleDegrees;
    }

    /**
     * @return la position du projecteur
     */
    public Point getPosition() {
        return position;
    }

    /**
     * @return la direction principale d'éclairage
     */
    public Vector getDirection() {
        return direction;
    }

    /**
     * @return l'angle du cône d'éclairage (en degrés)
     */
    public double getConeAngleDegrees() {
        return coneAngleDegrees;
    }

    /**
     * @return l'angle de pénombre (transition douce, en degrés)
     */
    public double getPenumbraAngleDegrees() {
        return penumbraAngleDegrees;
    }

    /**
     * Calcule le facteur d'atténuation angulaire pour un point donné.
     *
     * <p>Retourne :
     * <ul>
     *   <li>1.0 si le point est dans le cône principal</li>
     *   <li>[0.0, 1.0] si le point est dans la pénombre (transition)</li>
     *   <li>0.0 si le point est hors du cône</li>
     * </ul>
     *
     * @param pointDirection direction du projecteur vers le point
     * @return facteur d'atténuation angulaire [0, 1]
     */
    public double getAngularAttenuation(Vector pointDirection) {
        double cosAngle = direction.dot(pointDirection.normalized());
        double angleDegrees = Math.toDegrees(Math.acos(cosAngle));

        double innerAngle = coneAngleDegrees - penumbraAngleDegrees;

        if (angleDegrees <= innerAngle) {
            return 1.0; // Pleine intensité
        } else if (angleDegrees <= coneAngleDegrees) {
            // Transition linéaire dans la pénombre
            double t = (angleDegrees - innerAngle) / penumbraAngleDegrees;
            return 1.0 - t;
        } else {
            return 0.0; // Hors du cône
        }
    }


    @Override
    public Vector incidentFrom(Point hitPoint) {
        return null;
    }

    @Override
    public String describe() {
        return String.format("SpotLight[position=%s, direction=%s, cone=%.1f°, color=%s]",
                position, direction, coneAngleDegrees, color);
    }

    @Override
    public String toString() {
        return describe();
    }
}