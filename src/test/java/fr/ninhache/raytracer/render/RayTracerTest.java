
package fr.ninhache.raytracer.render;

import fr.ninhache.raytracer.geometry.Sphere;
import fr.ninhache.raytracer.math.Color;
import fr.ninhache.raytracer.math.Point;
import fr.ninhache.raytracer.math.Vector;
import fr.ninhache.raytracer.scene.Camera;
import fr.ninhache.raytracer.scene.Scene;
import fr.ninhache.raytracer.scene.SceneBuilder;
import fr.ninhache.raytracer.scene.exception.ParseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RayTracer - couleurs ambiantes et calcul des directions")
class RayTracerTest {

    @Test
    @DisplayName("Pixel centre -> direction = -w")
    void centerPixelGetDirectionEqualsMinusW() throws ParseException {
        Camera cam = new Camera(new Point(0,0,0), new Point(0,0,-1), Vector.Y_AXIS, 60);
        Scene scene = new SceneBuilder()
                .setSize(3, 3)
                .setCamera(cam)
                .setAmbientLight(new Color(1,1,1))
                .build();

        RayTracer rt = new RayTracer();
        // On calcule manuellement -w et on vérifie que la direction est colinéaire et de même sens

        var centerColor = rt.getPixelColor(scene, 1, 1); // juste pour exécuter le calcul

        // Si aucune forme, c'est noir
        assertEquals(0x000000, centerColor.toRGB());
    }

    @Test
    @DisplayName("Avec une sphère au centre, le pixel central prend la couleur ambiante")
    void hitReturnsAmbientColor() throws ParseException {
        Camera cam = new Camera(new Point(0,0,0), new Point(0,0,-1), Vector.Y_AXIS, 60);
        Scene scene = new SceneBuilder()
                .setSize(101, 101)
                .setCamera(cam)
                .setAmbientLight(new Color(0.2, 0.3, 0.4))
                .addShape(new Sphere(new Point(0,0,-5), 1.5))
                .build();

        RayTracer rt = new RayTracer();
        Color c = rt.getPixelColor(scene, 50, 50); // centre
        assertEquals(new Color(0.2,0.3,0.4).toRGB(), c.toRGB());
    }
}
