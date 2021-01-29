package game;

import common.Constants;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
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

        StackPane root = new StackPane();
        Scene scene = new Scene(root, Constants.FRAME_WIDTH, Constants.FRAME_HEIGHT);

        primaryStage.setTitle("Risk");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
