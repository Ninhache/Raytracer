package fr.ninhache.raytracer.parser.handlers;

import fr.ninhache.raytracer.math.Color;
import fr.ninhache.raytracer.parser.ParsingContext;
import fr.ninhache.raytracer.parser.TokenHandler;
import fr.ninhache.raytracer.parser.TokenProcessor;
import fr.ninhache.raytracer.scene.exception.ParseException;

/**
 * Handler pour la commande {@code ambient}.
 *
 * <p>Format : {@code ambient r g b}
 *
 * <p>Exemple : {@code ambient 0.1 0.1 0.1}
 */
@TokenHandler("ambient")
public class AmbientTokenHandler implements TokenProcessor {
    @Override
    public void process(String[] tokens, ParsingContext context) throws ParseException {
        if (tokens.length != 4) {
            throw new ParseException("ambient nécessite 3 paramètres : r g b");
        }

        try {
            double r = Double.parseDouble(tokens[1]);
            double g = Double.parseDouble(tokens[2]);
            double b = Double.parseDouble(tokens[3]);

            Color ambient = new Color(r, g, b);
            context.setCurrentAmbient(ambient);
        } catch (NumberFormatException e) {
            throw new ParseException("Les composantes de couleur doivent être des nombres");
        }
    }
}