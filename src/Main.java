import game.GameRoot;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import static common.Constants.*;

public class Main extends Application {

    /**
     * Application entry point.
     *
     * @param args TODO: what arguments will we allow for game configuration?
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        GameRoot gameRoot = new GameRoot();
        primaryStage.getIcons().add(new Image(Paths.ICON));

        Scene gameScene = new Scene(
                gameRoot,
                MAP_WIDTH + SHELL_WIDTH,
                MAP_HEIGHT
        );

        gameScene.getStylesheets().add(Paths.STYLE_SHEET);
        primaryStage.setMinWidth(MAP_WIDTH + SHELL_WIDTH);
        primaryStage.setMinHeight(MAP_HEIGHT);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Risk");
        primaryStage.setScene(gameScene);
        primaryStage.show();

        // Start the game!
        gameRoot.start();
    }
}
