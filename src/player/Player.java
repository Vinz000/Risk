package player;

import deck.Card;
import javafx.scene.paint.Color;
import map.country.Country;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public abstract class Player {
    private final String id;
    private final String name;
    private final Color color;
    private final List<Country> ownedCountries = new ArrayList<>();
    private final List<Card> hand = new ArrayList<>();

    public Player(String name, Color color) throws IllegalArgumentException {
        this.id = UUID.randomUUID().toString();
        this.name = Objects.requireNonNull(name);
        this.color = Objects.requireNonNull(color);

        assert !name.trim().isEmpty() : "Name cannot be empty";
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public void addCountry(Country country) {
        Objects.requireNonNull(country);

        ownedCountries.add(country);
    }

    public List<Country> getOwnedCountries() {
        return ownedCountries;
    }

    // TODO: Change Method (Should return calculated reinforcement)
    public int getReinforcement() {
        return 0;
    }

    public List<Card> getHand() {
        return hand;
    }

    public void addCard(Card card) {
        Objects.requireNonNull(card);

        hand.add(card);
    }

    // TODO: Change so that it removes specific card
    public Card removeCard() throws IllegalArgumentException {
        assert getHand().size() > 0 : "Cannot remove from empty hand";

        return hand.remove(0);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Player && ((Player) obj).id.equals(id);
    }
}
