package fr.ninhache.raytracer.scene;


import fr.ninhache.raytracer.geometry.IShape;
import fr.ninhache.raytracer.lighting.ILight;
import fr.ninhache.raytracer.scene.exception.ParseException;
import fr.ninhache.raytracer.math.Color;
import fr.ninhache.raytracer.math.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Constructeur progressif de scène (pattern Builder).
 *
 * <p>Cette classe permet de construire une {@link Scene} étape par étape
 * tout en validant les contraintes métier :
 * <ul>
 *   <li>Vérification que ambient + diffuse ≤ 1.0 pour chaque composante</li>
 *   <li>Vérification que la somme des intensités lumineuses ≤ 1.0</li>
 *   <li>Validation de la cohérence des données (caméra obligatoire, taille > 0, etc.)</li>
 * </ul>
 *
 * <h2>Exemple d'utilisation</h2>
 * <pre>{@code
 * SceneBuilder builder = new SceneBuilder();
 * builder.setSize(640, 480);
 * builder.setOutputFilename("scene1.png");
 * builder.setCamera(camera);
 * builder.setAmbientLight(new Color(0.1, 0.1, 0.1));
 * builder.addLight(light);
 * builder.addShape(sphere);
 *
 * Scene scene = builder.build(); // Validation finale
 * }</pre>
 */
public class SceneBuilder {
    private int width;
    private int height;
    private String outputFilename = "output.png";
    private Camera camera;


    private Color ambientLight = Color.BLACK;
    private Color totalLightIntensity = Color.BLACK;
    private Material currentMaterial = new Material();


    private final List<ILight> lights = new ArrayList<>();
    private final List<IShape> shapes = new ArrayList<>();
    private final List<Point> vertices = new ArrayList<>();
    private int maxVertices = 0;

    /**
     * Définit la taille de l'image à générer.
     *
     * @param width largeur en pixels (doit être > 0)
     * @param height hauteur en pixels (doit être > 0)
     * @throws ParseException si les dimensions sont invalides
     */
    public SceneBuilder setSize(int width, int height) throws ParseException {
        if (width <= 0 || height <= 0) {
            throw new ParseException(
                    String.format("Dimensions invalides : %dx%d (doivent être > 0)", width, height)
            );
        }
        this.width = width;
        this.height = height;

        return this;
    }

    /**
     * Définit le nom du fichier de sortie.
     *
     * @param filename nom du fichier (ex: "scene1.png")
     */
    public SceneBuilder setOutputFilename(String filename) {
        this.outputFilename = filename != null ? filename : "output.png";
        return this;
    }

    /**
     * Définit la caméra de la scène.
     *
     * @param camera la caméra
     */
    public SceneBuilder setCamera(Camera camera) {
        this.camera = camera;
        return this;
    }

    /**
     * Définit la couleur de la lumière ambiante.
     *
     * @param ambient couleur ambiante (composantes entre 0 et 1)
     * @throws ParseException si les composantes sont invalides
     */
    public SceneBuilder setAmbientLight(Color ambient) throws ParseException {
        validateColorRange(ambient, "ambient");
        this.ambientLight = ambient;
        validateMaterialConstraint();
        return this;
    }

    /**
     * Définit le matériau diffus pour les prochaines formes.
     *
     * @param diffuse couleur diffuse
     * @throws ParseException si invalide ou si ambient+diffuse > 1
     */
    public SceneBuilder setDiffuse(Color diffuse) throws ParseException {
        validateColorRange(diffuse, "diffuse");
        currentMaterial = new Material(diffuse, currentMaterial.getSpecular(), currentMaterial.getShininess());
        validateMaterialConstraint();
        return this;
    }

    /**
     * Définit le matériau spéculaire pour les prochaines formes.
     *
     * @param specular couleur spéculaire
     * @throws ParseException si les composantes sont invalides
     */
    public SceneBuilder setSpecular(Color specular) throws ParseException {
        validateColorRange(specular, "specular");
        currentMaterial = new Material(currentMaterial.getDiffuse(), specular, currentMaterial.getShininess());
        return this;
    }

    public SceneBuilder setShininess(double shininess) throws ParseException {
        if (shininess < 0 || shininess > 100) {
            throw new ParseException("shininess doit être compris entre 0 et 100");
        }

        currentMaterial = new Material(currentMaterial.getDiffuse(), currentMaterial.getSpecular(), shininess);
        return this;
    }



    /**
     * Valide que ambient + diffuse ≤ 1.0 sur chaque composante.
     *
     * @throws ParseException si la contrainte est violée
     */
    private void validateMaterialConstraint() throws ParseException {
        Color sum = ambientLight.add(currentMaterial.getDiffuse());

        if (sum.r() > 1.0 || sum.g() > 1.0 || sum.b() > 1.0) {
            throw new ParseException(
                    String.format(
                            "La somme ambient + diffuse dépasse 1.0 sur au moins une composante :\n" +
                                    "  ambient = %s\n" +
                                    "  diffuse = %s\n" +
                                    "  somme   = (%.2f, %.2f, %.2f)",
                            ambientLight, currentMaterial.getDiffuse(),
                            sum.r(), sum.g(), sum.b()
                    )
            );
        }
    }

    /**
     * Ajoute une source lumineuse à la scène.
     *
     * @param light la source lumineuse
     * @throws ParseException si la somme des intensités dépasse 1.0
     */
    public SceneBuilder addLight(ILight light) throws ParseException {
        Color newTotal = totalLightIntensity.add(light.getColor());

        if (newTotal.r() > 1.0 || newTotal.g() > 1.0 || newTotal.b() > 1.0) {
            throw new ParseException(
                    String.format(
                            "La somme des intensités lumineuses dépasse 1.0 :\n" +
                                    "  total actuel = %s\n" +
                                    "  nouvelle lumière = %s\n" +
                                    "  nouveau total = (%.2f, %.2f, %.2f)",
                            totalLightIntensity, light.getColor(),
                            newTotal.r(), newTotal.g(), newTotal.b()
                    )
            );
        }

        totalLightIntensity = newTotal;
        lights.add(light);
        return this;
    }

    /**
     * Ajoute une forme géométrique à la scène.
     *
     * <p>La forme hérite du matériau courant (diffuse + specular).
     *
     * @param shape la forme à ajouter
     */
    public SceneBuilder addShape(IShape shape) {
        shape.setMaterial(currentMaterial.copy());
        shapes.add(shape);
        return this;
    }

    /**
     * Définit le nombre maximum de vertices attendus.
     *
     * @param maxVertices nombre maximal de vertices
     * @throws ParseException si déjà défini ou invalide
     */
    public SceneBuilder setMaxVertices(int maxVertices) throws ParseException {
        if (this.maxVertices > 0) {
            throw new ParseException("maxverts déjà défini");
        }
        if (maxVertices <= 0) {
            throw new ParseException("maxverts doit être > 0");
        }
        this.maxVertices = maxVertices;
        return this;
    }

    /**
     * Ajoute un vertex à la liste.
     *
     * @param vertex le point à ajouter
     * @throws ParseException si le nombre de vertices dépasse maxVertices
     */
    public SceneBuilder addVertex(Point vertex) throws ParseException {
        if (maxVertices == 0) {
            throw new ParseException("maxverts doit être défini avant de déclarer des vertices");
        }
        if (vertices.size() >= maxVertices) {
            throw new ParseException(
                    String.format("Nombre de vertices (%d) dépasse maxverts (%d)",
                            vertices.size() + 1, maxVertices)
            );
        }
        vertices.add(vertex);
        return this;
    }

    /**
     * Récupère un vertex par son indice.
     *
     * @param index indice du vertex (commence à 0)
     * @return le point correspondant
     * @throws ParseException si l'indice est invalide
     */
    public Point getVertex(int index) throws ParseException {
        if (index < 0 || index >= vertices.size()) {
            throw new ParseException(
                    String.format("Indice de vertex invalide : %d (valides : 0-%d)",
                            index, vertices.size() - 1)
            );
        }
        return vertices.get(index);
    }

    /**
     * Valide les contraintes et construit la scène finale.
     *
     * @return la scène construite
     * @throws ParseException si des éléments obligatoires manquent
     */
    public Scene build() throws ParseException {
        // Validation des éléments obligatoires
        if (width == 0 || height == 0) {
            throw new ParseException("Taille d'image non définie (manque 'size')");
        }

        if (camera == null) {
            throw new ParseException("Caméra non définie (manque 'camera')");
        }

        // Construction de la scène
        return new Scene(
                width, height, outputFilename,
                camera, ambientLight,
                lights, shapes
        );
    }

    /**
     * Valide qu'une couleur a toutes ses composantes dans [0, 1].
     *
     * @param color la couleur à valider
     * @param name nom de la propriété (pour les messages d'erreur)
     * @throws ParseException si une composante est hors limites
     */
    private void validateColorRange(Color color, String name) throws ParseException {
        if (color.r() < 0 || color.r() > 1 ||
                color.g() < 0 || color.g() > 1 ||
                color.b() < 0 || color.b() > 1) {
            throw new ParseException(
                    String.format("%s doit avoir toutes ses composantes dans [0, 1] : %s",
                            name, color)
            );
        }
    }

    /**
     * @return le matériau courant (pour les tests)
     */
    public Material getCurrentMaterial() {
        return currentMaterial;
    }
}