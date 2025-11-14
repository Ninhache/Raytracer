package fr.ninhache.raytracer.render;

import java.awt.image.BufferedImage;

/**
 * Contient le résultat d'un rendu :
 * - l'image calculée
 * - les statistiques associées
 */
public record RenderResult(
        BufferedImage image,
        RenderStats stats
) {}