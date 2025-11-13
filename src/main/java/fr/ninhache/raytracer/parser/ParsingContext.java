package fr.ninhache.raytracer.parser;

import fr.ninhache.raytracer.geometry.IShape;
import fr.ninhache.raytracer.lighting.ILight;
import fr.ninhache.raytracer.math.Color;
import fr.ninhache.raytracer.math.Point;
import fr.ninhache.raytracer.scene.Camera;
import fr.ninhache.raytracer.scene.SceneBuilder;
import fr.ninhache.raytracer.scene.exception.ParseException;

import java.util.ArrayList;
import java.util.List;

/**
 * Contexte de parsing maintenant l'état courant pendant le parsing.
 *
 * <p>Ce contexte contient :
 * <ul>
 *   <li>Le {@link SceneBuilder} pour construire la scène</li>
 *   <li>L'état contextuel : couleurs diffuse/specular/ambient courantes</li>
 *   <li>Les vertices déclarés (pour les triangles)</li>
 *   <li>Le numéro de ligne courant (pour les messages d'erreur)</li>
 * </ul>
 *
 * <h2>Principe</h2>
 * <p>Les instructions {@code diffuse}, {@code specular}, {@code ambient} modifient
 * l'état du contexte. Les objets créés ensuite ({@code sphere}, {@code tri}, etc.)
 * héritent de cet état.
 */
public class ParsingContext {

    private final SceneBuilder sceneBuilder;

    // État contextuel (modifié par les tokens de matériaux)
    private int currentShininess;
    private Color currentAmbient;
    private Color currentDiffuse;
    private Color currentSpecular;

    // Gestion des vertices (pour les triangles)
    private int maxVertices;
    private final List<Point> vertices;

    // Informations de debug
    private int currentLineNumber;
    private String currentLine;

    /**
     * Crée un nouveau contexte de parsing.
     */
    public ParsingContext() {
        this.sceneBuilder = new SceneBuilder();
        this.currentAmbient = Color.BLACK;
        this.currentDiffuse = Color.BLACK;
        this.currentSpecular = Color.BLACK;
        this.vertices = new ArrayList<>();
        this.maxVertices = 0;
        this.currentLineNumber = 0;
    }

    public SceneBuilder getSceneBuilder() {
        return sceneBuilder;
    }

    public void setCamera(Camera camera) {
        this.sceneBuilder.setCamera(camera);
    }

    public void addLight(ILight light) throws ParseException {
        this.sceneBuilder.addLight(light);
    }

    public void addShape(IShape shape) throws ParseException {
        this.sceneBuilder.addShape(shape);
    }


    public Color getCurrentAmbient() {
        return currentAmbient;
    }

    public void setCurrentAmbient(Color ambient) throws ParseException {
        this.sceneBuilder.setAmbientLight(ambient);
    }

    public void setCurrentShininess(double shininess) throws ParseException {
        this.sceneBuilder.setShininess(shininess);
    }

    public Color getCurrentDiffuse() {
        return currentDiffuse;
    }

    public void setCurrentDiffuse(Color diffuse) throws ParseException {
        this.currentDiffuse = diffuse;
        this.sceneBuilder.setDiffuse(diffuse);
    }

    public Color getCurrentSpecular() {
        return currentSpecular;
    }

    public void setCurrentSpecular(Color specular) throws ParseException {
        this.sceneBuilder.setSpecular(specular);
    }

    public int getMaxVertices() {
        return maxVertices;
    }

    public void setMaxVertices(int maxVertices) {
        this.maxVertices = maxVertices;
    }

    public void addVertex(Point vertex) throws ParseException {
        if (maxVertices == 0) {
            throw new ParseException("maxverts doit être défini avant de déclarer des vertices");
        }
        if (vertices.size() >= maxVertices) {
            throw new ParseException(
                    String.format("Nombre de vertices (%d) dépasse maxverts (%d)",
                            vertices.size() + 1, maxVertices)
            );
        }
        vertices.add(vertex);
    }

    public Point getVertex(int index) throws ParseException {
        if (index < 0 || index >= vertices.size()) {
            throw new ParseException(
                    String.format("Indice de vertex invalide : %d (disponibles : 0-%d)",
                            index, vertices.size() - 1)
            );
        }
        return vertices.get(index);
    }

    public int getCurrentLineNumber() {
        return currentLineNumber;
    }

    public void setCurrentLineNumber(int lineNumber) {
        this.currentLineNumber = lineNumber;
    }

    public String getCurrentLine() {
        return currentLine;
    }

    public void setCurrentLine(String line) {
        this.currentLine = line;
    }

    public void setOutputFilename(String filename) {
        this.sceneBuilder.setOutputFilename(filename);
    }

    public void setSize(int width, int height) throws ParseException {
        this.sceneBuilder.setSize(width, height);
    }

}