package fr.ninhache.raytracer.parser.handlers;

import fr.ninhache.raytracer.math.Color;
import fr.ninhache.raytracer.parser.ParsingContext;
import fr.ninhache.raytracer.parser.TokenHandler;
import fr.ninhache.raytracer.parser.TokenProcessor;
import fr.ninhache.raytracer.scene.SceneBuilder;
import fr.ninhache.raytracer.scene.exception.ParseException;

/**
 * Handler pour la commande {@code diffuse}.
 *
 * <p>Format : {@code diffuse r g b}
 *
 * <p>Exemple : {@code diffuse 0.9 0 0} (rouge diffus)
 */
@TokenHandler("diffuse")
public class DiffuseTokenHandler implements TokenProcessor {
    @Override
    public void process(String[] tokens, ParsingContext context) throws ParseException {
        if (tokens.length != 4) {
            throw new ParseException("diffuse nécessite 3 paramètres : r g b");
        }

        try {
            double r = Double.parseDouble(tokens[1]);
            double g = Double.parseDouble(tokens[2]);
            double b = Double.parseDouble(tokens[3]);

            context.setCurrentDiffuse(new Color(r, g, b));
        } catch (NumberFormatException e) {
            throw new ParseException("Les composantes de couleur doivent être des nombres");
        }
    }
}