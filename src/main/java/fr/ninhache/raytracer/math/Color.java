package fr.ninhache.raytracer.math;

import static java.lang.Math.clamp;

/**
 * Représente une couleur RGB en virgule flottante.
 *
 * <p>Chaque composante (rouge, vert, bleu) est stockée comme un {@code double} dans
 * l'intervalle [0.0, 1.0], où :
 * <ul>
 *   <li>0.0 représente l'absence de la composante</li>
 *   <li>1.0 représente l'intensité maximale</li>
 * </ul>
 *
 * <p><strong>Note importante</strong> : en raytracing, les calculs intermédiaires peuvent
 * produire des valeurs supérieures à 1.0 (HDR - High Dynamic Range). Le clamping n'est
 * appliqué que lors de la conversion vers RGB entier via {@link #toRGB()}.
 *
 * <h2>Contrats</h2>
 * <ul>
 *   <li><strong>Immutabilité</strong> : une couleur ne peut pas être modifiée après création</li>
 *   <li><strong>Opérations valides</strong> :
 *     <ul>
 *       <li>Color + Color = Color (mélange additif)</li>
 *       <li>Color - Color = Color (différence de couleurs)</li>
 *       <li>Color × scalaire = Color (atténuation/intensification)</li>
 *       <li>Color × Color = Color (modulation, produit de Schur)</li>
 *     </ul>
 *   </li>
 *   <li><strong>Pas de clamping automatique</strong> : les valeurs peuvent dépasser [0, 1] pendant les calculs</li>
 *   <li><strong>Clamping à l'export</strong> : {@link #toRGB()} force les valeurs dans [0, 255]</li>
 * </ul>
 *
 * <h2>Exemples</h2>
 * <pre>{@code
 * // Couleurs de base
 * Color noir = new Color();              // (0, 0, 0) par défaut
 * Color rouge = new Color(1, 0, 0);
 * Color vert = new Color(0, 1, 0);
 * Color bleu = new Color(0, 0, 1);
 *
 * // Mélanges
 * Color jaune = rouge.add(vert);         // (1, 1, 0)
 * Color magenta = rouge.add(bleu);       // (1, 0, 1)
 * Color cyan = vert.add(bleu);           // (0, 1, 1)
 * Color blanc = rouge.add(vert).add(bleu); // (1, 1, 1)
 *
 * // Atténuation
 * Color demiRouge = rouge.mul(0.5);      // (0.5, 0, 0)
 *
 * // Modulation (éclairage)
 * Color rougeOmbré = rouge.schur(new Color(0.5, 0.5, 0.5)); // (0.5, 0, 0)
 *
 * // Conversion vers RGB 8-bit
 * int rgb = blanc.toRGB();  // 0xFFFFFF
 * }</pre>
 */
public final class Color extends AbstractVec3 {
    public static final Color BLACK = new Color(0, 0, 0);
    public static final Color BLUE = new Color(0, 0, 1);
    public static final Color GREEN = new Color(0, 1, 0);
    public static final Color CYAN = new Color(0, 1, 1);
    public static final Color RED = new Color(1, 0, 0);
    public static final Color MAGENTA = new Color(1, 0, 1);
    public static final Color YELLOW = new Color(1, 1, 0);
    public static final Color WHITE = new Color(1, 1, 1);

    /**
     * Crée une couleur noire (0, 0, 0).
     *
     * <p>C'est le constructeur par défaut conformément aux spécifications.
     */
    public Color() {
        super(0, 0, 0);
    }

    /**
     * Crée une couleur avec les composantes RGB spécifiées.
     *
     * <p><strong>Note</strong> : les valeurs ne sont PAS clampées à [0, 1] lors de la construction.
     * Cela permet de gérer les couleurs HDR pendant les calculs de raytracing.
     * Le clamping est appliqué uniquement lors de la conversion vers RGB entier avec {@link #toRGB()}.
     *
     * @param r composante rouge (typiquement entre 0.0 et 1.0, mais peut dépasser)
     * @param g composante verte (typiquement entre 0.0 et 1.0, mais peut dépasser)
     * @param b composante bleue (typiquement entre 0.0 et 1.0, mais peut dépasser)
     */
    public Color(double r, double g, double b) {
        super(r, g, b);
    }

    /**
     * Accesseur pour la composante rouge.
     *
     * @return la composante rouge (équivalent à {@code this.x})
     */
    public double r() {
        return x;
    }

    /**
     * Accesseur pour la composante verte.
     *
     * @return la composante verte (équivalent à {@code this.y})
     */
    public double g() {
        return y;
    }

    /**
     * Accesseur pour la composante bleue.
     *
     * @return la composante bleue (équivalent à {@code this.z})
     */
    public double b() {
        return z;
    }

    @Override
    protected Color ofSameType(double x, double y, double z) {
        return new Color(x, y, z);
    }

    /**
     * Additionne cette couleur avec une autre (mélange additif).
     *
     * <p>Le mélange additif est utilisé pour combiner plusieurs sources lumineuses.
     *
     * <p><strong>Exemple</strong> : rouge + vert = jaune
     *
     * <p><strong>Note</strong> : le résultat peut avoir des composantes > 1.0 (surexposition).
     *
     * @param other la couleur à ajouter (ne doit pas être null)
     * @return une nouvelle couleur {@code this + other}
     * @throws NullPointerException si other est null
     */
    public Color add(Color other) {
        return new Color(x + other.x, y + other.y, z + other.z);
    }

    /**
     * Soustrait une autre couleur de cette couleur.
     *
     * <p>Opération rarement utilisée, mais peut être utile pour certains effets
     * ou calculs de différences.
     *
     * <p><strong>Note</strong> : le résultat peut avoir des composantes négatives.
     *
     * @param other la couleur à soustraire (ne doit pas être null)
     * @return une nouvelle couleur {@code this - other}
     * @throws NullPointerException si other est null
     */
    public Color sub(Color other) {
        return new Color(x - other.x, y - other.y, z - other.z);
    }

    /**
     * Multiplie cette couleur par un scalaire (atténuation ou intensification).
     *
     * <p>Cette opération modifie uniformément l'intensité de toutes les composantes.
     *
     * <p><strong>Cas d'usage</strong> :
     * <ul>
     *   <li>Atténuation par distance : {@code couleur.mul(1.0 / (distance * distance))}</li>
     *   <li>Intensité lumineuse : {@code couleurLumiere.mul(intensite)}</li>
     *   <li>Assombrissement : {@code couleur.mul(0.5)}</li>
     * </ul>
     *
     * @param scalar le facteur de multiplication
     * @return une nouvelle couleur {@code (r×scalar, g×scalar, b×scalar)}
     */
    public Color mul(double scalar) {
        return new Color(x * scalar, y * scalar, z * scalar);
    }

    /**
     * Divise cette couleur par un scalaire.
     *
     * <p>Utile pour moyenner des couleurs ou normaliser des accumulations.
     *
     * <p><strong>Attention</strong> : ne vérifie pas la division par zéro.
     *
     * @param scalar le diviseur (ne doit pas être 0)
     * @return une nouvelle couleur {@code this / scalar}
     */
    public Color div(double scalar) {
        return new Color(x / scalar, y / scalar, z / scalar);
    }

    /**
     * Calcule le produit de Schur avec une autre couleur (modulation).
     *
     * <p>Le produit de Schur multiplie les composantes correspondantes et est utilisé pour :
     * <ul>
     *   <li>Modulation de couleur d'objet par la couleur de lumière</li>
     *   <li>Application de textures colorées</li>
     *   <li>Filtrage de couleurs</li>
     * </ul>
     *
     * <p><strong>Exemple physique</strong> : un objet rouge (1, 0, 0) éclairé par une lumière
     * verte (0, 1, 0) apparaît noir car (1, 0, 0) × (0, 1, 0) = (0, 0, 0).
     *
     * @param other la couleur de modulation (ne doit pas être null)
     * @return une nouvelle couleur {@code (r1×r2, g1×g2, b1×b2)}
     * @throws NullPointerException si other est null
     */
    public Color schur(Color other) {
        return (Color) super.schur(other);
    }

    /**
     * Convertit cette couleur en format RGB entier 24-bit.
     *
     * <p>Chaque composante flottante [0.0, 1.0] est convertie en entier [0, 255] avec :
     * <ul>
     *   <li><strong>Clamping</strong> : les valeurs &lt; 0 deviennent 0, les valeurs &gt; 1 deviennent 1</li>
     *   <li><strong>Troncature</strong> : la composante clampée est multipliée par 255 puis convertie en entier
     *       via un cast (par exemple 0.5 → 127)</li>
     *   <li><strong>Packing</strong> : format 0xRRGGBB (sans canal alpha)</li>
     * </ul>
     *
     * <p><strong>Format de retour</strong> : entier encodé sur 24 bits dans l'ordre RGB :
     * <pre>
     * Bits 23-16 : Rouge (0-255)
     * Bits 15-8  : Vert (0-255)
     * Bits 7-0   : Bleu (0-255)
     * </pre>
     *
     * <p><strong>Exemples</strong> :
     * <ul>
     *   <li>Color(1, 0, 0).toRGB() = 0xFF0000 (rouge pur)</li>
     *   <li>Color(1, 1, 1).toRGB() = 0xFFFFFF (blanc)</li>
     *   <li>Color(0, 0, 0).toRGB() = 0x000000 (noir)</li>
     *   <li>Color(1.5, 0.5, -0.2).toRGB() = 0xFF7F00 (clamping et troncature appliqués)</li>
     * </ul>
     *
     * @return un entier représentant la couleur au format RGB 24-bit
     */
    public int toRGB() {
        int red   = (int) (clamp(x, 0.0, 1.0) * 255);
        int green = (int) (clamp(y, 0.0, 1.0) * 255);
        int blue  = (int) (clamp(z, 0.0, 1.0) * 255);

        return ((red & 0xFF) << 16) | ((green & 0xFF) << 8) | (blue & 0xFF);
    }

    /**
     * Crée une couleur à partir de composantes RGB entières [0, 255].
     *
     * <p>Méthode utilitaire pour créer des couleurs à partir de valeurs RGB classiques.
     *
     * @param red composante rouge (0-255)
     * @param green composante verte (0-255)
     * @param blue composante bleue (0-255)
     * @return une nouvelle couleur avec des composantes flottantes [0.0, 1.0]
     */
    public static Color fromRGB(int red, int green, int blue) {
        return new Color(red / 255.0, green / 255.0, blue / 255.0);
    }

    /**
     * Crée une couleur à partir d'un entier RGB packed.
     *
     * @param rgb entier au format 0xRRGGBB
     * @return une nouvelle couleur
     */
    public static Color fromRGB(int rgb) {
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;
        return fromRGB(red, green, blue);
    }

    public Color gamma(double gamma) {
        return new Color(
                Math.pow(r(), 1.0 / gamma),
                Math.pow(g(), 1.0 / gamma),
                Math.pow(b(), 1.0 / gamma)
        );
    }
}
