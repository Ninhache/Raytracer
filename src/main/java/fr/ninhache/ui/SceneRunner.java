package fr.ninhache.ui;

import fr.ninhache.raytracer.render.RenderResult;
import fr.ninhache.raytracer.render.Renderer;
import fr.ninhache.raytracer.scene.Scene;
import fr.ninhache.raytracer.scene.SceneLoader;

public final class SceneRunner {

    private final SceneLoader loader = new SceneLoader();
    private final Renderer renderer = new Renderer();

    public Scene loadScene(String sceneFile) throws Exception {
        return loader.load(sceneFile);
    }

    public RenderResult renderScene(Scene scene) {
        return renderer.render(scene);
    }

    public void writeImage(RenderResult result, String outputFilename) throws Exception {
        renderer.writeToFile(outputFilename, result.image());
    }
}
