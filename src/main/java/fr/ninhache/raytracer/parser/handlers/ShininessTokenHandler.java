package fr.ninhache.raytracer.parser.handlers;

import fr.ninhache.raytracer.parser.ParsingContext;
import fr.ninhache.raytracer.parser.TokenHandler;
import fr.ninhache.raytracer.parser.TokenProcessor;
import fr.ninhache.raytracer.scene.exception.ParseException;

@TokenHandler("shininess")
public class ShininessTokenHandler implements TokenProcessor {
    @Override
    public void process(String[] tokens, ParsingContext context) throws ParseException {
        if (tokens.length != 2) {
            throw new ParseException("shininess nécessite 1 paramètre", context.getCurrentLineNumber(), context.getCurrentLine());
        }

        try {
            double intensity = Double.parseDouble(tokens[1]);

            context.setCurrentShininess(intensity);
        } catch (NumberFormatException e) {
            throw new ParseException("L'intensité doit être un nombre");
        }
    }
}
