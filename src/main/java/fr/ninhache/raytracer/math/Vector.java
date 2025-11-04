package fr.ninhache.raytracer.math;

/**
 * Représente un vecteur 3D
 *
 * <h2>Contrats</h2>
 * Todo
 *
 * <h2>Exemples</h2>
 * Todo
 */
public final class Vector extends AbstractVec3 {

    /**
     * Crée un vecteur (x, y, z).
     * @param x composante x
     * @param y composante y
     * @param z composante z
     */
    public Vector(double x, double y, double z) {
        super(x, y, z);
    }

    @Override
    protected Vector ofSameType(double x, double y, double z) {
        return new Vector(x, y, z);
    }

    /**
     * Additionne deux vecteurs composante par composante.
     * @param b second vecteur
     * @return {@code this + b}
     */
    public Vector add(Vector b) {
        return new Vector(x + b.x, y + b.y, z + b.z);
    }

    /**
     * Soustrait deux vecteurs composante par composante.
     * @param b second vecteur
     * @return {@code this - b}
     */
    public Vector sub(Vector b) {
        return new Vector(x - b.x, y - b.y, z - b.z);
    }

    /**
     * Multiplie le vecteur par un scalaire
     * @param d scalaire
     * @return {@code d * this}
     */
    public Vector mul(double d) {
        return new Vector(d * x, d * y, d * z);
    }

    /**
     * Produit scalaire
     * @param b second vecteur
     * @return {@code this · b}
     */
    public double dot(Vector b) {
        return x * b.x + y * b.y + z * b.z;
    }

    /**
     * Produit vectoriel {@code this × b}.
     * @param b second vecteur
     * @return un nouveau vecteur orthogonal à {@code this} et {@code b}
     * @implNote Non commutatif&nbsp;: {@code a.cross(b) = -b.cross(a)}.
     * @see #dot(Vector)
     */
    public Vector cross(Vector b) {
        return new Vector(
                y*b.z - z*b.y,
                z*b.x - x*b.z,
                x*b.y - y*b.x
        );
    }

    /**
     * Produit de Schur (Hadamard), composante par composante.
     * @param b second vecteur
     * @return {@code (x*b.x, y*b.y, z*b.z)}
     */
    public Vector schur(Vector b) { return (Vector) super.schur(b); }
}
