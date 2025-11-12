package fr.ninhache.raytracer.parser.custom;

import fr.ninhache.raytracer.geometry.Triangle;
import fr.ninhache.raytracer.math.Point;
import fr.ninhache.raytracer.parser.TokenHandler;
import fr.ninhache.raytracer.scene.SceneBuilder;
import fr.ninhache.raytracer.scene.exception.ParseException;

/**
 * Handler pour la commande {@code tri} (triangle).
 *
 * <p>Format : {@code tri index1 index2 index3}
 *
 * <p>Exemple : {@code tri 0 1 2}
 */
public class TriangleTokenHandler implements TokenHandler {

    @Override
    public void handle(String[] tokens, int lineNumber, SceneBuilder builder)
            throws ParseException {

        if (tokens.length != 4) {
            throw new ParseException(
                    "tri nécessite 3 paramètres : index1 index2 index3",
                    lineNumber,
                    String.join(" ", tokens)
            );
        }

        try {
            int i1 = Integer.parseInt(tokens[1]);
            int i2 = Integer.parseInt(tokens[2]);
            int i3 = Integer.parseInt(tokens[3]);

            Point p1 = builder.getVertex(i1);
            Point p2 = builder.getVertex(i2);
            Point p3 = builder.getVertex(i3);

            Triangle triangle = new Triangle(p1, p2, p3);
            builder.addShape(triangle);

        } catch (NumberFormatException e) {
            throw new ParseException(
                    "Les indices de triangle doivent être des entiers",
                    lineNumber,
                    String.join(" ", tokens),
                    e
            );
        }
    }

    @Override
    public String getTokenName() {
        return "tri";
    }
}