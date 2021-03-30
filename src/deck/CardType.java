package deck;
import java.lang.reflect.WildcardType;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public enum CardType{
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