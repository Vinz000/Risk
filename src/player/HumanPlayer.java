package player;

import javafx.scene.paint.Color;
import map.CountryNode;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HumanPlayer extends Player {

    private final String id;
    private int reinforcement = 0;
//    private final Deck deck;
    private final List<CountryNode> countryNodeList = new ArrayList<>();

    public HumanPlayer(String name, Color color) {
        super(name, color);
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

}
