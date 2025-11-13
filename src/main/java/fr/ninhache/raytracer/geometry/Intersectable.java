package fr.ninhache.raytracer.geometry;

import java.util.Optional;

/**
 * Contrat pour les objets géométriques pouvant être intersectés par un rayon.
 */
public interface Intersectable {

    /**
     * Calcule la première intersection entre cet objet et un rayon.
     *
     * <p>Si le rayon ne coupe pas l'objet, la méthode renvoie {@link Optional#empty()}.
     * Sinon, elle renvoie la plus proche intersection "devant" l'origine du rayon.
     *
     * @param ray le rayon incident (ne doit pas être null)
     * @return une intersection optionnelle
     */
    Optional<Intersection> intersect(Ray ray);
}
