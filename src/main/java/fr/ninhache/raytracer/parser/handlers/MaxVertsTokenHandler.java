package fr.ninhache.raytracer.parser.handlers;

import fr.ninhache.raytracer.parser.ParsingContext;
import fr.ninhache.raytracer.parser.TokenHandler;
import fr.ninhache.raytracer.parser.TokenProcessor;
import fr.ninhache.raytracer.scene.SceneBuilder;
import fr.ninhache.raytracer.scene.exception.ParseException;

@TokenHandler("maxverts")
public class MaxVertsTokenHandler implements TokenProcessor {

    @Override
    public void process(String[] tokens, ParsingContext context) throws ParseException {
        // Format : maxverts nombre
        if (tokens.length != 2) {
            throw new ParseException("maxverts nécessite 1 paramètre", context.getCurrentLineNumber(), String.join(" ", tokens));
        }
        int max = Integer.parseInt(tokens[1]);

        context.setMaxVertices(max);
    }
}
