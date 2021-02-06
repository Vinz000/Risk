package game;

import common.Constants;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import map.MapComponent;
import map.MapModel;
import shell.ShellComponent;
import shell.ShellModel;

/**
 * Change to however you want this to look like
 */

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
}
