package deck;

import org.w3c.dom.Element;

import java.util.Optional;

public enum CardType {
    SOLDIER("soldier"),
    CALVARY("calvary"),
    ARTILLERY("artillery"),
    WILDCARD("wildcard");

    public final String cardTypeString;

    CardType(String cardTypeString) {
        this.cardTypeString = cardTypeString;
    }

    public static Optional<CardType> fromString(String name) {
        for (CardType e : values()) {
            if (e.cardTypeString.contains(name.toLowerCase())) {
                return Optional.of(e);
            }
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        return this.cardTypeString;
    }
}