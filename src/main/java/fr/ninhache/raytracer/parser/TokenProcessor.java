package fr.ninhache.raytracer.parser;

import fr.ninhache.raytracer.scene.exception.ParseException;

/**
 * Interface pour les processeurs de tokens.
 *
 * <p>Chaque handler de token doit implémenter cette interface et être annoté
 * avec {@link TokenHandler}.
 */
public interface TokenProcessor {

    /**
     * Traite un token du fichier de scène.
     *
     * @param tokens la ligne découpée en tokens (tokens[0] = nom du token)
     * @param context le contexte de parsing (état courant, constructeur de scène)
     * @throws ParseException si le token est mal formé
     */
    void process(String[] tokens, ParsingContext context) throws ParseException;
}
