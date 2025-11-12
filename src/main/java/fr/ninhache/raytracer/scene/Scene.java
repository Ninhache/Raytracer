package fr.ninhache.raytracer.scene;

import fr.ninhache.raytracer.geometry.IShape;
import fr.ninhache.raytracer.lighting.ILight;
import fr.ninhache.raytracer.math.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Représente une scène 3D complète pour le raytracing.
 *
 * <p>Une scène contient :
 * <ul>
 *   <li>Les paramètres de rendu (taille d'image, fichier de sortie)</li>
 *   <li>La caméra (position, orientation, champ de vue)</li>
 *   <li>Les paramètres d'éclairage (lumière ambiante)</li>
 *   <li>Les sources lumineuses (directionnelles, ponctuelles)</li>
 *   <li>Les objets géométriques (sphères, triangles, plans)</li>
 * </ul>
 *
 * <p>Cette classe est <strong>immuable</strong> une fois construite.
 * Utilisez {@link SceneBuilder} pour la créer.
 *
 * <h2>Exemple d'utilisation</h2>
 * <pre>{@code
 * Scene scene = new SceneBuilder()
 *     .setSize(640, 480)
 *     .setCamera(camera)
 *     .addLight(light)
 *     .addShape(sphere)
 *     .build();
 * }</pre>
 */
public final class Scene {
    private final int width;
    private final int height;
    private final String outputFilename;
    private final Camera camera;
    private final Color ambientLight;
    private final List<ILight> lights;
    private final List<IShape> shapes;

    /**
     * Construit une scène (utilisez {@link SceneBuilder}).
     *
     * @param width largeur de l'image en pixels
     * @param height hauteur de l'image en pixels
     * @param outputFilename nom du fichier de sortie
     * @param camera la caméra
     * @param ambientLight couleur de la lumière ambiante
     * @param lights liste des sources lumineuses
     * @param shapes liste des objets géométriques
     */
    Scene(int width, int height, String outputFilename, Camera camera,
          Color ambientLight, List<ILight> lights, List<IShape> shapes) {
        this.width = width;
        this.height = height;
        this.outputFilename = outputFilename;
        this.camera = camera;
        this.ambientLight = ambientLight;
        this.lights = Collections.unmodifiableList(new ArrayList<>(lights));
        this.shapes = Collections.unmodifiableList(new ArrayList<>(shapes));
    }

    /**
     * @return la largeur de l'image à générer (en pixels)
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return la hauteur de l'image à générer (en pixels)
     */
    public int getHeight() {
        return height;
    }

    /**
     * @return le nom du fichier de sortie (ex: "output.png")
     */
    public String getOutputFilename() {
        return outputFilename;
    }

    /**
     * @return la caméra de la scène
     */
    public Camera getCamera() {
        return camera;
    }

    /**
     * @return la couleur de la lumière ambiante
     */
    public Color getAmbientLight() {
        return ambientLight;
    }

    /**
     * @return une liste immuable des sources lumineuses
     */
    public List<ILight> getLights() {
        return lights;
    }

    /**
     * @return une liste immuable des objets géométriques
     */
    public List<IShape> getShapes() {
        return shapes;
    }

    /**
     * @return le nombre d'objets dans la scène
     */
    public int getShapeCount() {
        return shapes.size();
    }

    /**
     * @return le nombre de sources lumineuses
     */
    public int getLightCount() {
        return lights.size();
    }

    @Override
    public String toString() {
        return String.format("Scene[%dx%d, %d shapes, %d lights, output=%s]",
                width, height, shapes.size(), lights.size(), outputFilename);
    }
}
