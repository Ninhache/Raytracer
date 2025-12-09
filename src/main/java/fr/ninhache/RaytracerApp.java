package fr.ninhache;

import fr.ninhache.ui.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Point d'entrée JavaFX du raytracer.
 * <p>
 * Ne fait que :
 * - créer la fenêtre
 * - instancier la vue principale
 */
public class RaytracerApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        MainView root = new MainView();

        Scene scene = new Scene(root, 1200, 800);

        stage.setTitle("Raytracer");
        stage.setScene(scene);
        stage.show();
    }
}
