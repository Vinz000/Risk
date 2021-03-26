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


    @Override
    public String toString() {
        return this.cardTypeString;
    }
}