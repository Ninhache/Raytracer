package fr.ninhache.raytracer.lighting;

import fr.ninhache.raytracer.math.Color;

/**
 * Interface représentant une source lumineuse dans la scène.
 *
 * <p>Deux types principaux de lumières sont supportés :
 * <ul>
 *   <li>{@link DirectionalLight} : lumière directionnelle (soleil)</li>
 *   <li>{@link PointLight} : lumière ponctuelle (ampoule, bougie)</li>
 * </ul>
 *
 * <h2>Utilisation en raytracing</h2>
 * <p>Les lumières contribuent à l'illumination des surfaces selon différents modèles :
 * <ul>
 *   <li><strong>Diffuse (Lambert)</strong> : intensité ∝ cos(θ) où θ = angle entre normale et direction lumière</li>
 *   <li><strong>Spéculaire (Phong)</strong> : reflets brillants dépendant de l'angle de vue</li>
 * </ul>
 */
public interface ILight {

    /**
     * @return la couleur/intensité de cette source lumineuse
     */
    Color getColor();

    /**
     * @return une description textuelle de la lumière (pour débogage)
     */
    String describe();
}
