package player;

import card.Card;
import javafx.scene.paint.Color;
import map.country.Country;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Player {
    private final String id;
    private final String name;
    private final Color color;
    private final List<Country> ownedCountries = new ArrayList<>();
    private final List<Card> hand = new ArrayList<>();
    private int reinforcement;

    public Player(String name, Color color) {
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

    public void addCard(Card card) {
        hand.add(card);
    }

    public Card removeCard() {
        Card drawCard = hand.get(0);
        hand.remove(0);

        return drawCard;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Player && ((Player) obj).id.equals(id);
    }
}
