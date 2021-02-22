package player;

import card.Card;
import javafx.scene.paint.Color;
import map.country.Country;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Player {
    private final String id;
    private final String name;
    private final Color color;
    private final List<Country> ownedCountries = new ArrayList<>();
    private final List<Card> hand = new ArrayList<>();
    private int reinforcement;

    public Player(String name, Color color) throws IllegalArgumentException {
        if (isNull(name) || name.trim().isEmpty() || isNull(color)) {
            throw new IllegalArgumentException("Illegal parameters");
        }

        this.id = UUID.randomUUID().toString();
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
        if (isNull(country)) {
            throw new IllegalArgumentException("Country is Null");
        }
        ownedCountries.add(country);
    }

    public List<Country> getOwnedCountries() {
        return ownedCountries;
    }

    public void updateReinforcement(int armyOffset) {
        if (reinforcement + armyOffset < 0) {
            throw new IllegalArgumentException("Reinforcement should never be less than 0");
        }
        reinforcement += armyOffset;
    }

    public int getReinforcement() {
        return reinforcement;
    }

    public List<Card> getHand() {
        return hand;
    }

    public void addCard(Card card) {
        if (isNull(card) ) {
            throw new IllegalArgumentException("Card is null");
        }
        hand.add(card);
    }

    public Card removeCard() throws IllegalArgumentException{
        if (hand.size() == 0) {
            throw new IllegalArgumentException("Cannot remove card");
        }
        return hand.remove(0);
    }

    private boolean isNull(Object obj) {
        return obj == null;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Player && ((Player) obj).id.equals(id);
    }
}
