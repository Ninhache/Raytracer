package fr.ninhache.raytracer.parser.handlers;

import fr.ninhache.raytracer.geometry.shape.RegularPolygon;
import fr.ninhache.raytracer.math.Point;
import fr.ninhache.raytracer.math.Vector;
import fr.ninhache.raytracer.parser.ParsingContext;
import fr.ninhache.raytracer.parser.TokenHandler;
import fr.ninhache.raytracer.parser.TokenProcessor;
import fr.ninhache.raytracer.scene.exception.ParseException;

@TokenHandler("regpoly")
public class RegularPolygonTokenHandler implements TokenProcessor {

    @Override
    public void process(String[] tokens, ParsingContext context) throws ParseException {
        // regpoly cx cy cz radius sides nx ny nz  => 1 + 3 + 1 + 1 + 3 = 9 tokens
        if (tokens.length != 9) {
            throw new ParseException(
                    ParseException.NUMBER_OF_ARGS + " pour regpoly (attendu: 8 paramètres)",
                    context.getCurrentLineNumber(),
                    context.getCurrentLine()
            );
        }

        try {
            double cx = Double.parseDouble(tokens[1]);
            double cy = Double.parseDouble(tokens[2]);
            double cz = Double.parseDouble(tokens[3]);

            double radius = Double.parseDouble(tokens[4]);
            int sides = Integer.parseInt(tokens[5]);

            double nx = Double.parseDouble(tokens[6]);
            double ny = Double.parseDouble(tokens[7]);
            double nz = Double.parseDouble(tokens[8]);

            Point center = new Point(cx, cy, cz);
            Vector normal = new Vector(nx, ny, nz);

            RegularPolygon poly = new RegularPolygon(center, radius, sides, normal);

            context.addShape(poly);

        } catch (NumberFormatException e) {
            throw new ParseException(
                    "Les paramètres de regpoly doivent être des nombres (cx cy cz radius sides nx ny nz)",
                    context.getCurrentLineNumber(),
                    context.getCurrentLine(),
                    e
            );
        } catch (IllegalArgumentException e) {
            throw new ParseException(
                    "Paramètres de regpoly invalides : " + e.getMessage(),
                    context.getCurrentLineNumber(),
                    context.getCurrentLine(),
                    e
            );
        }
    }
}
