package fr.ninhache.raytracer.math;

/**
 * Classe abstraite représentant un vecteur 3D immuable avec des composantes (x, y, z).
 *
 * <p>Cette classe sert de base pour différents types de vecteurs 3D (positions, directions, couleurs RGB, etc.)
 * tout en garantissant l'immutabilité et en fournissant des opérations mathématiques communes.
 *
 * <h2>Contrats</h2>
 * <ul>
 *   <li>Les composantes x, y, z sont immuables après construction.</li>
 *   <li>Les méthodes retournant un vecteur créent toujours une nouvelle instance (pas de mutation).</li>
 *   <li>Les comparaisons avec {@link #almostEquals} utilisent une tolérance relative, robuste aux grandes et petites valeurs.</li>
 *   <li>L'égalité stricte ({@link #equals}) compare les composantes au bit près et vérifie le type exact.</li>
 * </ul>
 *
 * <h2>Exemples</h2>
 * <pre>{@code
 * Vector v = new Vector(3, 4, 0);
 * double longueur = v.length();              // 5.0
 * Vector normalise = v.normalized();         // (0.6, 0.8, 0.0)
 * boolean estPetit = v.isZero(1e-6);         // false
 * }</pre>
 */
public abstract class AbstractVec3 {
    public final double x;
    public final double y;
    public final double z;

    /**
     * Construit un vecteur 3D avec les composantes spécifiées
     *
     * @param x composante x du vecteur
     * @param y composante y du vecteur
     * @param z composante z du vecteur
     */
    protected AbstractVec3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Crée une nouvelle instance du même type concret que ce vecteur
     *
     * <p>Cette méthode est utilisée par le pattern Template Method pour permettre aux sous-classes
     * de retourner leur propre type lors des opérations (normalisation, produit de Schur, etc.).
     *
     * @param x composante x du nouveau vecteur
     * @param y composante y du nouveau vecteur
     * @param z composante z du nouveau vecteur
     *
     * @return une nouvelle instance du type concret avec les composantes spécifiées
     */
    protected abstract AbstractVec3 ofSameType(double x, double y, double z);




    /**
     * Calcule la norme de ce vecteur
     *
     * <p>La norme est définie comme : sqrt(x² + y² + z²)
     *
     * @return la norme du vecteur, toujours positive ou nulle
     */
    public double length() {
        return Math.sqrt(lengthSquared());
    }

    /**
     * Calcule le carré de la norme de ce vecteur.
     *
     * <p>Cette méthode est plus efficace que {@link #length()} car elle évite le calcul de la racine carrée.
     * Utile pour les comparaisons de distances où seul l'ordre relatif importe.
     *
     * <p>Il est définie comme : (x² + y² + z²)</p>
     *
     * @return le carré de la norme du vecteur
     */
    public double lengthSquared() {
        return x * x + y * y + z * z;
    }

    /**
     * Retourne une version normalisée de ce vecteur
     *
     * <p>Si ce vecteur est le vecteur nul (norme = 0), retourne le vecteur nul
     * Sinon, retourne un vecteur de norme 1 pointant dans la même direction
     *
     * <p><strong>Note :</strong> La détection du vecteur nul utilise une comparaison stricte avec 0.0
     * Pour des vecteurs extrêmement petits (norme < 1e-150), le comportement peut être imprévisible en raison de la précision des flottants
     *
     * @return un nouveau vecteur normalisé, ou le vecteur nul si la norme est nulle
     */
    public AbstractVec3 normalized() {
        double len2 = lengthSquared();
        if (len2 == 0.0) {
            return ofSameType(0, 0, 0);
        }
        double inv = 1.0 / Math.sqrt(len2);
        return ofSameType(x * inv, y * inv, z * inv);
    }

    /**
     * Calcule le produit de Schur avec un autre vecteur
     *
     * <p>Le produit de Schur est la multiplication composante par composante :
     * (a.x * b.x, a.y * b.y, a.z * b.z)
     *
     * <p>Cette opération est utile pour moduler des couleurs, appliquer des masques, etc.
     *
     * @param other l'autre vecteur (doit être non null)
     * @return un nouveau vecteur
     */
    protected AbstractVec3 schur(AbstractVec3 other) {
        return ofSameType(this.x * other.x, this.y * other.y, this.z * other.z);
    }

    /**
     * Vérifie si ce vecteur est considéré comme nul selon une tolérance donnée
     *
     * <p>Un vecteur est considéré nul si sa norme est inférieure ou égale à epsilon.
     *
     * @param epsilon la tolérance (doit être positive)
     * @throws IllegalArgumentException si epsilon n'est pas positif
     *
     * @return {@code true} si la norme du vecteur ≤ epsilon, {@code false} sinon
     */
    public boolean isZero(double epsilon) {
        if (epsilon <= 0.0) {
            throw new IllegalArgumentException("epsilon must be positive");
        }

        return lengthSquared() <= epsilon * epsilon;
    }

    /**
     * Vérifie si ce vecteur est approximativement égal à un autre vecteur
     *
     * <p>La comparaison utilise une tolérance <strong>relative</strong> pour chaque composante,
     * ce qui la rend robuste aux valeurs très grandes ou très petites.
     *
     * <p>Deux composantes a et b sont considérées égales si :
     * |a - b| <= epsilon × max(1, |a|, |b|)
     *
     * <p>Les valeurs "NaN" sont toujours considérées comme différentes.
     *
     * @param other le vecteur à comparer (peut être null)
     * @param epsilon la tolérance relative (typiquement 1e-9 pour une précision double)
     * @return {@code true} si tous les composants sont proches selon la tolérance,
     *         {@code false} si other est null, d'un type différent, ou si les composantes diffèrent
     */
    public boolean almostEquals(AbstractVec3 other, double epsilon) {
        if (other == null || !getClass().equals(other.getClass())) {
            return false;
        }
        return Epsilon.almostEqual(x, other.x, epsilon)
                && Epsilon.almostEqual(y, other.y, epsilon)
                && Epsilon.almostEqual(z, other.z, epsilon);
    }

    /**
     * Retourne une représentation textuelle de ce vecteur.
     *
     * <p>Format : "NomDeClasse(x, y, z)"
     *
     * @return une chaîne descriptive incluant le type et les composantes
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + x + ", " + y + ", " + z + ")";
    }

    /**
     * Teste l'égalité stricte avec un autre objet.
     *
     * <p>Deux vecteurs sont égaux si et seulement si :
     * <ul>
     *   <li>Ils sont du même type concret (la classe doit être identique)</li>
     *   <li>Leurs composantes x, y, z sont identiques au bit près (comparaison exacte)</li>
     * </ul>
     *
     * <p>Pour une comparaison avec tolérance, utiliser {@link #almostEquals}.
     *
     * @param o l'objet à comparer
     * @return {@code true} si les vecteurs sont strictement égaux, {@code false} sinon
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractVec3 v)) return false;
        if (!getClass().equals(v.getClass())) return false;
        return Double.compare(x, v.x) == 0
                && Double.compare(y, v.y) == 0
                && Double.compare(z, v.z) == 0;
    }

    /**
     * Calcule le code de hachage de ce vecteur.
     *
     * <p>Le hash prend en compte les trois composantes ainsi que le type concret de la classe,
     * garantissant que des vecteurs de types différents mais avec les mêmes valeurs
     * n'auront généralement pas le même hash.
     *
     * @return le code de hachage
     */
    @Override
    public int hashCode() {
        int h = 17;
        h = 31 * h + Double.hashCode(x);
        h = 31 * h + Double.hashCode(y);
        h = 31 * h + Double.hashCode(z);
        h = 31 * h + getClass().hashCode();
        return h;
    }
}