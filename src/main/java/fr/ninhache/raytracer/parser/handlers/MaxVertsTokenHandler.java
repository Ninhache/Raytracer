package fr.ninhache.raytracer.parser.custom;

import fr.ninhache.raytracer.parser.TokenHandler;
import fr.ninhache.raytracer.scene.SceneBuilder;
import fr.ninhache.raytracer.scene.exception.ParseException;

public class MaxVertsTokenHandler implements TokenHandler {
    @Override
    public void handle(String[] tokens, int lineNumber, SceneBuilder builder)
            throws ParseException {
        // Format : maxverts nombre
        if (tokens.length != 2) {
            throw new ParseException("maxverts nécessite 1 paramètre", lineNumber, String.join(" ", tokens));
        }
        int max = Integer.parseInt(tokens[1]);
        builder.setMaxVertices(max);
    }

    @Override
    public String getTokenName() {
        return "maxverts";
    }
}
