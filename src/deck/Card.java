package deck;

import java.util.Objects;

public class Card {
    private final CardType type;
    private final String countryName;

    public Card(CardType type) {
        this.type = type;
        this.countryName = "";
    }

    public Card(CardType type, String countryName) {
        this.type = Objects.requireNonNull(type, "CardType cannot be null");
        this.countryName = Objects.requireNonNull(countryName, "CountryName cannot be null.");
    }

    public CardType getType() {
        return type;
    }

    public String getCountryName() {
        return countryName;
    }

    @Override
    public String toString() {
        return countryName + ", " + type;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Card && ((Card) o).getType().equals(type);
    }

}
