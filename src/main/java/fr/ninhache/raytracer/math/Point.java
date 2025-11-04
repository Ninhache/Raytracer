package fr.ninhache.raytracer.math;

/**
 * Représente un point (position) dans l'espace 3D.
 *
 * <p>Un point diffère d'un vecteur par sa sémantique géométrique :
 * <ul>
 *   <li>Un <strong>point</strong> représente une <em>position absolue</em> dans l'espace</li>
 *   <li>Un <strong>vecteur</strong> représente une <em>direction</em> ou un <em>déplacement</em></li>
 * </ul>
 *
 * <h2>Contrats</h2>
 * <ul>
 *   <li><strong>Immutabilité</strong> : un point ne peut pas être modifié après création</li>
 *   <li><strong>Opérations géométriques valides</strong> :
 *     <ul>
 *       <li>Point - Point = Vector (vecteur de déplacement)</li>
 *       <li>Point + Vector = Point (translation)</li>
 *       <li>Point - Vector = Point (translation inverse)</li>
 *       <li>Point × scalaire = Point (homothétie depuis l'origine)</li>
 *     </ul>
 *   </li>
 *   <li><strong>Opérations interdites</strong> : Point + Point n'a pas de sens géométrique</li>
 * </ul>
 *
 * <h2>Exemples</h2>
 * <pre>{@code
 * // Définir des positions
 * Point origine = new Point(0, 0, 0);
 * Point cible = new Point(3, 4, 0);
 *
 * // Calculer un vecteur de déplacement
 * Vector direction = cible.sub(origine);  // (3, 4, 0)
 *
 * // Se déplacer le long d'un vecteur
 * Vector deplacement = new Vector(1, 0, 0);
 * Point nouveau = origine.add(deplacement);  // (1, 0, 0)
 *
 * // Calculer des distances
 * double dist = origine.distance(cible);  // 5.0
 *
 * // Point milieu (interpolation)
 * Point milieu = origine.mul(0.5).add(cible.mul(0.5));  // (1.5, 2.0, 0)
 * }</pre>
 *
 * @see Vector
 */
public final class Point extends AbstractVec3 {

    /**
     * Crée un point aux coordonnées spécifiées.
     *
     * @param x coordonnée x dans l'espace
     * @param y coordonnée y dans l'espace
     * @param z coordonnée z dans l'espace
     */
    public Point(double x, double y, double z) {
        super(x, y, z);
    }

    @Override
    protected Point ofSameType(double x, double y, double z) {
        return new Point(x, y, z);
    }

    /**
     * Soustrait un autre point pour obtenir le vecteur de déplacement.
     *
     * <p>Retourne le vecteur qui va de {@code other} vers {@code this}.
     * C'est l'opération fondamentale pour calculer des directions et des distances.
     *
     * <p><strong>Interprétation géométrique</strong> :
     * <pre>
     * Point A = (1, 2, 3)
     * Point B = (4, 6, 9)
     * Vector AB = B.sub(A) = (3, 4, 6)  // Direction de A vers B
     * </pre>
     *
     * @param other le point de départ (ne doit pas être null)
     * @return le vecteur {@code this - other}
     * @throws NullPointerException si other est null
     */
    public Vector sub(Point other) {
        return new Vector(x - other.x, y - other.y, z - other.z);
    }

    /**
     * Translate ce point selon un vecteur de déplacement.
     *
     * <p>Retourne un nouveau point décalé de {@code v} par rapport à ce point.
     *
     * <p><strong>Utilisation courante</strong> : déplacer un point le long d'un rayon,
     * appliquer une transformation, etc.
     *
     * <p><strong>Exemple</strong> :
     * <pre>{@code
     * Point origine = new Point(1, 2, 3);
     * Vector deplacement = new Vector(10, 0, 0);
     * Point destination = origine.add(deplacement);  // (11, 2, 3)
     * }</pre>
     *
     * @param v le vecteur de translation (ne doit pas être null)
     * @return un nouveau point {@code this + v}
     * @throws NullPointerException si v est null
     */
    public Point add(Vector v) {
        return new Point(x + v.x, y + v.y, z + v.z);
    }

    /**
     * Translate ce point dans la direction opposée à un vecteur.
     *
     * <p>Équivalent à {@code this.add(v.mul(-1))}, mais plus efficace.
     *
     * @param v le vecteur dont on soustrait les composantes (ne doit pas être null)
     * @return un nouveau point {@code this - v}
     * @throws NullPointerException si v est null
     */
    public Point sub(Vector v) {
        return new Point(x - v.x, y - v.y, z - v.z);
    }

    /**
     * Multiplie ce point par un scalaire (homothétie depuis l'origine).
     *
     * <p>Cette opération applique une mise à l'échelle par rapport à l'origine (0, 0, 0).
     *
     * <p><strong>Cas d'usage</strong> :
     * <ul>
     *   <li>Interpolation linéaire : {@code p1.mul(t).add(p2.mul(1-t))}</li>
     *   <li>Calcul de barycentre : {@code p1.mul(w1).add(p2.mul(w2)).add(p3.mul(w3))}</li>
     *   <li>Mise à l'échelle de scène</li>
     * </ul>
     *
     * @param scalar le facteur de multiplication
     * @return un nouveau point {@code (x×scalar, y×scalar, z×scalar)}
     */
    public Point mul(double scalar) {
        return new Point(x * scalar, y * scalar, z * scalar);
    }

    /**
     * Calcule la distance euclidienne entre ce point et un autre.
     *
     * <p>La distance est la norme du vecteur {@code this.sub(other)}.
     *
     * <p><strong>Note de performance</strong> : si seule la comparaison de distances
     * importe (sans besoin de la valeur exacte), préférer {@link #distanceSquared(Point)}
     * qui évite le calcul coûteux de la racine carrée.
     *
     * @param other l'autre point (ne doit pas être null)
     * @return la distance euclidienne (toujours ≥ 0)
     * @throws NullPointerException si other est null
     * @see #distanceSquared(Point)
     */
    public double distance(Point other) {
        return this.sub(other).length();
    }

    /**
     * Calcule le carré de la distance à un autre point.
     *
     * <p>Plus efficace que {@link #distance(Point)} car évite le calcul de la racine carrée.
     *
     * <p><strong>Utilisation</strong> : comparer des distances, tester si un point est dans
     * un rayon donné, trouver le point le plus proche, etc.
     *
     * <p><strong>Exemple - Test de proximité</strong> :
     * <pre>{@code
     * double rayonCarre = rayon * rayon;
     * if (point.distanceSquared(centre) <= rayonCarre) {
     *     // Point dans la sphère
     * }
     * }</pre>
     *
     * @param other l'autre point (ne doit pas être null)
     * @return le carré de la distance
     * @throws NullPointerException si other est null
     */
    public double distanceSquared(Point other) {
        Vector v = this.sub(other);
        return v.lengthSquared();
    }
}



