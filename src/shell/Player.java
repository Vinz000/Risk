package shell;

import javafx.scene.paint.Color;

import java.util.UUID;

public class Player {

    private final String id;
    private final String name;
    private final Color color;

    public Player(String name, Color color) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }
}
