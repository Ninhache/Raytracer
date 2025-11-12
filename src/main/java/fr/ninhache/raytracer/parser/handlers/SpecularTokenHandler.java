package fr.ninhache.raytracer.parser.handlers;

import fr.ninhache.raytracer.math.Color;
import fr.ninhache.raytracer.parser.ParsingContext;
import fr.ninhache.raytracer.parser.TokenHandler;
import fr.ninhache.raytracer.parser.TokenProcessor;
import fr.ninhache.raytracer.scene.SceneBuilder;
import fr.ninhache.raytracer.scene.exception.ParseException;

/**
 * Handler pour la commande {@code specular}.
 *
 * <p>Format : {@code specular r g b}
 *
 * <p>Exemple : {@code specular 0.5 0.5 0.5}
 */
@TokenHandler("specular")
public class SpecularTokenHandler implements TokenProcessor {

    @Override
    public void process(String[] tokens, ParsingContext context) throws ParseException {
        if (tokens.length != 4) {
            throw new ParseException(
                    "specular nécessite 3 paramètres : r g b",
                    context.getCurrentLineNumber(),
                    String.join(" ", tokens)
            );
        }

        try {
            double r = Double.parseDouble(tokens[1]);
            double g = Double.parseDouble(tokens[2]);
            double b = Double.parseDouble(tokens[3]);

            Color specular = new Color(r, g, b);
            context.getSceneBuilder().setSpecular(specular);

        } catch (NumberFormatException e) {
            throw new ParseException(
                    "Les composantes de couleur doivent être des nombres",
                    context.getCurrentLineNumber(),
                    String.join(" ", tokens),
                    e
            );
        }
    }
}
