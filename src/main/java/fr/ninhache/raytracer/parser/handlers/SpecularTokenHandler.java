package fr.ninhache.raytracer.parser.custom;

import fr.ninhache.raytracer.math.Color;
import fr.ninhache.raytracer.parser.TokenHandler;
import fr.ninhache.raytracer.scene.SceneBuilder;
import fr.ninhache.raytracer.scene.exception.ParseException;

/**
 * Handler pour la commande {@code specular}.
 *
 * <p>Format : {@code specular r g b}
 *
 * <p>Exemple : {@code specular 0.5 0.5 0.5}
 */
public class SpecularTokenHandler implements TokenHandler {

    @Override
    public void handle(String[] tokens, int lineNumber, SceneBuilder builder)
            throws ParseException {

        if (tokens.length != 4) {
            throw new ParseException(
                    "specular nécessite 3 paramètres : r g b",
                    lineNumber,
                    String.join(" ", tokens)
            );
        }

        try {
            double r = Double.parseDouble(tokens[1]);
            double g = Double.parseDouble(tokens[2]);
            double b = Double.parseDouble(tokens[3]);

            Color specular = new Color(r, g, b);
            builder.setSpecular(specular);

        } catch (NumberFormatException e) {
            throw new ParseException(
                    "Les composantes de couleur doivent être des nombres",
                    lineNumber,
                    String.join(" ", tokens),
                    e
            );
        }
    }

    @Override
    public String getTokenName() {
        return "specular";
    }
}
