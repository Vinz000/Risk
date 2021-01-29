package game;

import javafx.scene.layout.VBox;
import map.MapComponent;

/**
 * Change to however you want this to look like
 */

public class GameRoot extends VBox {
    public GameRoot() {

        MapComponent mapComponent = new MapComponent();

        this.getChildren().add(mapComponent);
    }
}
