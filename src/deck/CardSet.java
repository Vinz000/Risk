package deck;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public enum CardSet {
    THREE_ARTILLERY("artillery", CardSet.atLeastThreeOfType(CardType.ARTILLERY)),
    THREE_CALVARY("calvary", CardSet.atLeastThreeOfType(CardType.CALVARY)),
    THREE_SOLDIER("soldier", CardSet.atLeastThreeOfType(CardType.SOLDIER)),
    ONE_OF_EACH("mixed", CardSet.atLeastOneOfEach()),
    ONE_WILDCARD("wild", CardSet.oneWildCard()),
    TWO_WILDCARDS("wild", CardSet.twoWildCards());

    public final String cardSetName;
    public final Predicate<List<Card>> logic;

    CardSet(String cardSetName, Predicate<List<Card>> logic) {
        this.cardSetName = cardSetName;
        this.logic = logic;
    }

    @Override
    public String toString() {
        return this.cardSetName;
    }

    public static Optional<CardSet> fromString(String response) {
        for (CardSet e : values()) {
            if (e.cardSetName.contains(response.toLowerCase())) {
                return Optional.of(e);
            }
        }
        return Optional.empty();
    }

    public static Predicate<List<Card>> atLeastThreeOfType(CardType cardType) {
       return cards -> CardSet.getCardsOfType(cardType, cards).size() > 2;
    }

    public static Predicate<List<Card>> atLeastOneOfEach() {
        return cards -> CardSet.getCardsOfType(CardType.ARTILLERY, cards).size() >= 1 &&
                CardSet.getCardsOfType(CardType.CALVARY, cards).size() >= 1 &&
                CardSet.getCardsOfType(CardType.SOLDIER, cards).size() >= 1;
    }

    public static Predicate<List<Card>> oneWildCard() {
        return cards -> CardSet.getCardsOfType(CardType.WILDCARD, cards).size() == 1 && atLeastThreeCards(cards);
    }

    public static Predicate<List<Card>> twoWildCards() {
        return cards -> CardSet.getCardsOfType(CardType.WILDCARD, cards).size() == 2 && atLeastThreeCards(cards);
    }

    public static List<Card> getCardsOfType(CardType cardType, List<Card> cards) {
        Predicate<Card> isCardType = card -> card.getType() == cardType;
        return cards
                .stream()
                .filter(isCardType)
                .collect(Collectors.toList());
    }

    public static boolean atLeastThreeCards(List<Card> cards) {
        return cards.size() >= 3;
    }

}
