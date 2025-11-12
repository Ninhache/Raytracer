package fr.ninhache.raytracer.parser.handlers;

import fr.ninhache.raytracer.math.Point;
import fr.ninhache.raytracer.parser.ParsingContext;
import fr.ninhache.raytracer.parser.TokenHandler;
import fr.ninhache.raytracer.parser.TokenProcessor;
import fr.ninhache.raytracer.scene.SceneBuilder;
import fr.ninhache.raytracer.scene.exception.ParseException;

/**
 * Handler pour la commande {@code vertex}.
 *
 * <p>Format : {@code vertex x y z}
 *
 * <p>Exemple : {@code vertex -1 -1 0}
 */
@TokenHandler("vertex")
public class VertexTokenHandler implements TokenProcessor {


    @Override
    public void process(String[] tokens, ParsingContext context) throws ParseException {
        if (tokens.length != 4) {
            throw new ParseException(
                    "vertex nécessite 3 paramètres : x y z",
                    context.getCurrentLineNumber(),
                    String.join(" ", tokens)
            );
        }

        try {
            double x = Double.parseDouble(tokens[1]);
            double y = Double.parseDouble(tokens[2]);
            double z = Double.parseDouble(tokens[3]);

            Point vertex = new Point(x, y, z);
            context.addVertex(vertex);

        } catch (NumberFormatException e) {
            throw new ParseException(
                    "Les coordonnées du vertex doivent être des nombres",
                    context.getCurrentLineNumber(),
                    String.join(" ", tokens),
                    e
            );
        }
    }
}
