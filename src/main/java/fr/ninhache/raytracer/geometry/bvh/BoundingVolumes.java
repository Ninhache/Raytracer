package fr.ninhache.raytracer.geometry.bvh;

import fr.ninhache.raytracer.geometry.IShape;
import fr.ninhache.raytracer.geometry.shape.Disk;
import fr.ninhache.raytracer.geometry.shape.Plane;
import fr.ninhache.raytracer.geometry.shape.RegularPolygon;
import fr.ninhache.raytracer.geometry.shape.Sphere;
import fr.ninhache.raytracer.geometry.shape.Triangle;
import fr.ninhache.raytracer.math.Point;

/**
 * Fournit des méthodes utilitaires pour créer des boîtes BVH alignées sur les axes (Axis-Aligned Bounding Boxes, AABB) pour des formes géométriques connues
 *
 * <p>Une boîte englobante alignée sur les axes est un volume rectangulaire minimal,
 * aligné sur les axes du repère (X, Y, Z), qui contient entièrement une forme donnée.
 * Ce type de volume est largement utilisé en rendu 3D et en détection de collisions:
 * <ul>
 *   <li><strong>Encapsulation rapide</strong>: permet d'approximer la forme avec un volume simple</li>
 *   <li><strong>Tests d'intersection efficaces</strong>: les comparaisons se réduisent à des tests
 *       de bornes sur chaque axe</li>
 *   <li><strong>Optimisation</strong>: sert de brique de base pour les structures d'accélération
 *       (BVH, octrees, grilles, etc.)</li>
 * </ul>
 *
 * <p>Les méthodes fournies ici construisent automatiquement ces "boîtes" à partir de formes dont la géométrie est connue (sphère, triangle, disque, polygone régulier, etc.),
 * évitant ainsi les calculs répétitifs (et accessoirement les erreurs de manipulation des coordonnées)
 */
public final class BoundingVolumes {

    /**
     * Classe utilitaire: constructeur privé pour empêcher l'instanciation.
     */
    private BoundingVolumes() {}

    /**
     * Calcule une "boîte" approximative pour une forme donnée.
     *
     * <p>Formes actuellement supportées:
     * <ul>
     *   <li>{@link Sphere}: "boîte" du rayon autour du centre</li>
     *   <li>{@link Triangle}: "boîte" serrée à partir des trois sommets</li>
     *   <li>{@link Disk}: "boîte" du disque vue comme une sphère aplatie</li>
     *   <li>{@link RegularPolygon}: "boîte" du polygone à partir de son centre et de son rayon</li>
     * </ul>
     *
     * <p>Pour les formes infinies comme {@link Plane}, ou toute forme dont les bornes
     * ne peuvent pas être déterminées proprement, la méthode retourne une
     * "boîte" infinie via {@link BoundingBox#infinite()}.
     *
     * @param shape forme dont on souhaite obtenir une "boîte", ne doit pas être null
     * @return boîte alignée sur les axes qui contient la forme, ou une boîte infinie si la forme ne peut pas être bornée correctement
     */
    public static BoundingBox forShape(IShape shape) {
        if (shape instanceof Sphere sphere) {
            Point c = sphere.getCenter();
            double r = sphere.getRadius();
            Point min = new Point(c.x - r, c.y - r, c.z - r);
            Point max = new Point(c.x + r, c.y + r, c.z + r);
            return BoundingBox.of(min, max);
        }

        if (shape instanceof Triangle tri) {
            Point v1 = tri.getV1();
            Point v2 = tri.getV2();
            Point v3 = tri.getV3();
            return BoundingBox.fromPoints(v1, v2, v3);
        }

        if (shape instanceof Disk disk) {
            Point c = disk.getCenter();
            double r = disk.getRadius();
            Point min = new Point(c.x - r, c.y - r, c.z - r);
            Point max = new Point(c.x + r, c.y + r, c.z + r);
            return BoundingBox.of(min, max);
        }

        if (shape instanceof RegularPolygon poly) {
            Point c = poly.getCenter();
            double r = poly.getRadius();
            Point min = new Point(c.x - r, c.y - r, c.z - r);
            Point max = new Point(c.x + r, c.y + r, c.z + r);
            return BoundingBox.of(min, max);
        }

        if (shape instanceof Plane) {
            // Forme infinie: pas de bornes finies
            // return BoundingBox.infinite();
        }

        // Indique que l'on ne sait pas borner proprement la forme:
        // on retourne une boîte infinie pour ne pas exclure à tort des intersections.
        return BoundingBox.infinite();
    }
}
