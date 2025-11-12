package fr.ninhache.raytracer.parser;

import fr.ninhache.raytracer.scene.Scene;
import fr.ninhache.raytracer.scene.exception.ParseException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Interface pour les parsers de format de fichier.
 *
 * <p>Chaque parser gère la LECTURE d'un format spécifique (texte, JSON, binaire...)
 * mais utilise les MÊMES TokenProcessors pour interpréter les commandes.
 *
 * <h2>Responsabilité</h2>
 * <ul>
 *   <li>Lire le fichier selon son format</li>
 *   <li>Extraire les "commandes" (tokens)</li>
 *   <li>Déléguer à {@link TokenProcessorRegistry}</li>
 * </ul>
 */
public interface FormatParser {

    /**
     * Parse un flux d'entrée et construit une scène.
     *
     * @param input le flux d'entrée
     * @param registry le registre de processors (logique métier partagée)
     * @return la scène construite
     * @throws IOException si erreur de lecture
     * @throws ParseException si erreur d'interprétation
     */
    Scene parse(InputStream input, TokenProcessorRegistry registry)
            throws IOException, ParseException;

    /**
     * Vérifie si ce parser peut traiter le fichier.
     *
     * @param filename nom du fichier (peut être null)
     * @param firstBytes premiers octets du fichier
     * @return true si ce parser peut traiter ce fichier
     */
    boolean canParse(String filename, byte[] firstBytes);
}
