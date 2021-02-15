package shell;

import cards.Card;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.UUID;

public class Player {

    private final String id;
    private final String name;
    private final Color color;
    private ArrayList<Card> hand;

    public Player(String name, Color color, ArrayList<Card> hand) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.color = color;
        this.hand = hand;
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

    public ArrayList<Card> getHand(){ return hand;}

}
