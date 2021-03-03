package player;

import javafx.scene.paint.Color;

import java.util.UUID;

public class HumanPlayer extends Player {
    private final String id;

    public HumanPlayer(String name, Color color) {
        super(name, color);
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof HumanPlayer && ((HumanPlayer) obj).getId().equals(id);
    }
}
