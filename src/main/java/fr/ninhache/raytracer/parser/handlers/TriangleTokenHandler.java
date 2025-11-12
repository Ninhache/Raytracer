package fr.ninhache.raytracer.parser.handlers;

import fr.ninhache.raytracer.geometry.Triangle;
import fr.ninhache.raytracer.math.Point;
import fr.ninhache.raytracer.parser.ParsingContext;
import fr.ninhache.raytracer.parser.TokenHandler;
import fr.ninhache.raytracer.parser.TokenProcessor;
import fr.ninhache.raytracer.scene.SceneBuilder;
import fr.ninhache.raytracer.scene.exception.ParseException;

/**
 * Handler pour la commande {@code tri} (triangle).
 *
 * <p>Format : {@code tri index1 index2 index3}
 *
 * <p>Exemple : {@code tri 0 1 2}
 */
@TokenHandler("tri")
public class TriangleTokenHandler implements TokenProcessor {


    @Override
    public void process(String[] tokens, ParsingContext context) throws ParseException {
        if (tokens.length != 4) {
            throw new ParseException(
                    "tri nécessite 3 paramètres : index1 index2 index3",
                    context.getCurrentLineNumber(),
                    String.join(" ", tokens)
            );
        }

        try {
            int i1 = Integer.parseInt(tokens[1]);
            int i2 = Integer.parseInt(tokens[2]);
            int i3 = Integer.parseInt(tokens[3]);

            Point p1 = context.getVertex(i1);
            Point p2 = context.getVertex(i2);
            Point p3 = context.getVertex(i3);

            Triangle triangle = new Triangle(p1, p2, p3);
            context.addShape(triangle);

        } catch (NumberFormatException e) {
            throw new ParseException(
                    "Les indices de triangle doivent être des entiers",
                    context.getCurrentLineNumber(),
                    String.join(" ", tokens),
                    e
            );
        }
    }
}