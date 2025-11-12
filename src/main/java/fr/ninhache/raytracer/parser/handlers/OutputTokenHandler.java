package fr.ninhache.raytracer.parser.custom;

import fr.ninhache.raytracer.parser.TokenHandler;
import fr.ninhache.raytracer.scene.SceneBuilder;
import fr.ninhache.raytracer.scene.exception.ParseException;

/**
 * Handler pour la commande {@code output}.
 *
 * <p>Format : {@code output nom_fichier.png}
 *
 * <p>Exemple : {@code output scene1.png}
 */
public class OutputTokenHandler implements TokenHandler {

    @Override
    public void handle(String[] tokens, int lineNumber, SceneBuilder builder) throws ParseException {

        if (tokens.length != 2) {
            throw new ParseException(
                "output nécessite exactement 1 paramètre : nom_fichier",
                lineNumber,
                String.join(" ", tokens)
            );
        }

        builder.setOutputFilename(tokens[1]);
    }

    @Override
    public String getTokenName() {
        return "output";
    }
}