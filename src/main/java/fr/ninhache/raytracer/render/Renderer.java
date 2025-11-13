package fr.ninhache.raytracer.render;

import fr.ninhache.raytracer.math.Color;
import fr.ninhache.raytracer.scene.Scene;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/** Effectue le rendu image en bouclant sur tous les pixels. */
public final class Renderer {
    private final RayTracer rayTracer = new RayTracer();

    public BufferedImage render(Scene scene) {
        int w = scene.getWidth();
        int h = scene.getHeight();
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                Color c = rayTracer.getPixelColor(scene, i, j);
                img.setRGB(i, j, c.toRGB());
            }
        }
        return img;
    }

    public void writeToFile(String outputPath, BufferedImage image) throws IOException {
        File outputFile = new File(outputPath);

        // Création du dossier parent uniquement s’il existe
        File parent = outputFile.getParentFile();
        if (parent != null && !parent.exists()) {
            if (!parent.mkdirs()) {
                System.err.println("Warning: impossible de créer le dossier de sortie: " + parent);
            }
        }

        ImageIO.write(image, "png", outputFile);
        System.out.println("Image écrite dans: " + outputFile.getAbsolutePath());
    }

    private static String extension(String filename) {
        int i = filename.lastIndexOf('.');
        return (i >= 0 && i < filename.length()-1) ? filename.substring(i+1) : "png";
    }
}
