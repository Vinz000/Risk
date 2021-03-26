package game;

import common.BaseComponent;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import map.component.MapComponent;
import shell.component.ShellComponent;

import static common.Constants.ComponentIds;

public class GameComponent extends BorderPane {

    public GameComponent() {
        BaseComponent.build(this::build, this::setCssId);
    }

    public void build() {
        // Create top-level components
        MapComponent mapComponent = new MapComponent();
        ShellComponent shellComponent = new ShellComponent();

        // BorderPane configuration
        setRight(shellComponent);
        setCenter(mapComponent);
        setMargin(shellComponent, new Insets(10));
        setMargin(mapComponent, new Insets(10, 0, 10, 10));
    }

    public void setCssId() {
        setId(ComponentIds.GAME_ROOT);
    }
}
