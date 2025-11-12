package fr.ninhache.raytracer.parser.formats;

import fr.ninhache.raytracer.parser.*;
import fr.ninhache.raytracer.scene.Scene;
import fr.ninhache.raytracer.scene.exception.ParseException;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Parser pour les fichiers texte ligne par ligne (format custom du projet).
 *
 * <p>Ce parser lit le fichier ligne par ligne, découpe chaque ligne en tokens,
 * et délègue l'interprétation au TokenProcessorRegistry.
 */
@FileFormatParser(
        extensions = {".txt", ".scene"},
        description = "Format texte ligne par ligne"
)
public class LineBasedFormatParser implements FormatParser {

    @Override
    public Scene parse(InputStream input, TokenProcessorRegistry registry)
            throws IOException, ParseException {

        ParsingContext context = new ParsingContext();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(input, StandardCharsets.UTF_8))) {

            int lineNumber = 0;
            String line;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                // Ignorer commentaires et lignes vides
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                // Découpage en tokens
                String[] tokens = line.split("\\s+");

                // Mise à jour du contexte
                context.setCurrentLineNumber(lineNumber);
                context.setCurrentLine(line);

                // Déléguer au registry
                try {
                    registry.dispatch(tokens, context);
                } catch (NumberFormatException e) {
                    throw new ParseException(
                            "Erreur de format numérique : " + e.getMessage(),
                            lineNumber, line, e
                    );
                }
            }
        }

        // Construction finale
        return context.getSceneBuilder().build();
    }

    @Override
    public boolean canParse(String filename, byte[] firstBytes) {
        // Détecter par extension
        if (filename != null) {
            String lower = filename.toLowerCase();
            if (lower.endsWith(".txt") || lower.endsWith(".scene")) {
                return true;
            }
        }

        // Détecter par contenu (recherche de mots-clés)
        if (firstBytes != null && firstBytes.length > 0) {
            String content = new String(firstBytes, StandardCharsets.UTF_8).toLowerCase();
            return content.contains("size") || content.contains("camera");
        }

        return false;
    }
}
