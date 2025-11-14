package fr.ninhache.raytracer.render.stats;

import java.util.concurrent.atomic.LongAdder;

/**
 * Compteur thread-safe de rayons lancés pendant un rendu.
 *
 * Utilise LongAdder pour de bonnes perf en contexte multi-thread.
 */
public final class RayStats {

    private final LongAdder primaryRays = new LongAdder();
    private final LongAdder shadowRays = new LongAdder();
    private final LongAdder reflectionRays = new LongAdder();

    public void incPrimary() {
        primaryRays.increment();
    }

    public void incShadow() {
        shadowRays.increment();
    }

    public void incReflection() {
        reflectionRays.increment();
    }

    public long primaryRays() {
        return primaryRays.sum();
    }

    public long shadowRays() {
        return shadowRays.sum();
    }

    public long reflectionRays() {
        return reflectionRays.sum();
    }

    public long totalRays() {
        return primaryRays() + shadowRays() + reflectionRays();
    }
}
