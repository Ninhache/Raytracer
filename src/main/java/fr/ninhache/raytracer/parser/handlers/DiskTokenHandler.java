package fr.ninhache.raytracer.parser.handlers;

import fr.ninhache.raytracer.geometry.shape.Disk;
import fr.ninhache.raytracer.math.Point;
import fr.ninhache.raytracer.math.Vector;
import fr.ninhache.raytracer.parser.ParsingContext;
import fr.ninhache.raytracer.parser.TokenHandler;
import fr.ninhache.raytracer.parser.TokenProcessor;
import fr.ninhache.raytracer.scene.exception.ParseException;

@TokenHandler("disk")
public class DiskTokenHandler implements TokenProcessor {

    @Override
    public void process(String[] tokens, ParsingContext context) throws ParseException {
        // disk cx cy cz nx ny nz radius  => 1 + 3 + 3 + 1 = 8 tokens
        if (tokens.length != 8) {
            throw new ParseException("disk nécessite 7 paramètres : cx cy cz nx ny nz radius");
        }

        try {
            double cx = Double.parseDouble(tokens[1]);
            double cy = Double.parseDouble(tokens[2]);
            double cz = Double.parseDouble(tokens[3]);

            double nx = Double.parseDouble(tokens[4]);
            double ny = Double.parseDouble(tokens[5]);
            double nz = Double.parseDouble(tokens[6]);

            double radius = Double.parseDouble(tokens[7]);

            Point center = new Point(cx, cy, cz);
            Vector normal = new Vector(nx, ny, nz);

            Disk disk = new Disk(center, normal, radius);

            context.addShape(disk);
        } catch (NumberFormatException e) {
            throw new ParseException("Les paramètres de disk doivent être des nombres");
        }
    }
}
