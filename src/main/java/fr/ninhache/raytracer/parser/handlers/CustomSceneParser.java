package fr.ninhache.raytracer.parser.handlers;


import fr.ninhache.raytracer.parser.*;
import fr.ninhache.raytracer.scene.Scene;
import fr.ninhache.raytracer.scene.SceneBuilder;
import fr.ninhache.raytracer.scene.SceneParser;
import fr.ninhache.raytracer.scene.exception.ParseException;


import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Parser pour le format de scène custom du projet.
 *
 * <p>Ce parser traite les fichiers textuels ligne par ligne selon le format :
 * <pre>
 * size 640 480
 * camera 0 0 4 0 0 0 0 1 0 45
 * diffuse 0.9 0 0
 * sphere 0 1 0 0.5
 * ...
 * </pre>
 *
 * <p>Architecture : chaque type de ligne (token) est traité par un {@link TokenHandler}
 * dédié, enregistré dans un {@link TokenHandlerRegistry}.
 *
 * <h2>Tokens supportés</h2>
 * <ul>
 *   <li>{@code size} : dimensions de l'image</li>
 *   <li>{@code output} : nom du fichier de sortie</li>
 *   <li>{@code camera} : position et orientation de la caméra</li>
 *   <li>{@code ambient} : lumière ambiante</li>
 *   <li>{@code diffuse} : matériau diffus</li>
 *   <li>{@code specular} : matériau spéculaire</li>
 *   <li>{@code directional} : lumière directionnelle</li>
 *   <li>{@code point} : lumière ponctuelle</li>
 *   <li>{@code sphere} : sphère</li>
 *   <li>{@code maxverts} : nombre maximum de vertices</li>
 *   <li>{@code vertex} : définition d'un vertex</li>
 *   <li>{@code tri} : triangle</li>
 *   <li>{@code plane} : plan</li>
 * </ul>
 */
public class CustomSceneParser implements SceneParser {

    private final TokenHandlerRegistry registry;

    /**
     * Crée un parser avec les handlers par défaut.
     */
    public CustomSceneParser() {
        this.registry = new TokenHandlerRegistry();
        registerDefaultHandlers();
    }

    /**
     * Enregistre tous les handlers de tokens supportés.
     */
    private void registerDefaultHandlers() {
        /*
        // Paramètres de rendu
        registry.register(new SizeTokenHandler());
        registry.register(new OutputTokenHandler());

        // Caméra
        registry.register(new CameraTokenHandler());

        // Matériaux
        registry.register(new AmbientTokenHandler());
        registry.register(new DiffuseTokenHandler());
        registry.register(new SpecularTokenHandler());

        // Lumières
        registry.register(new DirectionalLightTokenHandler());
        registry.register(new PointLightTokenHandler());

        // Formes
        registry.register(new SphereTokenHandler());
        registry.register(new MaxVertsTokenHandler());
        registry.register(new VertexTokenHandler());
        registry.register(new TriangleTokenHandler());
        registry.register(new PlaneTokenHandler());
        */

    }

    @Override
    public Scene parse(InputStream input) throws IOException, ParseException {
        SceneBuilder builder = new SceneBuilder();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(input, StandardCharsets.UTF_8))) {

            int lineNumber = 0;
            String line;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                // Nettoyage de la ligne
                line = line.trim();

                // Ignorer les lignes vides et les commentaires
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                // Découpage en tokens
                String[] tokens = line.split("\\s+");

                // Dispatch vers le handler approprié
                try {
                    registry.dispatch(tokens, lineNumber, builder);
                } catch (NumberFormatException e) {
                    throw new ParseException(
                        "Erreur de format numérique : " + e.getMessage(),
                        lineNumber, line, e
                    );
                } catch (ParseException e) {
                    // Enrichir l'exception si elle n'a pas encore de contexte
                    if (e.getLineNumber() == 0) {
                        throw new ParseException(e.getMessage(), lineNumber, line, e.getCause());
                    }
                    throw e;
                }
            }
        }

        // Construction finale avec validation
        return builder.build();
    }

    @Override
    public boolean canParse(String filename, byte[] firstBytes) {
        // Détection par extension
        if (filename != null && filename.toLowerCase().endsWith(".txt")) {
            return true;
        }

        // Détection par contenu (recherche de tokens typiques)
        if (firstBytes != null && firstBytes.length > 0) {
            String content = new String(firstBytes, StandardCharsets.UTF_8).toLowerCase();
            return content.contains("size") ||
                    content.contains("camera") ||
                    content.contains("sphere");
        }

        return false;
    }
}
