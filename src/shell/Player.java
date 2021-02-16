package shell;

import cards.Card;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Player {

    private final String id;
    private final String name;
    private final Color color;
    private final List<Card> hand = new ArrayList<>();

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

    public List<Card> getHand() {
        return hand;
    }

    public Card removeCard() {
        Card drawCard = hand.get(0);
        hand.remove(0);

        return drawCard;
    }
}
