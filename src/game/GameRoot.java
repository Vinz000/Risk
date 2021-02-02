package game;

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

        // Create top-level components
        MapComponent mapComponent = new MapComponent(mapModel);
        ShellComponent shellComponent = new ShellComponent(shellModel);
        mapModel.addObserver(mapComponent);
        shellModel.addObserver(shellComponent);

        // BorderPane configuration
        final Insets componentInsets = new Insets(5);
        setLeft(shellComponent);
        setCenter(mapComponent);
        setMargin(mapComponent, componentInsets);
        setMargin(shellComponent, componentInsets);
    }
}
