package fr.ninhache.raytracer.scene;

import fr.ninhache.raytracer.scene.Scene;
import fr.ninhache.raytracer.scene.exception.ParseException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Interface définissant un parser de scène 3D.
 *
 * <p>Cette interface suit le pattern <strong>Strategy</strong> pour permettre
 * différents formats de fichiers (custom, Blender, OBJ, FBX, etc.).
 *
 * <h2>Implémentations prévues</h2>
 * <ul>
 *   <li>{@code CustomSceneParser} : format textuel du projet</li>
 *   <li>{@code BlenderSceneParser} : fichiers .blend (futur)</li>
 *   <li>{@code ObjSceneParser} : format Wavefront OBJ (futur)</li>
 * </ul>
 *
 * <h2>Exemple d'utilisation</h2>
 * <pre>{@code
 * SceneLoader loader = new SceneLoader();
 * Scene scene = loader.load("scenes/scene1.txt");
 * }</pre>
 */
public interface SceneParser {

    /**
     * Parse un flux d'entrée pour créer une scène 3D.
     *
     * @param input le flux contenant la description de la scène
     * @return la scène construite
     * @throws IOException si une erreur de lecture survient
     * @throws ParseException si le format est invalide
     */
    Scene parse(InputStream input) throws IOException, ParseException;

    /**
     * Indique si ce parser peut traiter le fichier donné.
     *
     * <p>Permet au {@link SceneLoader} de choisir le bon parser automatiquement
     * en fonction de l'extension ou du contenu du fichier.
     *
     * @param filename le nom du fichier (peut être null)
     * @param firstBytes les premiers octets du fichier (pour détecter le format)
     * @return {@code true} si ce parser peut traiter ce fichier
     */
    boolean canParse(String filename, byte[] firstBytes);
}