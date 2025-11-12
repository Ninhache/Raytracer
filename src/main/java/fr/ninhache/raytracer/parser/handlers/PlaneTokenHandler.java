package fr.ninhache.raytracer.parser.handlers;

import fr.ninhache.raytracer.geometry.Plane;
import fr.ninhache.raytracer.math.Point;
import fr.ninhache.raytracer.math.Vector;
import fr.ninhache.raytracer.parser.ParsingContext;
import fr.ninhache.raytracer.parser.TokenHandler;
import fr.ninhache.raytracer.parser.TokenProcessor;
import fr.ninhache.raytracer.scene.exception.ParseException;

/**
 * Handler pour la commande {@code plane}.
 *
 * <p>Format : {@code plane x y z nx ny nz}
 *
 * <p>Paramètres :
 * <ul>
 *   <li>(x, y, z) : un point appartenant au plan</li>
 *   <li>(nx, ny, nz) : normale au plan</li>
 * </ul>
 *
 * <p>Exemple : {@code plane 0 -1 0 0 1 0} (sol au niveau y=-1)
 */
@TokenHandler("plane")
public class PlaneTokenHandler implements TokenProcessor {
    @Override
    public void process(String[] tokens, ParsingContext context) throws ParseException {
        if (tokens.length != 7) {
            throw new ParseException(
                    "plane nécessite 6 paramètres : x y z nx ny nz",
                    context.getCurrentLineNumber(),
                    String.join(" ", tokens)
            );
        }

        try {
            // Point du plan
            double x = Double.parseDouble(tokens[1]);
            double y = Double.parseDouble(tokens[2]);
            double z = Double.parseDouble(tokens[3]);
            Point point = new Point(x, y, z);

            // Normale du plan
            double nx = Double.parseDouble(tokens[4]);
            double ny = Double.parseDouble(tokens[5]);
            double nz = Double.parseDouble(tokens[6]);
            Vector normal = new Vector(nx, ny, nz);

            // Création du plan
            Plane plane = new Plane(point, normal);

            context.getSceneBuilder().addShape(plane);

        } catch (NumberFormatException e) {
            throw new ParseException(
                    "Les paramètres de plane doivent être des nombres",
                    context.getCurrentLineNumber(),
                    String.join(" ", tokens),
                    e
            );
        } catch (IllegalArgumentException e) {
            throw new ParseException(
                    "Paramètres de plan invalides : " + e.getMessage(),
                    context.getCurrentLineNumber(),
                    String.join(" ", tokens),
                    e
            );
        }
    }
}