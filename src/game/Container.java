package game;

import javafx.scene.layout.VBox;

/**
 * Change to however you want this to look like
 */

public class Container extends VBox {
    public Container() {
        MapComponent mapComponent = new MapComponent();
        // add Input pane
        // add Output pane
        this.getChildren().add(new MapComponent());
    }
}
