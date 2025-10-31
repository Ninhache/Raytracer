package fr.ninhache;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public final class ResourceLoader {
    private ResourceLoader() { }

    public static <T> T loadResource(String fileName, IOFunction<InputStream, T> mapper) {
        try (InputStream is = openResourceStream(fileName)) {
            return mapper.apply(is);
        } catch (IOException e) {
            throw new RuntimeException("Impossible de charger la ressource : " + fileName, e);
        }
    }

    private static InputStream openResourceStream(String fileName) {
        InputStream is = ResourceLoader.class.getClassLoader().getResourceAsStream(fileName);
        if (is == null) {
            throw new IllegalArgumentException("Ressource introuvable : " + fileName);
        }
        return is;
    }

    public static String loadResourceAsString(String fileName) {
        return loadResource(fileName, is -> {
            try (BufferedReader reader =
                         new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        });
    }

    public static BufferedImage loadResourceAsBufferedImage(String fileName) {
        return loadResource(fileName, is -> {
            BufferedImage img = ImageIO.read(is);
            if (img == null) {
                throw new IOException("Format d’image non supporté ou image vide : " + fileName);
            }
            return img;
        });
    }

    public static byte[] loadResourceAsBytes(String fileName) {
        return loadResource(fileName, is -> {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            is.transferTo(buffer);
            return buffer.toByteArray();
        });
    }

    public static Reader loadResourceAsReader(String fileName) {
        return new InputStreamReader(openResourceStream(fileName), StandardCharsets.UTF_8);
    }

    @FunctionalInterface
    public interface IOFunction<I, O> {
        O apply(I in) throws IOException;
    }
}
