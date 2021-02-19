package player;

import cards.Card;
import javafx.scene.paint.Color;
import map.Country;

import java.util.ArrayList;
import java.util.List;

public abstract class Player {
    private final String name;
    private final Color color;
    private final List<Country> ownedCountries = new ArrayList<>();
    private final List<Card> hand = new ArrayList<>();
    private int reinforcement;

    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public void addCountry(Country country) {
        ownedCountries.add(country);
    }

    public List<Country> getOwnedCountries() {
        return ownedCountries;
    }

    public void updateReinforcement(int armyOffset) {
        reinforcement += armyOffset;
    }

    public int getReinforcement() {
        return reinforcement;
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
