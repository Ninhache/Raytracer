package fr.ninhache.raytracer.render;

import fr.ninhache.raytracer.geometry.Intersection;
import fr.ninhache.raytracer.geometry.Ray;
import fr.ninhache.raytracer.lighting.ILight;
import fr.ninhache.raytracer.lighting.PointLight;
import fr.ninhache.raytracer.lighting.SpotLight;
import fr.ninhache.raytracer.math.Color;
import fr.ninhache.raytracer.math.Point;
import fr.ninhache.raytracer.math.Vector;
import fr.ninhache.raytracer.scene.Camera;
import fr.ninhache.raytracer.scene.Scene;

import java.util.Optional;

import static fr.ninhache.raytracer.math.Epsilon.EPS;

/**
 * RayTracer avec :
 * - ambiante
 * - Lambert + Blinn-Phong
 * - ombres
 * - réflexions jusqu'à maxDepth
 */
public final class RayTracer {

    /**
     * Calcule la couleur d'un pixel (i,j) en lançant un rayon primaire
     * depuis la caméra et en appelant la fonction récursive traceRay.
     */
    public Color getPixelColor(Scene scene, int i, int j) {
        Camera cam = scene.getCamera();
        int W = scene.getWidth(), H = scene.getHeight();

        double aspect = (double) W / (double) H;
        double viewH = 2.0 * Math.tan(cam.getFovRadians() / 2.0); // FOV vertical
        double viewW = viewH * aspect;

        double pxW = viewW / W;
        double pxH = viewH / H;

        double sx = (i + 0.5) * pxW - (viewW * 0.5);
        double sy = (viewH * 0.5) - (j + 0.5) * pxH;

        Vector dir = cam.getU().mul(sx)
                .add(cam.getV().mul(sy))
                .sub(cam.getW())
                .normalized();

        Ray primary = new Ray(cam.getLookFrom(), dir);
        return traceRay(scene, primary, 1);
    }

    /**
     * Calcule la couleur vue le long d'un rayon, avec récursion pour les réflexions.
     *
     * @param scene la scène
     * @param ray   le rayon courant
     * @param depth profondeur actuelle (1 pour le rayon primaire)
     */
    private Color traceRay(Scene scene, Ray ray, int depth) {
        Optional<Intersection> ohit = scene.findClosestIntersection(ray);
        if (ohit.isEmpty()) {
            return Color.BLACK;
        }

        Intersection hit = ohit.get();
        var mat = hit.shape.getMaterial();
        Camera cam = scene.getCamera();

        // Vecteur vue (du point vers la caméra)
        Vector V = cam.getLookFrom().sub(hit.point).normalized();

        Color amb = scene.getAmbientLight();
        Color kd  = mat.getDiffuse();
        Color ks  = mat.getSpecular();
        double shininess = mat.getShininess();

        Color color = amb.schur(kd);

        // Origine légèrement décalée pour éviter les "auto-intersections"
        Point shadowOrigin = hit.point.add(hit.normal.mul(EPS));

        for (ILight light : scene.getLights()) {
            Vector L = light.incidentFrom(shadowOrigin);

            double maxT = Double.POSITIVE_INFINITY;
            if (light instanceof PointLight pl) {
                maxT = pl.getPosition().sub(shadowOrigin).length();
            } else if (light instanceof SpotLight sl) {
                maxT = sl.getPosition().sub(shadowOrigin).length();
            }

            Ray shadowRay = new Ray(shadowOrigin, L);
            Optional<Intersection> occ = scene.findClosestIntersection(shadowRay);
            if (occ.isPresent()) {
                double t = occ.get().t;
                if (t > EPS && t < maxT - EPS) {
                    // Un objet bloque la lumière
                    continue;
                }
            }

            double ndotl = hit.normal.dot(L);
            if (ndotl <= 0.0) {
                continue;
            }

            Color lc = light.getColor();

            Color lambert = kd.schur(lc).mul(ndotl);
            color = color.add(lambert);

            if (shininess > 0.0 && (ks.r() > 0 || ks.g() > 0 || ks.b() > 0)) {
                Vector H = L.add(V).normalized();
                double ndoth = Math.max(0.0, hit.normal.dot(H));
                if (ndoth > 0.0) {
                    double specPow = Math.pow(ndoth, shininess);
                    Color spec = ks.schur(lc).mul(specPow);
                    color = color.add(spec);
                }
            }
        }

        int maxDepth = scene.getMaxDepth();
        boolean hasSpecular = ks.r() > 0 || ks.g() > 0 || ks.b() > 0;

        if (depth < maxDepth && hasSpecular) {
            Vector reflDir = ray.getDirection().reflect(hit.normal).normalized();
            Point reflOrigin = hit.point.add(hit.normal.mul(EPS));
            Ray reflRay = new Ray(reflOrigin, reflDir);

            Color reflected = traceRay(scene, reflRay, depth + 1);

            Color reflContribution = ks.schur(reflected);
            color = color.add(reflContribution);
        }

        return new Color(
                clamp01(color.r()),
                clamp01(color.g()),
                clamp01(color.b())
        );
    }

    private double clamp01(double v) {
        return Math.max(0.0, Math.min(1.0, v));
    }
}
