package fr.ninhache.raytracer.parser.handlers;

import fr.ninhache.raytracer.parser.ParsingContext;
import fr.ninhache.raytracer.parser.TokenHandler;
import fr.ninhache.raytracer.parser.TokenProcessor;
import fr.ninhache.raytracer.scene.SceneBuilder;
import fr.ninhache.raytracer.scene.exception.ParseException;

/**
 * Handler pour la commande {@code output}.
 *
 * <p>Format : {@code output nom_fichier.png}
 *
 * <p>Exemple : {@code output scene1.png}
 */
@TokenHandler("output")
public class OutputTokenHandler implements TokenProcessor {
    @Override
    public void process(String[] tokens, ParsingContext context) throws ParseException {
        if (tokens.length != 2) {
            throw new ParseException("output nécessite 1 paramètre: nom_fichier");
        }

        context.setOutputFilename(tokens[1]);
    }

}