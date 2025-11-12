package fr.ninhache.raytracer.parser.custom;

import fr.ninhache.raytracer.lighting.PointLight;
import fr.ninhache.raytracer.math.Color;
import fr.ninhache.raytracer.math.Point;
import fr.ninhache.raytracer.parser.TokenHandler;
import fr.ninhache.raytracer.scene.SceneBuilder;
import fr.ninhache.raytracer.scene.exception.ParseException;

/**
 * Handler pour la commande {@code point} (lumière ponctuelle).
 *
 * <p>Format : {@code point x y z r g b}
 *
 * <p>Paramètres :
 * <ul>
 *   <li>(x, y, z) : position de la lumière</li>
 *   <li>(r, g, b) : couleur/intensité de la lumière</li>
 * </ul>
 *
 * <p>Exemple : {@code point 4 3 2 1 1 1}
 */
public class PointLightTokenHandler implements TokenHandler {

    @Override
    public void handle(String[] tokens, int lineNumber, SceneBuilder builder)
            throws ParseException {

        if (tokens.length != 7) {
            throw new ParseException(
                    "point nécessite 6 paramètres : x y z r g b",
                    lineNumber,
                    String.join(" ", tokens)
            );
        }

        try {
            // Position de la lumière
            double x = Double.parseDouble(tokens[1]);
            double y = Double.parseDouble(tokens[2]);
            double z = Double.parseDouble(tokens[3]);
            Point position = new Point(x, y, z);

            // Couleur de la lumière
            double r = Double.parseDouble(tokens[4]);
            double g = Double.parseDouble(tokens[5]);
            double b = Double.parseDouble(tokens[6]);
            Color color = new Color(r, g, b);

            // Création de la lumière ponctuelle
            PointLight light = new PointLight(position, color);
            builder.addLight(light);

        } catch (NumberFormatException e) {
            throw new ParseException(
                    "Les paramètres de point doivent être des nombres",
                    lineNumber,
                    String.join(" ", tokens),
                    e
            );
        } catch (IllegalArgumentException e) {
            throw new ParseException(
                    "Paramètres de lumière ponctuelle invalides : " + e.getMessage(),
                    lineNumber,
                    String.join(" ", tokens),
                    e
            );
        }
    }

    @Override
    public String getTokenName() {
        return "point";
    }
}