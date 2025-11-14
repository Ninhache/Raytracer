package fr.ninhache.raytracer.render;

/**
 * Statistiques sur un rendu.
 *
 * Immuable, idéal pour logs, UI, etc.
 */
public record RenderStats(
        int width,
        int height,
        int threadCount,
        boolean multiThreaded,
        long startNanoTime,
        long endNanoTime,
        long primaryRays,
        long shadowRays,
        long reflectionRays
) {

    public long durationNanos() {
        return endNanoTime - startNanoTime;
    }

    public double durationMillis() {
        return durationNanos() / 1_000_000.0;
    }

    public double durationSeconds() {
        return durationNanos() / 1_000_000_000.0;
    }

    public int totalPixels() {
        return width * height;
    }

    public long totalRays() {
        return primaryRays + shadowRays + reflectionRays;
    }

    public double raysPerPixel() {
        if (totalPixels() == 0) return 0.0;
        return (double) totalRays() / (double) totalPixels();
    }

    public double raysPerSecond() {
        double s = durationSeconds();
        if (s <= 0.0) return 0.0;
        return totalRays() / s;
    }

    @Override
    public String toString() {
        return String.format(
                "RenderStats[%dx%d, %s, threads=%d, time=%.2f ms, rays=%d (P=%d, S=%d, R=%d), rays/pixel=%.1f, rays/s=%.1f M]",
                width, height,
                multiThreaded ? "multi" : "mono",
                threadCount,
                durationMillis(),
                totalRays(),
                primaryRays,
                shadowRays,
                reflectionRays,
                raysPerPixel(),
                raysPerSecond() / 1_000_000.0
        );
    }
}
