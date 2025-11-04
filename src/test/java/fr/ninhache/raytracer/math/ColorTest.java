package fr.ninhache.raytracer.math;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static fr.ninhache.raytracer.math.TestUtils.EPS;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de la classe Color")
class ColorTest {

    @Test
    @DisplayName("Constructeur par défaut crée une couleur noire")
    void defaultConstructor_createsBlackColor() {
        Color color = new Color();

        assertTrue(color.almostEquals(new Color(0, 0, 0), EPS));
        assertEquals(Color.BLACK, color);
    }

    @Test
    @DisplayName("Accesseurs r(), g(), b() retournent les bonnes composantes")
    void accessors_returnCorrectComponents() {
        Color color = new Color(0.3, 0.5, 0.7);

        assertEquals(0.3, color.r(), EPS);
        assertEquals(0.5, color.g(), EPS);
        assertEquals(0.7, color.b(), EPS);
    }

    @Test
    @DisplayName("add() - mélange additif de couleurs")
    void add_twoColors_returnsAdditiveBlend() {
        Color rouge = new Color(1, 0, 0);
        Color vert = new Color(0, 1, 0);

        Color jaune = rouge.add(vert);

        assertTrue(jaune.almostEquals(new Color(1, 1, 0), EPS));
        assertTrue(jaune.almostEquals(Color.YELLOW, EPS));
    }

    @Test
    @DisplayName("add() - permet la surexposition (> 1.0)")
    void add_allowsOverexposure() {
        Color c1 = new Color(0.8, 0.6, 0.4);
        Color c2 = new Color(0.5, 0.7, 0.9);

        Color result = c1.add(c2);

        assertTrue(result.r() > 1.0, "La composante rouge devrait dépasser 1.0");
        assertTrue(result.g() > 1.0, "La composante verte devrait dépasser 1.0");
        assertTrue(result.b() > 1.0, "La composante bleue devrait dépasser 1.0");
    }

    @Test
    @DisplayName("sub() - soustrait deux couleurs")
    void sub_twoColors_returnsDifference() {
        Color blanc = new Color(1, 1, 1);
        Color rouge = new Color(1, 0, 0);

        Color cyan = blanc.sub(rouge);

        assertTrue(cyan.almostEquals(new Color(0, 1, 1), EPS));
        assertTrue(cyan.almostEquals(Color.CYAN, EPS));
    }

    @Test
    @DisplayName("mul() - atténue uniformément toutes les composantes")
    void mul_scalar_scalesAllComponents() {
        Color couleur = new Color(1, 0.8, 0.6);

        Color attenuee = couleur.mul(0.5);

        assertTrue(attenuee.almostEquals(new Color(0.5, 0.4, 0.3), EPS));
    }

    @Test
    @DisplayName("mul(0) - retourne noir")
    void mul_zero_returnsBlack() {
        Color couleur = new Color(1, 1, 1);

        Color result = couleur.mul(0);

        assertTrue(result.almostEquals(Color.BLACK, EPS));
    }

    @Test
    @DisplayName("div() - divise toutes les composantes")
    void div_scalar_dividesAllComponents() {
        Color couleur = new Color(1, 0.8, 0.6);

        Color result = couleur.div(2);

        assertTrue(result.almostEquals(new Color(0.5, 0.4, 0.3), EPS));
    }

    @Test
    @DisplayName("schur() - modulation composante par composante")
    void schur_twoColors_modulatesComponents() {
        Color objetRouge = new Color(1, 0, 0);
        Color lumiereVerte = new Color(0, 1, 0);

        Color resultat = objetRouge.schur(lumiereVerte);

        assertTrue(resultat.almostEquals(Color.BLACK, EPS),
                "Un objet rouge sous lumière verte devrait apparaître noir");
    }

    @Test
    @DisplayName("schur() - filtre les couleurs")
    void schur_filtering_attenuatesColors() {
        Color couleur = new Color(1, 0.8, 0.6);
        Color filtre = new Color(0.5, 0.5, 0.5);  // Gris 50%

        Color filtree = couleur.schur(filtre);

        assertTrue(filtree.almostEquals(new Color(0.5, 0.4, 0.3), EPS),
                "Un filtre gris devrait atténuer toutes les composantes");
    }

    @Test
    @DisplayName("toRGB() - convertit correctement vers RGB 24-bit")
    void toRGB_validComponents_convertsCorrectly() {
        // Couleurs de base
        assertEquals(0xFF0000, new Color(1, 0, 0).toRGB(), "Rouge");
        assertEquals(0x00FF00, new Color(0, 1, 0).toRGB(), "Vert");
        assertEquals(0x0000FF, new Color(0, 0, 1).toRGB(), "Bleu");
        assertEquals(0xFFFFFF, new Color(1, 1, 1).toRGB(), "Blanc");
        assertEquals(0x000000, new Color(0, 0, 0).toRGB(), "Noir");

        int tutu = 0x7F7F7F; // 8355711
        // Gris 50%
        assertEquals(0x7F7F7F, new Color(0.5, 0.5, 0.5).toRGB(), "Gris");
    }

    @Test
    @DisplayName("toRGB() - clampe les valeurs > 1.0")
    void toRGB_overexposedValues_clampsToMax() {
        Color surexposee = new Color(1.5, 2.0, 3.0);

        int rgb = surexposee.toRGB();

        assertEquals(0xFFFFFF, rgb,
                "Les valeurs > 1.0 devraient être clampées à 255 (blanc)");
    }

    @Test
    @DisplayName("toRGB() - clampe les valeurs négatives")
    void toRGB_negativeValues_clampsToZero() {
        Color negative = new Color(-0.5, -1.0, -2.0);

        int rgb = negative.toRGB();

        assertEquals(0x000000, rgb,
                "Les valeurs négatives devraient être clampées à 0 (noir)");
    }

    @Test
    @DisplayName("toRGB() - gère le clamping mixte")
    void toRGB_mixedClamping_clampsIndependently() {
        Color mixed = new Color(1.5, 0.5, -0.2);

        int rgb = mixed.toRGB();

        // Rouge clamped à 255, vert = 127.5 ≈ 128, bleu clamped à 0
        assertEquals(0xFF7F00, rgb);
    }

    @Test
    @DisplayName("fromRGB() - crée une couleur depuis des composantes entières")
    void fromRGB_intComponents_createsCorrectColor() {
        Color rouge = Color.fromRGB(255, 0, 0);

        assertTrue(rouge.almostEquals(Color.RED, EPS));
    }

    @Test
    @DisplayName("fromRGB() - round-trip avec toRGB()")
    void fromRGB_roundTrip_preservesColor() {
        Color original = new Color(0.5, 0.7, 0.3);

        int rgb = original.toRGB();
        Color reconstructed = Color.fromRGB(rgb);

        // Devrait être très proche (petite perte de précision due à l'arrondi)
        assertTrue(reconstructed.almostEquals(original, 0.01),
                "Un round-trip RGB devrait préserver la couleur");
    }

    @Test
    @DisplayName("Constantes prédéfinies sont correctes")
    void predefinedColors_haveCorrectValues() {
        assertEquals(0x000000, Color.BLACK.toRGB());
        assertEquals(0xFFFFFF, Color.WHITE.toRGB());
        assertEquals(0xFF0000, Color.RED.toRGB());
        assertEquals(0x00FF00, Color.GREEN.toRGB());
        assertEquals(0x0000FF, Color.BLUE.toRGB());
        assertEquals(0xFFFF00, Color.YELLOW.toRGB());
        assertEquals(0x00FFFF, Color.CYAN.toRGB());
        assertEquals(0xFF00FF, Color.MAGENTA.toRGB());
    }

    @Test
    @DisplayName("equals() - compare strictement les composantes")
    void equals_sameComponents_returnsTrue() {
        Color c1 = new Color(0.5, 0.6, 0.7);
        Color c2 = new Color(0.5, 0.6, 0.7);

        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    @DisplayName("toString() - contient les composantes")
    void toString_containsComponents() {
        String str = new Color(0.3, 0.5, 0.7).toString();

        assertTrue(str.contains("Color"));
        assertTrue(str.contains("0.3"));
        assertTrue(str.contains("0.5"));
        assertTrue(str.contains("0.7"));
    }
}