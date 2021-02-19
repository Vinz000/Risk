package game;

import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import map.MapComponent;
import shell.ShellComponent;
import shell.ShellModel;

import static common.Constants.ComponentIds;

public class GameComponent extends BorderPane {

    public GameComponent() {
        setId(ComponentIds.GAME_ROOT);

        // Create top-level components
        MapComponent mapComponent = new MapComponent();
        ShellComponent shellComponent = new ShellComponent();

        // BorderPane configuration
        setRight(shellComponent);
        setCenter(mapComponent);
        setMargin(shellComponent, new Insets(10));
        setMargin(mapComponent, new Insets(10, 0, 10, 10));
    }
}
