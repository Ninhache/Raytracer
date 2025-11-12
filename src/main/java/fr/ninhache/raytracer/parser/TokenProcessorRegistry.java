package fr.ninhache.raytracer.parser;

import fr.ninhache.raytracer.scene.exception.ParseException;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.util.*;

/**
 * Registre des processeurs de tokens avec auto-découverte par réflexion.
 *
 * <p>Ce registre scanne automatiquement le classpath pour trouver toutes les classes
 * annotées avec {@link TokenHandler} et les enregistre.
 *
 * <h2>Avantages</h2>
 * <ul>
 *   <li>Ajout de nouveaux tokens sans modifier le code du parser</li>
 *   <li>Extension facile : créer une classe annotée suffit</li>
 *   <li>Pas de configuration manuelle</li>
 * </ul>
 */
public class TokenProcessorRegistry {

    private final Map<String, TokenProcessor> processors;

    public TokenProcessorRegistry(String packageName) {
        this.processors = new HashMap<>();
        discoverHandlers(packageName);
    }

    /**
     * Découvre automatiquement tous les handlers annotés avec @TokenHandler.
     */
    private void discoverHandlers(String packageName) {
        System.out.println("Chargement des handlers dans : " + packageName);

        try {
            Reflections reflections = new Reflections(packageName, Scanners.TypesAnnotated);
            Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(TokenHandler.class);

            for (Class<?> clazz : annotatedClasses) {
                try {
                    if (!TokenProcessor.class.isAssignableFrom(clazz)) {
                        System.err.println(clazz.getSimpleName() + " est annoté @TokenHandler mais n'implémente pas TokenProcessor");
                        continue;
                    }

                    TokenHandler annotation = clazz.getAnnotation(TokenHandler.class);
                    String tokenName = annotation.value();

                    TokenProcessor processor = (TokenProcessor) clazz.getDeclaredConstructor().newInstance();

                    processors.put(tokenName, processor);
                    System.out.println(tokenName + " -> " + clazz.getSimpleName());

                } catch (Exception e) {
                    System.err.println("Erreur instanciation " + clazz.getName() + ": " + e.getMessage());
                }
            }

            System.out.println(processors.size() + " handler(s) découvert(s)\n");

        } catch (Exception e) {
            System.err.println("Erreur découverte handlers: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Dispatch une ligne vers le handler approprié.
     */
    public void dispatch(String[] tokens, ParsingContext context) throws ParseException {
        if (tokens.length == 0) {
            return;
        }

        String tokenName = tokens[0];
        TokenProcessor processor = processors.get(tokenName);

        if (processor == null) {
            throw new ParseException(
                    "Token inconnu : '" + tokenName + "'",
                    context.getCurrentLineNumber(),
                    context.getCurrentLine()
            );
        }

        processor.process(tokens, context);
    }

    public void register(String tokenName, TokenProcessor processor) {
        processors.put(tokenName, processor);
    }

    public int getHandlerCount() {
        return processors.size();
    }
}