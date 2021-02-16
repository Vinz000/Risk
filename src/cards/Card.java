package cards;

public class Card {
    private final CardType type;
    private final String countryName;

    public Card(CardType type, String countryName) {
        this.type = type;
        this.countryName = countryName;
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
}
