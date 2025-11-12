package fr.ninhache.raytracer.parser.handlers;

import fr.ninhache.raytracer.parser.TokenHandler;
import fr.ninhache.raytracer.scene.exception.ParseException;
import fr.ninhache.raytracer.parser.*;

/**
 * Handler pour la commande {@code size}.
 *
 * <p>Format : {@code size largeur hauteur}
 *
 * <p>Exemple : {@code size 640 480}
 */
@TokenHandler("size")
public class SizeTokenProcessor implements TokenProcessor {
    @Override
    public void process(String[] tokens, ParsingContext context) throws ParseException {
        if (tokens.length != 3) {
            throw new ParseException("size nécessite 2 paramètres: largeur hauteur");
        }
        int width = Integer.parseInt(tokens[1]);
        int height = Integer.parseInt(tokens[2]);
        context.getSceneBuilder().setSize(width, height);
    }
}
