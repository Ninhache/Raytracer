package fr.ninhache.raytracer.math;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Utilitaires de tests pour les assertions numériques en virgule flottante.
 * <p>
 * Cette classe centralise une assertion de quasi-égalité pour les {@code double}
 * basée sur une tolérance relative, adaptée aux comparaisons stables en présence
 * d’erreurs d’arrondi.
 * </p>
 *
 * <h2>Principe de la tolérance</h2>
 * <ul>
 *   <li>On considère la différence absolue {@code |a - b|} acceptable si elle est
 *       inférieure ou égale à {@code EPS * scale}.</li>
 *   <li>{@code scale = max(1, |a|, |b|)} permet de passer d’une tolérance absolue à
 *       une tolérance <em>relative</em> quand les valeurs sont de grande amplitude,
 *       tout en restant raisonnable près de zéro.</li>
 *   <li>La constante {@link #EPS} vaut {@code 1e-9} par défaut, ce qui est adapté
 *       à la plupart des tests de géométrie 3D en {@code double}.</li>
 * </ul>
 *
 * <h2>Contrats</h2>
 * <ul>
 *   <li>Si l’une des valeurs est {@code NaN}, l’assertion échoue.</li>
 *   <li>L’assertion est symétrique : {@code assertAlmost(a, b)} équivaut à
 *       {@code assertAlmost(b, a)}.</li>
 *   <li>Le message d’erreur détaille la valeur attendue et la valeur obtenue.</li>
 * </ul>
 *
 * <h2>Exemple</h2>
 * <pre>{@code
 * double a = 0.1 + 0.2; // 0.30000000000000004
 * double b = 0.3;
 * TestUtils.assertAlmost(a, b); // passe grâce à la tolérance
 * }</pre>
 */
public class TestUtils {
    private TestUtils() {}

    /**
     * Tolérance par défaut utilisée par {@link #assertAlmost(double, double)}.
     */
    public static final double EPS = 1e-9;

    /**
     * Vérifie que deux réels sont quasi-égaux avec une tolérance relative {@link #EPS}.
     * <p>
     * Le test réussit si {@code |actual - expected| <= EPS * max(1, |actual|, |expected|)}.
     * </p>
     *
     * @param actual   valeur observée
     * @param expected valeur attendue
     * @throws AssertionError si la quasi-égalité n’est pas respectée
     */
    public static void assertAlmost(double actual, double expected) {
        double diff = Math.abs(actual - expected);
        double scale = Math.max(1.0, Math.max(Math.abs(actual), Math.abs(expected)));
        assertTrue(diff <= EPS * scale, () -> "expected " + expected + " but got " + actual);
    }
}
