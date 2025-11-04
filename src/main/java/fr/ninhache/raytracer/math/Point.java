package fr.ninhache.raytracer.math;

/**
 * Représente un point 3D
 * Todo
 *
 * <h2>Contrats</h2>
 * Todo
 *
 * <h2>Exemples</h2>
 * Todo
 */
public final class Point extends AbstractVec3 {

    /**
     * Crée un point aux coordonnées données
     *
     * @param x composante x
     * @param y composante y
     * @param z composante z
     */
    public Point(double x, double y, double z) {
        super(x, y, z);
    }

    @Override
    protected Point ofSameType(double x, double y, double z) {
        return new Point(x, y, z);
    }

    /**
     * Soustrait un autre point
     *
     * @param other point de référence
     * @return vecteur {@code this - other}
     */
    public Vector sub(Point other) {
        return new Vector(x - other.x, y - other.y, z - other.z);
    }

    /**
     * Ajoute un vecteur à ce point
     *
     * @param v vecteur
     * @return nouveau point {@code this + v}
     */
    public Point add(Vector v) {
        return new Point(x + v.x, y + v.y, z + v.z);
    }

    /**
     * Soustrait un vecteur
     *
     * @param v vecteur à soustraire
     * @return nouveau point {@code this - v}
     */
    public Point sub(Vector v) {
        return new Point(x - v.x, y - v.y, z - v.z);
    }

    /**
     * Calcule la distance euclidienne à un autre point
     *
     * @param other autre point
     * @return distance (>= 0)
     */
    public double distance(Point other) {
        return this.sub(other).length();
    }

    /**
     * Calcule la distance au carré (évite un sqrt, plus rapide)
     *
     * @param other autre point
     * @return distance²
     */
    public double distanceSquared(Point other) {
        Vector v = this.sub(other);
        return v.lengthSquared();
    }
}
