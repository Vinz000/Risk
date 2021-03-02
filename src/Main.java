import game.GameComponent;
import game.GameCore;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import static common.Constants.*;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        // Create UI components
        GameComponent gameComponent = new GameComponent();
        Scene gameScene = new Scene(
                gameComponent,
                MAP_WIDTH + SHELL_WIDTH,
                MAP_HEIGHT
        );
        gameScene.getStylesheets().add(Paths.STYLE_SHEET);

        // Stage configuration
        primaryStage.getIcons().add(new Image(Paths.ICON));
        primaryStage.setMinWidth(MAP_WIDTH + SHELL_WIDTH);
        primaryStage.setMinHeight(MAP_HEIGHT);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Risk");
        primaryStage.setScene(gameScene);
        primaryStage.show();

        // Start
        GameCore gameCore = new GameCore();
        gameCore.start();
    }
}
