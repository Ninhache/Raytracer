package fr.ninhache.raytracer.math;

/**
 * Implémentation concrète de {@link AbstractVec3} utilisée uniquement pour les tests unitaires.
 *
 * <p>Cette classe expose publiquement certaines méthodes protégées (comme le produit de Schur)
 * pour faciliter leur vérification dans les tests.
 *
 * <p><strong>Ne pas utiliser en production.</strong> Utiliser les classes métier appropriées
 * comme {@code Vector}, {@code Point}, etc.
 */

public final class TestVec extends AbstractVec3 {
    /**
     * Construit un vecteur de test avec les composantes spécifiées.
     *
     * @param x composante x
     * @param y composante y
     * @param z composante z
     */
    public TestVec(double x, double y, double z) {
        super(x, y, z);
    }

    @Override
    protected TestVec ofSameType(double x, double y, double z) {
        return new TestVec(x, y, z);
    }

    /**
     * Expose publiquement le produit de Schur pour les tests.
     *
     * <p>Cette méthode est un wrapper autour de {@link #schur(AbstractVec3)} qui permet
     * de tester la logique du produit composante par composante.
     *
     * @param other l'autre vecteur de test
     * @return le produit de Schur (multiplication composante par composante)
     */
    public TestVec schurPublic(TestVec other) {
        return (TestVec) schur(other);
    }

}
