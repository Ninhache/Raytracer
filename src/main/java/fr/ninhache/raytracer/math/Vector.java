package fr.ninhache.raytracer.math;

/**
 * Représente un vecteur 3D (direction, déplacement ou normale).
 *
 * <p>Un vecteur diffère d'un point par sa sémantique géométrique :
 * <ul>
 *   <li>Un <strong>vecteur</strong> représente une <em>direction</em> ou un <em>déplacement</em></li>
 *   <li>Un <strong>point</strong> représente une <em>position absolue</em> dans l'espace</li>
 * </ul>
 *
 * <h2>Contrats</h2>
 * <ul>
 *   <li><strong>Immutabilité</strong> : un vecteur ne peut pas être modifié après création</li>
 *   <li><strong>Opérations vectorielles valides</strong> :
 *     <ul>
 *       <li>Vector + Vector = Vector (somme vectorielle)</li>
 *       <li>Vector - Vector = Vector (différence vectorielle)</li>
 *       <li>Vector × scalaire = Vector (multiplication par un scalaire)</li>
 *       <li>Vector · Vector = double (produit scalaire)</li>
 *       <li>Vector × Vector = Vector (produit vectoriel, non commutatif)</li>
 *     </ul>
 *   </li>
 *   <li><strong>Normalisation</strong> : un vecteur normalisé a une longueur de 1 (vecteur unitaire)</li>
 * </ul>
 *
 * <h2>Exemples</h2>
 * <pre>{@code
 * // Créer des vecteurs directionnels
 * Vector v1 = new Vector(3, 4, 0);
 * Vector v2 = new Vector(1, 0, 0);
 *
 * // Opérations vectorielles
 * Vector somme = v1.add(v2);           // (4, 4, 0)
 * Vector diff = v1.sub(v2);            // (2, 4, 0)
 * Vector scale = v1.mul(2);            // (6, 8, 0)
 *
 * // Produits
 * double dot = v1.dot(v2);             // 3.0
 * Vector cross = v1.cross(v2);         // (0, 0, -4)
 *
 * // Normalisation
 * Vector unitaire = v1.normalized();   // (0.6, 0.8, 0)
 * double longueur = v1.length();       // 5.0
 * }</pre>
 *
 * @see Point
 */
public final class Vector extends AbstractVec3 {
    public static Vector X_AXIS = new Vector(1, 0, 0);
    public static Vector Y_AXIS = new Vector(0, 1, 0);
    public static Vector Z_AXIS = new Vector(0, 0, 1);

    /**
     * Crée un vecteur avec les composantes spécifiées.
     *
     * @param x composante x du vecteur
     * @param y composante y du vecteur
     * @param z composante z du vecteur
     */
    public Vector(double x, double y, double z) {
        super(x, y, z);
    }

    @Override
    protected Vector ofSameType(double x, double y, double z) {
        return new Vector(x, y, z);
    }

    /**
     * Additionne ce vecteur avec un autre vecteur.
     *
     * <p>L'addition vectorielle est commutative : v1.add(v2) = v2.add(v1)
     *
     * <p><strong>Utilisation courante</strong> : combiner des forces, des vitesses,
     * accumuler des déplacements.
     *
     * @param other le vecteur à ajouter (ne doit pas être null)
     * @return un nouveau vecteur {@code this + other}
     * @throws NullPointerException si other est null
     */
    public Vector add(Vector other) {
        return new Vector(x + other.x, y + other.y, z + other.z);
    }

    /**
     * Soustrait un autre vecteur de ce vecteur.
     *
     * <p>La soustraction vectorielle n'est <strong>pas</strong> commutative :
     * v1.sub(v2) ≠ v2.sub(v1)
     *
     * @param other le vecteur à soustraire (ne doit pas être null)
     * @return un nouveau vecteur {@code this - other}
     * @throws NullPointerException si other est null
     */
    public Vector sub(Vector other) {
        return new Vector(x - other.x, y - other.y, z - other.z);
    }

    /**
     * Multiplie ce vecteur par un scalaire.
     *
     * <p>Cette opération change la longueur du vecteur sans modifier sa direction
     * (sauf si le scalaire est négatif, auquel cas le vecteur est inversé).
     *
     * <p><strong>Cas particuliers</strong> :
     * <ul>
     *   <li>mul(0) retourne le vecteur nul</li>
     *   <li>mul(1) retourne un vecteur identique</li>
     *   <li>mul(-1) inverse la direction</li>
     * </ul>
     *
     * @param scalar le facteur de multiplication
     * @return un nouveau vecteur {@code (x×scalar, y×scalar, z×scalar)}
     */
    public Vector mul(double scalar) {
        return new Vector(x * scalar, y * scalar, z * scalar);
    }

    /**
     * Divise ce vecteur par un scalaire.
     *
     * <p>Équivalent à {@code mul(1.0 / scalar)}, mais plus lisible.
     *
     * <p><strong>Attention</strong> : ne vérifie pas la division par zéro.
     * Si {@code scalar} est 0, le résultat contiendra des valeurs infinies.
     *
     * @param scalar le diviseur (ne doit pas être 0)
     * @return un nouveau vecteur {@code this / scalar}
     */
    public Vector div(double scalar) {
        return new Vector(x / scalar, y / scalar, z / scalar);
    }

    /**
     * Calcule le produit scalaire (dot product) avec un autre vecteur.
     *
     * <p>Le produit scalaire est défini par :
     * <pre>a · b = a.x×b.x + a.y×b.y + a.z×b.z = ||a|| × ||b|| × cos(θ)</pre>
     * où θ est l'angle entre les deux vecteurs.
     *
     * <p><strong>Propriétés</strong> :
     * <ul>
     *   <li>Commutatif : a.dot(b) = b.dot(a)</li>
     *   <li>Si a · b = 0, les vecteurs sont <strong>orthogonaux</strong></li>
     *   <li>Si a · b > 0, les vecteurs pointent dans des <strong>directions similaires</strong></li>
     *   <li>Si a · b < 0, les vecteurs pointent dans des <strong>directions opposées</strong></li>
     * </ul>
     *
     * <p><strong>Utilisations en raytracing</strong> :
     * <ul>
     *   <li>Calcul d'éclairage (loi de Lambert) : intensité ∝ normale · direction_lumière</li>
     *   <li>Test de visibilité (backface culling)</li>
     *   <li>Projection d'un vecteur sur un autre</li>
     *   <li>Calcul d'angles entre surfaces</li>
     * </ul>
     *
     * @param other l'autre vecteur (ne doit pas être null)
     * @return le produit scalaire (valeur réelle quelconque)
     * @throws NullPointerException si other est null
     */
    public double dot(Vector other) {
        return x * other.x + y * other.y + z * other.z;
    }

    /**
     * Calcule le produit vectoriel (cross product) avec un autre vecteur.
     *
     * <p>Le produit vectoriel retourne un vecteur <strong>perpendiculaire</strong> aux deux
     * vecteurs d'entrée, dont la norme est égale à l'aire du parallélogramme formé par a et b.
     *
     * <p><strong>Formule</strong> :
     * <pre>a × b = (a.y×b.z - a.z×b.y, a.z×b.x - a.x×b.z, a.x×b.y - a.y×b.x)</pre>
     *
     * <p><strong>Propriétés IMPORTANTES</strong> :
     * <ul>
     *   <li><strong>Non commutatif</strong> : a.cross(b) = -b.cross(a)</li>
     *   <li>La direction suit la <strong>règle de la main droite</strong></li>
     *   <li>Si a × b = 0, les vecteurs sont <strong>colinéaires</strong></li>
     *   <li>||a × b|| = ||a|| × ||b|| × sin(θ)</li>
     * </ul>
     *
     * <p><strong>Utilisations en raytracing</strong> :
     * <ul>
     *   <li>Calcul de normales de surfaces (triangles, polygones)</li>
     *   <li>Construction de repères orthonormés</li>
     *   <li>Détection d'orientation (sens horaire/antihoraire)</li>
     *   <li>Calcul de moments et rotations</li>
     * </ul>
     *
     * @param other l'autre vecteur (ne doit pas être null)
     * @return un vecteur perpendiculaire à this et other (règle de la main droite)
     * @throws NullPointerException si other est null
     */
    public Vector cross(Vector other) {
        return new Vector(
                y * other.z - z * other.y,
                z * other.x - x * other.z,
                x * other.y - y * other.x
        );
    }

    /**
     * Inverse la direction de ce vecteur.
     *
     * <p>Équivalent à {@code mul(-1)}, mais plus expressif sémantiquement.
     *
     * <p><strong>Utilisation</strong> : inverser une normale, calculer une direction opposée.
     *
     * @return un nouveau vecteur {@code (-x, -y, -z)}
     */
    public Vector negate() {
        return new Vector(-x, -y, -z);
    }

    /**
     * Retourne une version normalisée de ce vecteur (vecteur unitaire).
     *
     * <p>Un vecteur normalisé conserve la même direction mais a une longueur de 1.
     *
     * <p><strong>Note</strong> : si ce vecteur est nul ou quasi-nul (longueur < 1e-14),
     * retourne le vecteur nul (0, 0, 0).
     *
     * @return un nouveau vecteur de norme 1, ou le vecteur nul si la norme initiale est nulle
     */
    @Override
    public Vector normalized() {
        return (Vector) super.normalized();
    }

    /**
     * Calcule le produit de Schur (multiplication composante par composante).
     *
     * <p>Bien que rarement utilisé pour les vecteurs directionnels, cette opération
     * peut être utile pour certaines transformations ou masquages.
     *
     * @param other l'autre vecteur (ne doit pas être null)
     * @return un nouveau vecteur {@code (x×other.x, y×other.y, z×other.z)}
     * @throws NullPointerException si other est null
     */
    public Vector schur(Vector other) {
        return (Vector) super.schur(other);
    }

    /**
     * Projette ce vecteur sur un autre vecteur.
     *
     * <p>La projection de a sur b est le vecteur colinéaire à b dont l'extrémité
     * est le point le plus proche de a le long de b.
     *
     * <p><strong>Formule</strong> : proj_b(a) = ((a · b) / (b · b)) × b
     *
     * <p><strong>Précondition</strong> : le vecteur {@code onto} ne doit pas être nul.
     *
     * @param onto le vecteur sur lequel projeter (ne doit pas être nul)
     * @return la projection de ce vecteur sur {@code onto}
     * @throws ArithmeticException si onto est le vecteur nul
     */
    public Vector projectOnto(Vector onto) {
        double ontoLengthSq = onto.lengthSquared();
        if (ontoLengthSq == 0.0) {
            throw new ArithmeticException("Cannot project onto zero vector");
        }
        double scalar = this.dot(onto) / ontoLengthSq;
        return onto.mul(scalar);
    }

    /**
     * Réfléchit ce vecteur par rapport à une normale.
     *
     * <p>Calcule la réflexion spéculaire de ce vecteur incident par rapport à une surface
     * dont la normale est {@code normal}.
     *
     * <p><strong>Formule</strong> : r = v - 2×(v · n)×n
     *
     * <p><strong>Précondition</strong> : la normale doit être unitaire (longueur = 1).
     *
     * <p><strong>Utilisation en raytracing</strong> : calcul de rayons réfléchis pour
     * les surfaces spéculaires (miroirs, métaux).
     *
     * @param normal la normale de surface (doit être normalisée)
     * @return le vecteur réfléchi
     */
    public Vector reflect(Vector normal) {
        double dotProduct = this.dot(normal);
        return this.sub(normal.mul(2.0 * dotProduct));
    }
}

