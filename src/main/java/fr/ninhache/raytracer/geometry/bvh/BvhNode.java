package fr.ninhache.raytracer.geometry.bvh;

import fr.ninhache.raytracer.geometry.Intersection;
import fr.ninhache.raytracer.geometry.IShape;
import fr.ninhache.raytracer.geometry.Ray;
import fr.ninhache.raytracer.math.Epsilon;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Noeud d'une hiérarchie de volumes englobants (Bounding Volume Hierarchy, BVH).
 *
 * <p>Un noeud de BVH représente un sous-ensemble de la scène:
 * <ul>
 *   <li><strong>Feuille</strong>: contient une forme unique ({@link #shape}) et sa boîte englobante</li>
 *   <li><strong>Noeud interne</strong>: contient deux enfants ({@link #left} et {@link #right})
 *       et une boîte englobante qui recouvre la union des boîtes de ses enfants</li>
 * </ul>
 *
 * <p>La BVH permet d'accélérer les tests d'intersection rayon-scène en éliminant rapidement
 * des régions entières de l'espace: si un rayon ne coupe pas la boîte englobante d'un noeud,
 * il est inutile de tester les formes contenues dans ce noeud.
 */
public final class BvhNode {

    private final BoundingBox bounds;
    private final BvhNode left;
    private final BvhNode right;
    private final IShape shape;

    /**
     * Construit un noeud de BVH.
     *
     * @param bounds boîte englobante du noeud (peut être null si le noeud ne doit pas être testé)
     * @param left   fils gauche, ou null si noeud feuille
     * @param right  fils droit, ou null si noeud feuille
     * @param shape  forme contenue si noeud feuille, sinon null pour un noeud interne
     */
    private BvhNode(BoundingBox bounds, BvhNode left, BvhNode right, IShape shape) {
        this.bounds = bounds;
        this.left = left;
        this.right = right;
        this.shape = shape;
    }

    /**
     * Construit récursivement une BVH à partir d'une liste de formes et de leurs boîtes englobantes.
     *
     * <p>Algorithme utilisé:
     * <ol>
     *   <li>Si la liste est vide, retourne null.</li>
     *   <li>Si la liste contient une seule forme, crée un noeud feuille.</li>
     *   <li>Sinon:
     *     <ol>
     *       <li>Calcule la "boîte" globale de toutes les formes.</li>
     *       <li>Choisit l'axe le plus étendu (x, y ou z).</li>
     *       <li>Trie les formes selon la position de leur "boîte" sur cet axe.</li>
     *       <li>Scinde la liste en deux sous-listes et construit récursivement les noeuds enfants.</li>
     *       <li>Crée un noeud interne dont la "boîte" est l'union de celles des deux enfants.</li>
     *     </ol>
     *   </li>
     * </ol>
     *
     * <p>Si un découpage dégénéré est détecté (une des deux sous-listes est vide),
     * la méthode crée un noeud feuille à partir de la première forme pour éviter une récursion
     * non productive.
     *
     * @param shapes liste des formes et de leurs "boîtes"
     * @return racine de la BVH construite, ou null si la liste est vide
     */
    public static BvhNode build(List<ShapeBounds> shapes) {
        if (shapes == null || shapes.isEmpty()) {
            return null;
        }

        if (shapes.size() == 1) {
            ShapeBounds sb = shapes.get(0);
            return new BvhNode(sb.bounds(), null, null, sb.shape());
        }

        BoundingBox global = shapes.get(0).bounds();
        for (int i = 1; i < shapes.size(); i++) {
            global = global.union(shapes.get(i).bounds());
        }

        double dx = global.getMax().x - global.getMin().x;
        double dy = global.getMax().y - global.getMin().y;
        double dz = global.getMax().z - global.getMin().z;

        Comparator<ShapeBounds> comparator;
        if (dx >= dy && dx >= dz) {
            comparator = Comparator.comparingDouble(sb -> sb.bounds().getMin().x + sb.bounds().getMax().x);
        } else if (dy >= dx && dy >= dz) {
            comparator = Comparator.comparingDouble(sb -> sb.bounds().getMin().y + sb.bounds().getMax().y);
        } else {
            comparator = Comparator.comparingDouble(sb -> sb.bounds().getMin().z + sb.bounds().getMax().z);
        }

        List<ShapeBounds> sorted = new ArrayList<>(shapes);
        sorted.sort(comparator);
        int mid = sorted.size() / 2;

        List<ShapeBounds> leftShapes = sorted.subList(0, mid);
        List<ShapeBounds> rightShapes = sorted.subList(mid, sorted.size());

        // garde contre les splits dégénérés
        if (leftShapes.isEmpty() || rightShapes.isEmpty()) {
            ShapeBounds sb = sorted.get(0);
            return new BvhNode(sb.bounds(), null, null, sb.shape());
        }

        BvhNode left = build(leftShapes);
        BvhNode right = build(rightShapes);
        BoundingBox nodeBounds = left.bounds.union(right.bounds);

        return new BvhNode(nodeBounds, left, right, null);
    }

    /**
     * Teste l'intersection d'un rayon avec ce noeud de BVH et, récursivement, avec ses descendants.
     *
     * <p>Le test suit la logique suivante:
     * <ol>
     *   <li>Si le noeud possède une boîte englobante, on teste d'abord l'intersection
     *       du rayon avec cette boîte (en utilisant un t minimal {@link Epsilon#EPS} pour
     *       éviter les auto-intersections).</li>
     *   <li>Si le rayon ne coupe pas la boîte, on peut rejeter le noeud entièrement.</li>
     *   <li>Si le noeud est une feuille (shape non null), on délègue le test
     *       à la forme, via {@link IShape#intersect(Ray)}.</li>
     *   <li>Sinon, on propage le test aux enfants gauche et droit, en mettant
     *       à jour la meilleure distance trouvée pour limiter les tests ultérieurs.</li>
     * </ol>
     *
     * @param ray         rayon à tester
     * @param currentBest distance du meilleur point d'intersection déjà connu, utilisée
     *                    pour couper plus tôt les branches qui ne peuvent pas fournir
     *                    d'intersection plus proche
     * @return un {@link Optional} contenant l'intersection la plus proche trouvée
     *         dans ce sous-arbre et plus proche que currentBest, ou {@link Optional#empty()}
     *         si aucune intersection n'est détectée
     */
    public Optional<Intersection> intersect(Ray ray, double currentBest) {
        if (bounds != null && !bounds.hit(ray, Epsilon.EPS, currentBest)) {
            return Optional.empty();
        }

        if (shape != null) {
            return shape.intersect(ray);
        }

        Intersection hit = null;
        double bestT = currentBest;

        if (left != null) {
            Optional<Intersection> lh = left.intersect(ray, bestT);
            if (lh.isPresent()) {
                hit = lh.get();
                bestT = hit.t();
            }
        }

        if (right != null) {
            Optional<Intersection> rh = right.intersect(ray, bestT);
            if (rh.isPresent()) {
                Intersection rHit = rh.get();
                if (hit == null || rHit.t() < bestT) {
                    hit = rHit;
                    bestT = rHit.t();
                }
            }
        }

        return Optional.ofNullable(hit);
    }

    /**
     * Retourne la "boîte" associée à ce noeud.
     *
     * @return la "boîte" du noeud, ou null si le noeud n'en possède pas
     */
    public BoundingBox getBounds() {
        return bounds;
    }

    /**
     * Couple une forme et sa "boîte" pré-calculée.
     *
     * <p>Ce type est utilisé en entrée de {@link #build(List)} pour éviter
     * de recalculer les "boîtes" lors de la construction de la BVH.
     *
     * @param shape  forme géométrique
     * @param bounds "boîte" associée à la forme
     */
    public record ShapeBounds(IShape shape, BoundingBox bounds) {
    }
}
