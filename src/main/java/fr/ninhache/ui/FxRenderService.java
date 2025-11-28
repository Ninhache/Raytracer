package fr.ninhache.ui;

import fr.ninhache.raytracer.render.RenderResult;
import fr.ninhache.raytracer.render.Renderer;
import fr.ninhache.raytracer.scene.Scene;
import javafx.application.Platform;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Service de rendu asynchrone pour JavaFX.
 */
public final class FxRenderService {

    private final Renderer renderer = new Renderer();
    private final ExecutorService pool =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public void renderAsync(Scene scene,
                            Consumer<RenderResult> onSuccess,
                            Consumer<Throwable> onError) {

        pool.submit(() -> {
            try {
                // 👉 utilise ton API actuelle
                RenderResult result = renderer.render(scene);
                Platform.runLater(() -> onSuccess.accept(result));
            } catch (Throwable t) {
                Platform.runLater(() -> onError.accept(t));
            }
        });
    }

    public void shutdown() {
        pool.shutdownNow();
    }
}