package game;

import common.Constants;
import common.Validators;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import map.MapComponent;
import map.MapModel;
import shell.*;


public class GameRoot extends BorderPane {

    // Create models
    private final ShellModel shellModel = new ShellModel();
    private final MapModel mapModel = new MapModel();

    public GameRoot() {
        setId(Constants.ComponentIds.GAME_ROOT);

        // Create top-level components
        MapComponent mapComponent = new MapComponent(mapModel);
        ShellComponent shellComponent = new ShellComponent(shellModel, mapModel);
        mapModel.addObserver(mapComponent);
        shellModel.addObserver(shellComponent);
        shellModel.addObserver(mapComponent);

        // BorderPane configuration
        setRight(shellComponent);
        setCenter(mapComponent);
        setMargin(shellComponent, new Insets(10));
        setMargin(mapComponent, new Insets(10, 0, 10, 10));
    }

    public void start() {
        shellModel.notify(Constants.Notifications.WELCOME);

        /*
         * Getting player names
         */

        shellModel.notify(Constants.Notifications.NAME + "(P1)");
        ShellPrompt playerOnePrompt = new ShellPrompt(input -> {
            Player playerOne = new Player(input, Color.valueOf("#710193"));
            mapModel.addPlayer(playerOne);

            // Send message for next prompt
            shellModel.notify(Constants.Notifications.NAME + "(P2)");

        }, Validators.nonEmpty);
        shellModel.prompt(playerOnePrompt);

        ShellPrompt playerTwoPrompt = new ShellPrompt(input -> {
            Player playerTwo = new Player(input, Color.valueOf("#fee227"));
            mapModel.addPlayer(playerTwo);

            // We have player info now, so
            // initialise the map!
            mapModel.initializeGame();
        }, Validators.nonEmpty);
        shellModel.prompt(playerTwoPrompt);

        /*
         * Whatever comes after we get player names
         */

    }
}
