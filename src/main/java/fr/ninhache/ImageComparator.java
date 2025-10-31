package fr.ninhache;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageComparator {

    public static int countDifferentPixel(BufferedImage image1, BufferedImage image2) {
        if (image1.getWidth() != image2.getWidth() ||
                image1.getHeight() != image2.getHeight()) {
            throw new IllegalArgumentException("Images must have same dimensions");
        }

        int w = image1.getWidth();
        int h = image1.getHeight();

        int differencesNumber = 0;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int argb1 = image1.getRGB(x, y);
                int argb2 = image2.getRGB(x, y);

                if (argb1 != argb2) {
                    differencesNumber++;
                }
            }
        }

        return differencesNumber;
    }

    public static void generateImageFromDifferentPixel(BufferedImage image1, BufferedImage image2) {
        generateImageFromDifferentPixel(image1, image2, "./generatedImage.png");
    }
    public static void generateImageFromDifferentPixel(BufferedImage image1, BufferedImage image2, String path) {
        if (image1.getWidth() != image2.getWidth() ||
                image1.getHeight() != image2.getHeight()) {
            throw new IllegalArgumentException("Images must have same dimensions");
        }

        int w = image1.getWidth();
        int h = image1.getHeight();

        BufferedImage diffImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int argb1 = image1.getRGB(x, y);
                int argb2 = image2.getRGB(x, y);

                if (argb1 != argb2) {
                    diffImage.setRGB(x, y, new Color(255, 0, 0, 255).getRGB());
                } else {
                    diffImage.setRGB(x, y, new Color(0, 0, 0, 255).getRGB());
                }
            }
        }


        try {
            ImageIO.write(diffImage, "png", new File(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
