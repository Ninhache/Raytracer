package fr.ninhache.raytracer.parser.custom;

import fr.ninhache.raytracer.lighting.DirectionalLight;
import fr.ninhache.raytracer.math.Color;
import fr.ninhache.raytracer.math.Vector;
import fr.ninhache.raytracer.parser.TokenHandler;
import fr.ninhache.raytracer.scene.SceneBuilder;
import fr.ninhache.raytracer.scene.exception.ParseException;

/**
 * Handler pour la commande {@code directional} (lumière directionnelle).
 *
 * <p>Format : {@code directional x y z r g b}
 *
 * <p>Paramètres :
 * <ul>
 *   <li>(x, y, z) : direction de la lumière (vecteur)</li>
 *   <li>(r, g, b) : couleur/intensité de la lumière</li>
 * </ul>
 *
 * <p>Exemple : {@code directional 0 -1 0 0.8 0.8 0.8}
 */
public class DirectionalLightTokenHandler implements TokenHandler {

    @Override
    public void handle(String[] tokens, int lineNumber, SceneBuilder builder)
            throws ParseException {

        if (tokens.length != 7) {
            throw new ParseException(
                    "directional nécessite 6 paramètres : x y z r g b",
                    lineNumber,
                    String.join(" ", tokens)
            );
        }

        try {
            // Direction de la lumière
            double x = Double.parseDouble(tokens[1]);
            double y = Double.parseDouble(tokens[2]);
            double z = Double.parseDouble(tokens[3]);
            Vector direction = new Vector(x, y, z);

            // Couleur de la lumière
            double r = Double.parseDouble(tokens[4]);
            double g = Double.parseDouble(tokens[5]);
            double b = Double.parseDouble(tokens[6]);
            Color color = new Color(r, g, b);

            // Création de la lumière directionnelle
            DirectionalLight light = new DirectionalLight(direction, color);
            builder.addLight(light);

        } catch (NumberFormatException e) {
            throw new ParseException(
                    "Les paramètres de directional doivent être des nombres",
                    lineNumber,
                    String.join(" ", tokens),
                    e
            );
        } catch (IllegalArgumentException e) {
            throw new ParseException(
                    "Paramètres de lumière directionnelle invalides : " + e.getMessage(),
                    lineNumber,
                    String.join(" ", tokens),
                    e
            );
        }
    }

    @Override
    public String getTokenName() {
        return "directional";
    }
}