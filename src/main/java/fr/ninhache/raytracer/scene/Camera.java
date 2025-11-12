package fr.ninhache.raytracer.scene;

import fr.ninhache.raytracer.math.Point;
import fr.ninhache.raytracer.math.Vector;

/**
 * Représente une caméra pour le raytracing.
 *
 * <p>Une caméra définit le point de vue depuis lequel la scène est observée.
 * Elle génère des rayons pour chaque pixel de l'image selon un modèle de caméra pinhole.
 *
 * <h2>Système de coordonnées</h2>
 * <ul>
 *   <li><strong>lookFrom</strong> : position de l'œil (origine des rayons)</li>
 *   <li><strong>lookAt</strong> : point visé par la caméra</li>
 *   <li><strong>up</strong> : direction vers le haut (définit l'orientation)</li>
 * </ul>
 *
 * <p>La caméra construit un repère orthonormé (u, v, w) où :
 * <ul>
 *   <li><strong>w</strong> = direction opposée au regard (lookFrom - lookAt)</li>
 *   <li><strong>u</strong> = direction vers la droite (up x w)</li>
 *   <li><strong>v</strong> = direction vers le haut corrigée (w x u)</li>
 * </ul>
 *
 * <h2>Exemple</h2>
 * <pre>{@code
 * // Caméra standard regardant vers -Z
 * Camera camera = new Camera(
 *     new Point(0, 0, 4),     // Position de l'œil
 *     new Point(0, 0, 0),     // Regard vers l'origine
 *     new Vector(0, 1, 0),    // Haut = +Y
 *     45.0                     // Champ de vision de 45°
 * );
 *
 * }</pre>
 */
public final class Camera {

    // Position et orientation
    private final Point lookFrom;
    private final Point lookAt;
    private final Vector up;

    // Champ de vision
    private final double fovDegrees;
    private final double fovRadians;

    // Repère orthonormé de la caméra
    private final Vector u;  // Direction droite
    private final Vector v;  // Direction haut
    private final Vector w;  // Direction opposée au regard

    // Dimensions du plan image
    private final double viewportHeight;
    private final double viewportWidth;

    /**
     * Crée une caméra.
     *
     * @param lookFrom position de l'œil
     * @param lookAt point visé
     * @param up direction vers le haut
     * @param fovDegrees angle de vue en degrés (typiquement 45-90°)
     * @throws IllegalArgumentException si les paramètres sont invalides
     */
    public Camera(Point lookFrom, Point lookAt, Vector up, double fovDegrees) {
        if (lookFrom == null || lookAt == null || up == null) {
            throw new IllegalArgumentException("lookFrom, lookAt et up ne peuvent pas être null");
        }
        if (lookFrom.equals(lookAt)) {
            throw new IllegalArgumentException("lookFrom et lookAt doivent être différents");
        }
        if (up.isZero(1e-10)) {
            throw new IllegalArgumentException("Le vecteur up ne peut pas être nul");
        }
        if (fovDegrees <= 0 || fovDegrees >= 180) {
            throw new IllegalArgumentException("Le FOV doit être dans ]0, 180[ degrés");
        }

        this.lookFrom = lookFrom;
        this.lookAt = lookAt;
        this.up = up.normalized();
        this.fovDegrees = fovDegrees;
        this.fovRadians = Math.toRadians(fovDegrees);

        // Construction du repère orthonormé
        this.w = lookFrom.sub(lookAt).normalized();  // Direction opposée au regard
        this.u = up.cross(w).normalized();           // Direction droite
        this.v = w.cross(u);                         // Direction haut corrigée

        // Calcul des dimensions du viewport (plan image)
        // Le viewport est à distance 1 de la caméra
        this.viewportHeight = 2.0 * Math.tan(fovRadians / 2.0);
        this.viewportWidth = viewportHeight; // Aspect ratio sera géré par le raytracer
    }
}
