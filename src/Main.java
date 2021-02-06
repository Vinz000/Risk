import common.Constants;
import game.GameRoot;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

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

        Scene gameScene = new Scene(
                gameRoot,
                Constants.MAP_WIDTH + Constants.SHELL_WIDTH,
                Constants.MAP_HEIGHT
        );
        gameScene.getStylesheets().add("resources/style.css");
        primaryStage.setMinWidth(Constants.MAP_WIDTH + Constants.SHELL_WIDTH);
        primaryStage.setMinHeight(Constants.MAP_HEIGHT);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Risk");
        primaryStage.setScene(gameScene);
        primaryStage.show();
    }
}
