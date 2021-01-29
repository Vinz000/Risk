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
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(new GameRoot(), Constants.FRAME_WIDTH, Constants.FRAME_HEIGHT);
        scene.getStylesheets().add("util/style.css");
        stage.setTitle("Risk");
        stage.setScene(scene);
        stage.show();
    }
}
