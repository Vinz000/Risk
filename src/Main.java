import common.Constants;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import shell.ShellComponent;
import shell.ShellModel;

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
        /**
         * Model Creation
         */

        final ShellModel shellModel = new ShellModel();
        ShellComponent boardLayout = new ShellComponent(shellModel);

        Scene gameScene = new Scene(boardLayout, Constants.MAP_FRAME_WIDTH + Constants.SHELL_WIDTH, Constants.MAP_FRAME_HEIGHT);
        gameScene.getStylesheets().add("util/style.css");
        primaryStage.setTitle("Risk");
        primaryStage.setScene(gameScene);
        primaryStage.show();
    }
}
