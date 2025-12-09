package fr.ninhache.raytracer.geometry.bvh;

import fr.ninhache.raytracer.geometry.Ray;
import fr.ninhache.raytracer.math.Point;

/**
 * Représente une "boîte" alignée sur les axes (Axis-Aligned Bounding Box, AABB) utilisée par BVH.
 *
 * <p>Une AABB est définie par deux coins opposés:
 * <ul>
 *   <li><strong>min</strong>: le coin qui contient les coordonnées x, y et z les plus petites</li>
 *   <li><strong>max</strong>: le coin qui contient les coordonnées x, y et z les plus grandes</li>
 * </ul>
 *
 * <p>Ces "boîtes" sont utilisées pour:
 * <ul>
 *   <li>regrouper des formes géométriques dans une hiérarchie de volumes</li>
 *   <li>rejeter rapidement les rayons qui ne peuvent pas toucher le contenu d'un noeud de BVH</li>
 *   <li>réduire le nombre de tests d'intersection exacts avec les formes</li>
 * </ul>
 *
 * <p>
 * Cette implémentation suppose que les coordonnées x, y et z de min sont inférieures ou égales à celles de max pour chaque axe
 */
public final class BoundingBox {

    private final Point min;
    private final Point max;

    /**
     * Construit une "boîte" à partir de deux coins opposés.
     *
     * @param min coin minimal (coordonnées les plus petites sur chaque axe)
     * @param max coin maximal (coordonnées les plus grandes sur chaque axe)
     */
    private BoundingBox(Point min, Point max) {
        this.min = min;
        this.max = max;
    }

    /**
     * Crée une "boîte" à partir de deux points opposés.
     *
     * <p>Cette méthode ne réordonne pas les composantes: l'appelant est responsable
     * de fournir un point min et un point max cohérents (min.x <= max.x, etc.).
     *
     * @param min coin minimal de la boîte
     * @param max coin maximal de la boîte
     * @return une nouvelle instance de {@code BoundingBox}
     */
    public static BoundingBox of(Point min, Point max) {
        return new BoundingBox(min, max);
    }

    /**
     * Calcule la plus petite "boîte" alignée sur les axes contenant
     * l'ensemble des points fournis.
     *
     * @param pts ensemble de points à englober, doit contenir au moins un point
     * @return une "boîte" couvrant tous les points
     * @throws IllegalArgumentException si aucun point n'est fourni
     */
    public static BoundingBox fromPoints(Point... pts) {
        if (pts == null || pts.length == 0) {
            throw new IllegalArgumentException("fromPoints requires at least one point");
        }

        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double minZ = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        double maxZ = Double.NEGATIVE_INFINITY;

        for (Point p : pts) {
            minX = Math.min(minX, p.x);
            minY = Math.min(minY, p.y);
            minZ = Math.min(minZ, p.z);
            maxX = Math.max(maxX, p.x);
            maxY = Math.max(maxY, p.y);
            maxZ = Math.max(maxZ, p.z);
        }

        return new BoundingBox(new Point(minX, minY, minZ), new Point(maxX, maxY, maxZ));
    }

    /**
     * Crée une "boîte" infinie.
     *
     * <p>Cette boîte couvre tout l'espace. Elle est utilisée comme valeur sentinelle
     * lorsque les bornes d'une forme ne peuvent pas être déterminées (par exemple un plan infini)
     * ou lorsque l'on souhaite explicitement représenter un volume non borné.
     *
     * @return une "boîte" infinie
     */
    public static BoundingBox infinite() {
        return new BoundingBox(
                new Point(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY),
                new Point(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)
        );
    }

    /**
     * Calcule l'union de cette boîte avec une autre boîte.
     *
     * <p>L'union correspond à la plus petite "boîte" contenant
     * les deux volumes.
     *
     * @param other autre "boîte"
     * @return une nouvelle "boîte" contenant les deux "boîtes"
     */
    public BoundingBox union(BoundingBox other) {
        return new BoundingBox(
                new Point(Math.min(min.x, other.min.x), Math.min(min.y, other.min.y), Math.min(min.z, other.min.z)),
                new Point(Math.max(max.x, other.max.x), Math.max(max.y, other.max.y), Math.max(max.z, other.max.z))
        );
    }

    /**
     * Indique si cette boîte est infinie.
     *
     * <p>Une boîte est considérée comme infinie dès qu'une de ses composantes
     * min ou max n'est pas finie ({@code Double.isFinite} retourne false).
     *
     * @return true si au moins une composante est infinie ou NaN, false sinon
     */
    public boolean isInfinite() {
        return !Double.isFinite(min.x) || !Double.isFinite(min.y) || !Double.isFinite(min.z)
                || !Double.isFinite(max.x) || !Double.isFinite(max.y) || !Double.isFinite(max.z);
    }

    /**
     * Teste l'intersection d'un rayon avec cette "boîte".
     *
     * <p>L'algorithme utilisé est le test des "slabs", qui consiste à
     * calculer pour chaque axe l'intervalle de paramètres t pour lequel
     * le rayon se trouve à l'intérieur de la boîte, puis à intersecter
     * ces intervalles.
     *
     * @param ray   rayon à tester
     * @param tMin  borne inférieure de l'intervalle valide sur le rayon
     *              (par exemple une distance minimale déjà trouvée)
     * @param tMax  borne supérieure de l'intervalle valide sur le rayon
     * @return true si le rayon intersecte la boîte pour un t dans [tMin, tMax],
     *         false sinon
     */
    public boolean hit(Ray ray, double tMin, double tMax) {
        double ox = ray.origin().x;
        double oy = ray.origin().y;
        double oz = ray.origin().z;

        double dx = ray.direction().x;
        double dy = ray.direction().y;
        double dz = ray.direction().z;

        double invDx = 1.0 / dx;
        double invDy = 1.0 / dy;
        double invDz = 1.0 / dz;

        double tx1 = (min.x - ox) * invDx;
        double tx2 = (max.x - ox) * invDx;
        double tmin = Math.min(tx1, tx2);
        double tmax = Math.max(tx1, tx2);

        double ty1 = (min.y - oy) * invDy;
        double ty2 = (max.y - oy) * invDy;
        tmin = Math.max(tmin, Math.min(ty1, ty2));
        tmax = Math.min(tmax, Math.max(ty1, ty2));

        double tz1 = (min.z - oz) * invDz;
        double tz2 = (max.z - oz) * invDz;
        tmin = Math.max(tmin, Math.min(tz1, tz2));
        tmax = Math.min(tmax, Math.max(tz1, tz2));

        return tmax >= Math.max(tMin, tmin) && tmin <= tMax;
    }

    /**
     * Retourne le coin minimal de la boîte.
     *
     * @return point représentant les composantes minimales x, y, z
     */
    public Point getMin() {
        return min;
    }

    /**
     * Retourne le coin maximal de la boîte.
     *
     * @return point représentant les composantes maximales x, y, z
     */
    public Point getMax() {
        return max;
    }
}
