package fr.ninhache.raytracer.render;

import java.awt.image.BufferedImage;

/**
 * Buffer d'image simple basé sur un tableau d'entiers ARGB.
 *
 * Thread-safe tant que chaque thread écrit dans des pixels distincts.
 */
public final class ImageBuffer {
    private final int width;
    private final int height;
    private final int[] pixels;

    public ImageBuffer(int width, int height) {
        this.width = width;
        this.height = height;
        this.pixels = new int[width * height];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /** Écrit un pixel (aucun check de bounds pour la perf) */
    public void setPixel(int x, int y, int argb) {
        pixels[y * width + x] = argb;
    }

    /** Convertit le buffer en BufferedImage AWT. */
    public BufferedImage toBufferedImage() {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        img.setRGB(0, 0, width, height, pixels, 0, width);
        return img;
    }
}
