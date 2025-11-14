package fr.ninhache.raytracer.parser.handlers;


import fr.ninhache.raytracer.parser.ParsingContext;
import fr.ninhache.raytracer.parser.TokenHandler;
import fr.ninhache.raytracer.parser.TokenProcessor;
import fr.ninhache.raytracer.scene.exception.ParseException;

@TokenHandler("maxdepth")
public class MaxDepthTokenHandler implements TokenProcessor {

    @Override
    public void process(String[] tokens, ParsingContext context) throws ParseException {
        if (tokens.length != 2) {
            throw new ParseException("maxdepth nécessite 1 paramètre : entier >= 1");
        }
        try {
            int depth = Integer.parseInt(tokens[1]);
            context.setMaxDepth(depth);
        } catch (NumberFormatException e) {
            throw new ParseException("maxdepth doit être un entier");
        }
    }
}
