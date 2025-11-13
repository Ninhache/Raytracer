
package fr.ninhache.raytracer.render;

import fr.ninhache.raytracer.geometry.Intersection;
import fr.ninhache.raytracer.geometry.Ray;
import fr.ninhache.raytracer.lighting.ILight;
import fr.ninhache.raytracer.math.Color;
import fr.ninhache.raytracer.math.Vector;
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
        int W = scene.getWidth(), H = scene.getHeight();

        double aspect = (double) W / (double) H;
        double viewH  = 2.0 * Math.tan(cam.getFovRadians() / 2.0);
        double viewW  = viewH * aspect;

        double pxW = viewW / W;
        double pxH = viewH / H;

        double sx = (i + 0.5) * pxW - (viewW * 0.5);
        double sy = (viewH * 0.5) - (j + 0.5) * pxH;

        Vector dir = cam.getU().mul(sx)
                .add(cam.getV().mul(sy))
                .sub(cam.getW())
                .normalized();

        Ray ray = new Ray(cam.getLookFrom(), dir);

        var ohit = scene.findClosestIntersection(ray);
        if (ohit.isEmpty()) return Color.BLACK;

        Intersection hit = ohit.get();
        var mat = hit.shape.getMaterial();

        Color color = scene.getAmbientLight().schur(mat.getDiffuse());

        for (ILight light : scene.getLights()) {
            Vector L = null;
            Color Lc = light.getColor();

            L = light.incidentFrom(hit.point);

            double ndotl = hit.normal.dot(L);
            if (ndotl > 0.0) {
                color = color.add(mat.getDiffuse().schur(Lc).mul(ndotl));
            }
        }

        return new Color(
                Math.max(0.0, Math.min(1.0, color.r())),
                Math.max(0.0, Math.min(1.0, color.g())),
                Math.max(0.0, Math.min(1.0, color.b()))
        );
    }
}
