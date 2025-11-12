package fr.ninhache.raytracer.parser.custom;

import fr.ninhache.raytracer.math.Point;
import fr.ninhache.raytracer.parser.TokenHandler;
import fr.ninhache.raytracer.scene.SceneBuilder;
import fr.ninhache.raytracer.scene.exception.ParseException;

/**
 * Handler pour la commande {@code vertex}.
 *
 * <p>Format : {@code vertex x y z}
 *
 * <p>Exemple : {@code vertex -1 -1 0}
 */
public class VertexTokenHandler implements TokenHandler {

    @Override
    public void handle(String[] tokens, int lineNumber, SceneBuilder builder)
            throws ParseException {

        if (tokens.length != 4) {
            throw new ParseException(
                    "vertex nécessite 3 paramètres : x y z",
                    lineNumber,
                    String.join(" ", tokens)
            );
        }

        try {
            double x = Double.parseDouble(tokens[1]);
            double y = Double.parseDouble(tokens[2]);
            double z = Double.parseDouble(tokens[3]);

            Point vertex = new Point(x, y, z);
            builder.addVertex(vertex);

        } catch (NumberFormatException e) {
            throw new ParseException(
                    "Les coordonnées du vertex doivent être des nombres",
                    lineNumber,
                    String.join(" ", tokens),
                    e
            );
        }
    }

    @Override
    public String getTokenName() {
        return "vertex";
    }
}
