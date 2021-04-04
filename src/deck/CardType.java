package deck;

public enum CardType {
    SOLDIER("soldier"),
    CALVARY("calvary"),
    ARTILLERY("artillery"),
    WILDCARD("wildcard");

    private final String cardTypeString;

    CardType(String cardTypeString) {
        this.cardTypeString = cardTypeString;
    }

    @Override
    public String toString() {
        return this.cardTypeString;
    }
}