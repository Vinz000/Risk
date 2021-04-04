package deck;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum CardSet {
    THREE_ARTILLERY("artillery", new Card(CardType.ARTILLERY), new Card(CardType.ARTILLERY), new Card(CardType.ARTILLERY)),
    THREE_CALVARY("calvary", new Card(CardType.CALVARY), new Card(CardType.CALVARY), new Card(CardType.CALVARY)),
    THREE_SOLDIER("soldier", new Card(CardType.SOLDIER), new Card(CardType.SOLDIER), new Card(CardType.SOLDIER)),
    ONE_OF_EACH("mixed", new Card(CardType.CALVARY), new Card(CardType.SOLDIER), new Card(CardType.ARTILLERY)),
    ONE_WILDCARD("wild", new Card(CardType.WILDCARD)),
    TWO_WILDCARDS("wild", new Card(CardType.WILDCARD), new Card(CardType.WILDCARD));

    public final String cardSetName;
    public final List<Card> cards;

    CardSet(String cardSetName, Card... cards) {
        this.cardSetName = cardSetName;
        this.cards = Arrays.asList(cards);
    }

    @Override
    public String toString() {
        return this.cardSetName;
    }

    public boolean matches(List<Card> cardsToInspect) {
        return containsAll(cardsToInspect, cards);
    }

    public static boolean containsAll(List<Card> cardsSelected, List<Card> cards) {
        List<Card> cardsClone = new ArrayList<>(cardsSelected);

        for (Card card : cards) {
            if (!cardsClone.remove(card)) return false;
        }

        return true;
    }

}
