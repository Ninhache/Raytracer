package fr.ninhache.raytracer.render;

import fr.ninhache.raytracer.math.Color;
import fr.ninhache.raytracer.render.stats.RayStats;
import fr.ninhache.raytracer.scene.Scene;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Renderer de scène vers une image 2D.
 *
 * - renderSingleThread : rendu séquentiel (simple, sûr)
 * - renderMultiThread  : rendu parallèle par lignes
 */
public final class Renderer {

    private final RayTracer rayTracer = new RayTracer();

    /**
     * Rendu "par défaut" : multi-thread si possible.
     */
    public RenderResult render(Scene scene) {
        int cores = Runtime.getRuntime().availableProcessors();

        return renderMultiThread(scene, cores);
    }

    public RenderResult renderSingleThread(Scene scene) {
        int width = scene.getWidth();
        int height = scene.getHeight();

        RayStats rayStats = new RayStats();
        long start = System.nanoTime();

        ImageBuffer buffer = new ImageBuffer(width, height);

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                Color c = rayTracer.getPixelColor(scene, i, j, rayStats);
                int rgb = c.toRGB();

                buffer.setPixel(i, j, rgb);
            }
        }

        long end = System.nanoTime();

        RenderStats stats = new RenderStats(
                width,
                height,
                1,
                false,
                start,
                end,
                rayStats.primaryRays(),
                rayStats.shadowRays(),
                rayStats.reflectionRays()
        );

        return new RenderResult(buffer.toBufferedImage(), stats);
    }


    public RenderResult renderMultiThread(Scene scene, int threadCount) {
        int width = scene.getWidth();
        int height = scene.getHeight();

        if (threadCount <= 1) {
            return renderSingleThread(scene);
        }

        RayStats rayStats = new RayStats();
        long start = System.nanoTime();

        ImageBuffer buffer = new ImageBuffer(width, height);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int j = 0; j < height; j++) {
            final int row = j;
            executor.submit(() -> {
                for (int i = 0; i < width; i++) {
                    Color c = rayTracer.getPixelColor(scene, i, row, rayStats);
                    int rgb = c.toRGB();

                    buffer.setPixel(i, row, rgb);
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long end = System.nanoTime();

        RenderStats stats = new RenderStats(
                width,
                height,
                threadCount,
                true,
                start,
                end,
                rayStats.primaryRays(),
                rayStats.shadowRays(),
                rayStats.reflectionRays()
        );

        return new RenderResult(buffer.toBufferedImage(), stats);
    }

    /**
     * Écrit une image sur disque.
     */
    public void writeToFile(String filename, BufferedImage img) throws IOException {
        File out = new File(filename);
        File parent = out.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
        ImageIO.write(img, "png", out);
    }
}
