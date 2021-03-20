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
    private final List<Card> cards = new ArrayList<>();
    private int reinforcements = 0;

    public Player(String name, Color color) throws IllegalArgumentException {
        assert !name.trim().isEmpty() : "Name cannot be empty";

        this.id = UUID.randomUUID().toString();
        this.name = Objects.requireNonNull(name);
        this.color = Objects.requireNonNull(color);
    }

    public int getReinforcements () {
        return reinforcements;
    }

    public void setReinforcements(int offsetReinforcements) {
        reinforcements = offsetReinforcements;
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

    // TODO: Refactor to more meaningful names
    public abstract void initReinforce();
    public abstract void reinforce();
    public abstract void combat();
    public abstract void fortify();

    public List<Card> getCards() {
        return cards;
    }

    public void addCard(Card card) {
        Objects.requireNonNull(card);

        cards.add(card);
    }

    // TODO: Change so that it removes specific card
    public Card removeTopCard() throws IllegalArgumentException {
        assert getCards().size() > 0 : "Cannot remove from empty hand";

        return cards.remove(0);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Player && ((Player) obj).id.equals(id);
    }
}
