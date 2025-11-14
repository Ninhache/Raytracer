package fr.ninhache.raytracer.parser.handlers;

import fr.ninhache.raytracer.geometry.shape.Sphere;
import fr.ninhache.raytracer.math.Point;
import fr.ninhache.raytracer.parser.ParsingContext;
import fr.ninhache.raytracer.parser.TokenHandler;
import fr.ninhache.raytracer.parser.TokenProcessor;
import fr.ninhache.raytracer.scene.exception.ParseException;

/**
 * Handler pour la commande {@code sphere}.
 *
 * <p>Format : {@code sphere x y z rayon}
 *
 * <p>Exemple : {@code sphere 0 1 0 0.5}
 */
@TokenHandler("sphere")
public class SphereTokenHandler implements TokenProcessor {


    @Override
    public void process(String[] tokens, ParsingContext context) throws ParseException {
        if (tokens.length != 5) {
            throw new ParseException(
                    "sphere nécessite 4 paramètres : x y z rayon",
                    context.getCurrentLineNumber(),
                    String.join(" ", tokens)
            );
        }

        try {
            double x = Double.parseDouble(tokens[1]);
            double y = Double.parseDouble(tokens[2]);
            double z = Double.parseDouble(tokens[3]);
            double radius = Double.parseDouble(tokens[4]);

            if (radius <= 0) {
                throw new ParseException(
                        "Le rayon de la sphère doit être > 0",
                        context.getCurrentLineNumber(),
                        String.join(" ", tokens)
                );
            }

            Point center = new Point(x, y, z);
            Sphere sphere = new Sphere(center, radius);
            context.addShape(sphere);


        } catch (NumberFormatException e) {
            throw new ParseException(
                    "Les paramètres de sphere doivent être des nombres",
                    context.getCurrentLineNumber(),
                    String.join(" ", tokens),
                    e
            );
        }
    }
}