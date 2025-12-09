package fr.ninhache.ui.model;

import fr.ninhache.raytracer.math.Color;
import fr.ninhache.raytracer.scene.Material;

public enum MaterialPreset {
    MATTE_RED,
    SHINY_METAL,
    GLASS,
    CUSTOM;

    public Material toMaterial() {
        return switch (this) {
            case MATTE_RED -> new Material(
                    new Color(0.8, 0.1, 0.1),
                    new Color(0.0, 0.0, 0.0),
                    5.0
            );
            case SHINY_METAL -> new Material(
                    new Color(0.6, 0.6, 0.6),
                    new Color(0.9, 0.9, 0.9),
                    80.0
            );
            case GLASS -> new Material(
                    new Color(0.1, 0.1, 0.1),
                    new Color(0.9, 0.9, 0.9),
                    120.0
            );
            case CUSTOM -> throw new IllegalStateException("CUSTOM n’a pas de valeur par défaut");
        };
    }
}
