package game;

import javafx.scene.layout.VBox;

/**
 * Change to however you want this to look like
 */

public class GameRoot extends VBox {
    public GameRoot() {

        MapComponent mapComponent = new MapComponent();
        // add Input pane
        // add Output pane

        this.getChildren().add(new MapComponent());
    }
}
