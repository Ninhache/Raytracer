package fr.ninhache.raytracer.parser.custom;


import fr.ninhache.raytracer.parser.TokenHandler;
import fr.ninhache.raytracer.scene.SceneBuilder;
import fr.ninhache.raytracer.scene.exception.ParseException;

import java.util.HashMap;
import java.util.Map;

/**
 * Registre central des handlers de tokens.
 *
 * <p>Cette classe implémente un pattern <strong>Registry</strong> pour gérer
 * dynamiquement l'ensemble des commandes supportées par le parser.
 *
 * <h2>Avantages</h2>
 * <ul>
 *   <li>Extensibilité : ajout de nouveaux tokens sans modifier le parser principal</li>
 *   <li>Testabilité : chaque handler peut être testé indépendamment</li>
 *   <li>Maintenabilité : logique de chaque commande isolée dans sa propre classe</li>
 * </ul>
 */
public class TokenHandlerRegistry {

    private final Map<String, TokenHandler> handlers;

    /**
     * Crée un registre vide.
     */
    public TokenHandlerRegistry() {
        this.handlers = new HashMap<>();
    }

    /**
     * Enregistre un handler pour un token spécifique.
     *
     * @param handler le handler à enregistrer
     */
    public void register(TokenHandler handler) {
        handlers.put(handler.getTokenName(), handler);
    }

    /**
     * Traite une ligne de commande en déléguant au handler approprié.
     *
     * @param tokens la ligne découpée en tokens
     * @param lineNumber numéro de ligne
     * @param builder constructeur de scène
     * @throws ParseException si le token est inconnu ou mal formé
     */
    public void dispatch(String[] tokens, int lineNumber, SceneBuilder builder)
            throws ParseException {

        if (tokens.length == 0) {
            return; // Ligne vide
        }

        String tokenName = tokens[0];
        TokenHandler handler = handlers.get(tokenName);

        if (handler == null) {
            throw new ParseException(
                    "Token inconnu : '" + tokenName + "'",
                    lineNumber,
                    String.join(" ", tokens)
            );
        }

        handler.handle(tokens, lineNumber, builder);
    }

    /**
     * Vérifie si un token est enregistré.
     *
     * @param tokenName nom du token
     * @return {@code true} si un handler existe pour ce token
     */
    public boolean hasHandler(String tokenName) {
        return handlers.containsKey(tokenName);
    }
}