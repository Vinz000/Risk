package game;

import common.Constants;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Brings together top-level components
 * to create the game itself.
 */
public class Game extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        MapComponent mapComponent = new MapComponent();
        Scene scene = new Scene(mapComponent, Constants.FRAME_WIDTH, Constants.FRAME_HEIGHT);
        scene.getStylesheets().add("util/style.css");
        primaryStage.setTitle("Risk");
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
