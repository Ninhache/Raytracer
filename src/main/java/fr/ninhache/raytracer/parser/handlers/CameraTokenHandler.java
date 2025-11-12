package fr.ninhache.raytracer.parser.handlers;

import fr.ninhache.raytracer.parser.ParsingContext;
import fr.ninhache.raytracer.parser.TokenHandler;
import fr.ninhache.raytracer.parser.TokenProcessor;
import fr.ninhache.raytracer.scene.exception.ParseException;
import fr.ninhache.raytracer.math.Point;
import fr.ninhache.raytracer.math.Vector;
import fr.ninhache.raytracer.scene.Camera;

/**
 * Handler pour la commande {@code camera}.
 *
 * <p>Format : {@code camera x y z u v w m n o fov}
 *
 * <p>Paramètres :
 * <ul>
 *   <li>(x, y, z) : position de l'œil (lookFrom)</li>
 *   <li>(u, v, w) : point visé (lookAt)</li>
 *   <li>(m, n, o) : direction vers le haut (up)</li>
 *   <li>fov : angle de vue en degrés</li>
 * </ul>
 *
 * <p>Exemple : {@code camera 0 0 4 0 0 0 0 1 0 45}
 */
@TokenHandler("camera")
public class CameraTokenHandler implements TokenProcessor {
    @Override
    public void process(String[] tokens, ParsingContext context) throws ParseException {
        if (tokens.length != 11) {
            throw new ParseException("camera nécessite 10 paramètres : x y z u v w m n o fov");
        }

        try {
            // Position de l'oeil (lookFrom)
            double eyeX = Double.parseDouble(tokens[1]);
            double eyeY = Double.parseDouble(tokens[2]);
            double eyeZ = Double.parseDouble(tokens[3]);
            Point lookFrom = new Point(eyeX, eyeY, eyeZ);

            // Point visé (lookAt)
            double targetX = Double.parseDouble(tokens[4]);
            double targetY = Double.parseDouble(tokens[5]);
            double targetZ = Double.parseDouble(tokens[6]);
            Point lookAt = new Point(targetX, targetY, targetZ);

            // Direction vers le haut (up)
            double upX = Double.parseDouble(tokens[7]);
            double upY = Double.parseDouble(tokens[8]);
            double upZ = Double.parseDouble(tokens[9]);
            Vector up = new Vector(upX, upY, upZ);

            // Angle de vue (FOV)
            double fov = Double.parseDouble(tokens[10]);

            // Création de la caméra
            Camera camera = new Camera(lookFrom, lookAt, up, fov);
            context.setCamera(camera);

        } catch (NumberFormatException e) {
            throw new ParseException("Les paramètres de camera doivent être des nombres");
        } catch (IllegalArgumentException e) {
            throw new ParseException("Paramètres de caméra invalides : " + e.getMessage());
        }
    }
}