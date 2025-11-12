package fr.ninhache.raytracer.scene;

import fr.ninhache.raytracer.parser.FileFormatParser;
import fr.ninhache.raytracer.parser.FormatParser;
import fr.ninhache.raytracer.parser.TokenHandler;
import fr.ninhache.raytracer.parser.TokenProcessorRegistry;
import fr.ninhache.raytracer.scene.exception.ParseException;

import java.io.*;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.util.*;

/**
 * Façade pour charger des scènes qui connaît en avance les formats qu'il sait lire
 *
 * <p>Cette classe :
 * <ul>
 *   <li>Découvre automatiquement tous les parsers de format disponibles</li>
 *   <li>Détecte le format du fichier</li>
 *   <li>Délègue au parser approprié</li>
 *   <li>Fournit un registre partagé de TokenProcessors</li>
 * </ul>
 */
public class SceneLoader {

    private final List<FormatParser> formatParsers;
    private final TokenProcessorRegistry tokenRegistry;
    private final Map<String, List<FormatParser>> parsersByExt = new HashMap<>();

    public SceneLoader() {
        this.formatParsers = new ArrayList<>();
        this.tokenRegistry = new TokenProcessorRegistry("fr.ninhache.raytracer.parser.handlers");

        discoverFormatParsers("fr.ninhache.raytracer.parser.formats");
    }

    /**
     * Découvre tous les parsers de format annotés.
     */
    private void discoverFormatParsers(String packageName) {
        System.out.println("Chargement des parsers de format dans : " + packageName);

        Reflections reflections = new Reflections(packageName, Scanners.TypesAnnotated);
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(FileFormatParser.class);

        for (Class<?> clazz : annotatedClasses) {
            try {
                if (!FormatParser.class.isAssignableFrom(clazz)) {
                    System.err.println(clazz.getName() + " est annoté @FileFormatParser mais n'implémente pas FormatParser");
                    continue;
                }

                FileFormatParser annotation = clazz.getAnnotation(FileFormatParser.class);
                FormatParser parser = (FormatParser) clazz.getDeclaredConstructor().newInstance();

                formatParsers.add(parser);

                for (String ext : annotation.extensions()) {
                    String norm = normalizeExt(ext);
                    parsersByExt.computeIfAbsent(norm, k -> new ArrayList<>()).add(parser);
                }

                String extensions = String.join(", ", annotation.extensions());
                System.out.println("Parser enregistré : " + clazz.getSimpleName() + " (" + extensions + ")");

            } catch (Exception e) {
                System.err.println("Erreur lors de l'instanciation de " + clazz.getName());
            }
        }

        formatParsers.sort((a, b) -> {
            int priorityA = a.getClass().getAnnotation(FileFormatParser.class).priority();
            int priorityB = b.getClass().getAnnotation(FileFormatParser.class).priority();
            return Integer.compare(priorityA, priorityB);
        });

        for (List<FormatParser> bucket : parsersByExt.values()) {
            bucket.sort((a, b) -> {
                int pa = a.getClass().getAnnotation(FileFormatParser.class).priority();
                int pb = b.getClass().getAnnotation(FileFormatParser.class).priority();
                return Integer.compare(pa, pb);
            });
        }


        System.out.println(formatParsers.size() + " parser(s) de format découvert(s)\n");
    }

    /**
     * Charge une scène depuis un fichier (détection automatique du format).
     *
     * @param filepath chemin du fichier
     * @return la scène construite
     * @throws IOException si erreur de lecture
     * @throws ParseException si erreur de parsing
     */
    public Scene load(String filepath) throws IOException, ParseException {
        Path path = Paths.get(filepath);

        if (!Files.exists(path)) {
            throw new FileNotFoundException("Fichier introuvable : " + filepath);
        }

        byte[] firstBytes = readFirstBytes(path, 512);

        FormatParser parser = findParser(filepath, firstBytes);

        if (parser == null) {
            throw new ParseException("Format de fichier non supporté : " + filepath);
        }

        System.out.println("Format détecté : " + parser.getClass().getSimpleName());

        try (InputStream input = Files.newInputStream(path)) {
            return parser.parse(input, tokenRegistry);
        }
    }

    /**
     * Trouve le parser approprié en parcourant les méthodes naïve suivante, si un parser n'est pas clairement identifié, on passe à la méthode suivante
     *
     * Méthode naïve :
     * 1 - Par extension
     * 2 - Par bruteforce (Test tous les parsers)
     */
    private FormatParser findParser(String filename, byte[] firstBytes) {
        String ext = extractExt(filename);


        List<FormatParser> candidates = (ext == null)
                ? Collections.emptyList()
                : parsersByExt.getOrDefault(ext, Collections.emptyList());
        for (FormatParser p : candidates) {
            if (safeCanParse(p, filename, firstBytes)) {
                return p;
            }
        }

        for (FormatParser p : formatParsers) {
            if (candidates.contains(p)) continue;
            if (safeCanParse(p, filename, firstBytes)) return p;
        }

        return null;
    }

    /**
     * Permet d'identifier l'extension d'un fichier par son nom de fichier
     *
     * @param filename
     * @return son extension
     */
    private static String extractExt(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot <= 0 || lastDot == filename.length() - 1) return null;
        return normalizeExt(filename.substring(lastDot + 1));
    }

    /**
     * "Normalise" une extension de fichier pour qu'elle renvoie toujours la même chose
     *
     * @param ".json" ou "json"
     * @return renvoie "json" en lower-case
     */
    private static String normalizeExt(String raw) {
        String s = raw.trim();
        if (s.startsWith(".")) {
            s = s.substring(1);
        }

        return s.toLowerCase();
    }

    private static boolean safeCanParse(FormatParser p, String filename, byte[] firstBytes) {
        try {
            return p.canParse(filename, firstBytes);
        } catch (Throwable t) {
            System.err.println("canParse a échoué pour " + p.getClass().getSimpleName() + " : " + t.getMessage());
            return false;
        }
    }

    /**
     * Lit les premiers octets d'un fichier.
     */
    private byte[] readFirstBytes(Path path, int maxBytes) throws IOException {
        try (InputStream input = Files.newInputStream(path)) {
            byte[] buffer = new byte[maxBytes];
            int bytesRead = input.read(buffer);
            if (bytesRead < buffer.length) {
                return Arrays.copyOf(buffer, bytesRead);
            }
            return buffer;
        }
    }
}