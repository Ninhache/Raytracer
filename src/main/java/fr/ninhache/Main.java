package fr.ninhache;

import fr.ninhache.raytracer.render.RenderResult;
import fr.ninhache.raytracer.render.RenderStats;
import fr.ninhache.raytracer.render.Renderer;
import fr.ninhache.raytracer.scene.Scene;
import fr.ninhache.raytracer.scene.SceneLoader;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class Main {
    private static int[][] convertTo2DWithoutUsingGetRGB(BufferedImage image) {

        final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        final int width = image.getWidth();
        final int height = image.getHeight();
        final boolean hasAlphaChannel = image.getAlphaRaster() != null;

        int[][] result = new int[height][width];
        if (hasAlphaChannel) {
            final int pixelLength = 4;
            for (int pixel = 0, row = 0, col = 0; pixel + 3 < pixels.length; pixel += pixelLength) {
                int argb = 0;
                argb += (((int) pixels[pixel] & 0xff) << 24); // alpha
                argb += ((int) pixels[pixel + 1] & 0xff); // blue
                argb += (((int) pixels[pixel + 2] & 0xff) << 8); // green
                argb += (((int) pixels[pixel + 3] & 0xff) << 16); // red
                result[row][col] = argb;
                col++;
                if (col == width) {
                    col = 0;
                    row++;
                }
            }
        } else {
            final int pixelLength = 3;
            for (int pixel = 0, row = 0, col = 0; pixel + 2 < pixels.length; pixel += pixelLength) {
                int argb = 0;
                argb += -16777216; // 255 alpha
                argb += ((int) pixels[pixel] & 0xff); // blue
                argb += (((int) pixels[pixel + 1] & 0xff) << 8); // green
                argb += (((int) pixels[pixel + 2] & 0xff) << 16); // red
                result[row][col] = argb;
                col++;
                if (col == width) {
                    col = 0;
                    row++;
                }
            }
        }

        return result;
    }

    public static void main(String[] args) {
//        if (args.length == 0) {
//            System.err.println("Usage: java -jar raytracer.jar <scene_file>");
//            System.exit(1);
//        }

        try {
//            String sceneFile = args[0];
            // temporaire
            String sceneFile = "/home/neo/imt/coo/tp3/src/main/resources/scenes/benchmark/gallery_rosaces.test";

            System.out.println("Chargement du fichier: " + sceneFile);

            SceneLoader loader = new SceneLoader();
            Scene scene = loader.load(sceneFile);

             System.out.println("Résumé temporaire");
             System.out.println("Dimensions: " + scene.getWidth() + "x" + scene.getHeight());
             System.out.println("Sortie: " + scene.getOutputFilename());
             System.out.println("Objets: " + scene.getShapeCount());
             System.out.println("Lumières: " + scene.getLightCount());


            Renderer renderer = new Renderer();
            RenderResult renderResult = renderer.render(scene);

            BufferedImage img = renderResult.image();
            RenderStats stats = renderResult.stats();

            String out = scene.getOutputFilename();
            if (out == null || out.isEmpty()) out = "output.png";
            renderer.writeToFile(out, img);
            System.out.println("Image écrite : " + out);
            System.out.println("Statistiques approximatives : " + stats);

        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void printUsage() {
        System.out.println("Usage : java -jar raytracer.jar <scene_file>");
        System.out.println();
        System.out.println("Description :");
        System.out.println("  Parse et valide un fichier de description de scène 3D.");
        System.out.println();
        System.out.println("Exemple :");
        System.out.println("  java -jar raytracer.jar scenes/scene1.txt");
        System.out.println();
        System.out.println("Le parser vérifie :");
        System.out.println("    - Format du fichier (syntaxe)");
        System.out.println("    - Contrainte : ambient + diffuse <= 1.0");
        System.out.println("    - Contrainte : somme des lumières <= 1.0");
        System.out.println("    - Indices de vertices < maxverts");
        System.out.println("    - Paramètres obligatoires (size, camera)");
    }

}
