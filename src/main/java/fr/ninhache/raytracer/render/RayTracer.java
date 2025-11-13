
package fr.ninhache.raytracer.render;

import fr.ninhache.raytracer.math.Color;
import fr.ninhache.raytracer.math.Vector;
import fr.ninhache.raytracer.geometry.Ray;
import fr.ninhache.raytracer.scene.Camera;
import fr.ninhache.raytracer.scene.Scene;

/** Calcule la couleur d'un pixel en traçant un rayon */
public final class RayTracer {

    /**
     * Calcule la couleur d'un pixel (i,j) selon l'algorithme du jalon 1/2 (intersections + ambiant).
     * - Noir si aucun objet n'est touché
     * - Couleur ambiante de la scène sinon
     */
    public Color getPixelColor(Scene scene, int i, int j) {
        Camera cam = scene.getCamera();
        int imgW = scene.getWidth();
        int imgH = scene.getHeight();

        // Viewport en unités caméra
        double aspect = (double) imgW / (double) imgH;
        double viewH  = 2.0 * Math.tan(cam.getFovRadians() / 2.0); // FOV vertical
        double viewW  = viewH * aspect;

        double pxW = viewW / imgW;
        double pxH = viewH / imgH;

        // Coordonnées du centre du pixel (i,j) dans le plan image
        double sx = (i + 0.5) * pxW - (viewW * 0.5);

        // j vers le bas -> sy positif vers le haut
        double sy = (viewH * 0.5) - (j + 0.5) * pxH;

        // Direction du rayon (u = droite, v = haut, w = vers l’arrière du plan image)
        Vector dir = cam.getU().mul(sx)
                .add(cam.getV().mul(sy))
                .sub(cam.getW())
                .normalized();

        Ray ray = new Ray(cam.getLookFrom(), dir);

        var hit = scene.findClosestIntersection(ray);
        return hit.isPresent() ? scene.getAmbientLight() : Color.BLACK;
    }
}
