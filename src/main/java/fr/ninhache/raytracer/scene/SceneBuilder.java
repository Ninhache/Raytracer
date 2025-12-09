package fr.ninhache.raytracer.scene;


import fr.ninhache.raytracer.geometry.IShape;
import fr.ninhache.raytracer.lighting.DirectionalLight;
import fr.ninhache.raytracer.lighting.ILight;
import fr.ninhache.raytracer.lighting.PointLight;
import fr.ninhache.raytracer.lighting.SpotLight;
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
    private Material currentMaterial = new Material(new Color(0.2, 0.2, 0.2), new Color(0.2, 0.2, 0.2), 32.0);
    private boolean materialExplicitlySet = false;


    private final List<ILight> lights = new ArrayList<>();
    private final List<IShape> shapes = new ArrayList<>();
    private final List<Point> vertices = new ArrayList<>();
    private int maxVertices = 0;
    private int maxDepth = 1;

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
        currentMaterial = new Material(diffuse, currentMaterial.specular(), currentMaterial.shininess());
        materialExplicitlySet = true;
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
        currentMaterial = new Material(currentMaterial.diffuse(), specular, currentMaterial.shininess());
        return this;
    }

    public SceneBuilder setShininess(double shininess) {
        currentMaterial = new Material(currentMaterial.diffuse(), currentMaterial.specular(), shininess);
        materialExplicitlySet = true;
        return this;
    }

    public SceneBuilder setMaxDepth(int maxDepth) throws ParseException {
        if (maxDepth < 1) {
            throw new ParseException("maxdepth doit être >= 1");
        }
        this.maxDepth = maxDepth;
        return this;
    }

    /**
     * Valide que ambient + diffuse ≤ 1.0 sur chaque composante.
     *
     * @throws ParseException si la contrainte est violée
     */
    private void validateMaterialConstraint() throws ParseException {
        if (!materialExplicitlySet) {
            return; // la matière par défaut sera ajustée dynamiquement en fonction de l'ambiant
        }

        Color sum = ambientLight.add(currentMaterial.diffuse());

        if (sum.r() > 1.0 || sum.g() > 1.0 || sum.b() > 1.0) {
            throw new ParseException(
                    String.format(
                            """
                                    La somme ambient + diffuse dépasse 1.0 sur au moins une composante :
                                      ambient = %s
                                      diffuse = %s
                                      somme   = (%.2f, %.2f, %.2f)""",
                            ambientLight, currentMaterial.diffuse(),
                            sum.r(), sum.g(), sum.b()
                    )
            );
        }
    }

    /**
     * Ajoute une source lumineuse à la scène.
     *
     * @param light la source lumineuse
     */
    public SceneBuilder addLight(ILight light) {
        Color allowed = new Color(
                Math.max(0.0, 1.0 - totalLightIntensity.r()),
                Math.max(0.0, 1.0 - totalLightIntensity.g()),
                Math.max(0.0, 1.0 - totalLightIntensity.b())
        );

        Color clamped = new Color(
                Math.min(light.getColor().r(), allowed.r()),
                Math.min(light.getColor().g(), allowed.g()),
                Math.min(light.getColor().b(), allowed.b())
        );

        if (!clamped.equals(light.getColor())) {
            light = withColor(light, clamped);
        }

        totalLightIntensity = totalLightIntensity.add(light.getColor());
        lights.add(light);
        return this;
    }

    private ILight withColor(ILight light, Color color) {
        if (light instanceof DirectionalLight directional) {
            return new DirectionalLight(directional.getDirection(), color);
        }
        if (light instanceof PointLight point) {
            return new PointLight(point.getPosition(), color);
        }
        if (light instanceof SpotLight spot) {
            return new SpotLight(
                    spot.getPosition(),
                    spot.getDirection(),
                    spot.getConeAngleDegrees(),
                    spot.getPenumbraAngleDegrees(),
                    color
            );
        }

        // Fallback : conserve le comportement d'éclairage mais avec la couleur limitée
        return new ILight() {
            @Override
            public Color getColor() {
                return color;
            }

            @Override
            public fr.ninhache.raytracer.math.Vector incidentFrom(Point hitPoint) {
                return light.incidentFrom(hitPoint);
            }

            @Override
            public String describe() {
                return light.describe();
            }
        };
    }

    /**
     * Ajoute une forme géométrique à la scène.
     *
     * <p>La forme hérite du matériau courant (diffuse + specular).
     *
     * @param shape la forme à ajouter
     */
    public SceneBuilder addShape(IShape shape) {
        if (shape == null) {
            return this;
        }

        Material material = shape.getMaterial();
        if (material == null) {
            Material fallback = materialExplicitlySet ? currentMaterial : buildAutoMaterial();
            shape.setMaterial(fallback.copy());
        } else if (isBlack(material)) {
            // Matériau absent ou valeur par défaut noire :
            //  - si l'utilisateur a défini un matériau courant explicite, on l'applique
            //  - sinon on applique un matériau auto pour éviter un rendu noir
            Material fallback = materialExplicitlySet ? currentMaterial : buildAutoMaterial();
            shape.setMaterial(fallback.copy());
        } else {
            shape.setMaterial(material.copy());
        }

        shapes.add(shape);
        return this;
    }

    private boolean isBlack(Material mat) {
        return mat.diffuse().equals(Color.BLACK) && mat.specular().equals(Color.BLACK);
    }

    private Material buildAutoMaterial() {
        double maxR = Math.max(0.0, 1.0 - ambientLight.r());
        double maxG = Math.max(0.0, 1.0 - ambientLight.g());
        double maxB = Math.max(0.0, 1.0 - ambientLight.b());

        double autoR = Math.min(0.2, maxR);
        double autoG = Math.min(0.2, maxG);
        double autoB = Math.min(0.2, maxB);

        return new Material(new Color(autoR, autoG, autoB), currentMaterial.specular(), currentMaterial.shininess());
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
                lights, shapes, maxDepth
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
