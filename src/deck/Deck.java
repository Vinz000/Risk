package deck;

import java.util.*;

import static common.Constants.*;

public class Deck {
    private static Deck instance;

    private final List<Card> cards = new ArrayList<>(COUNTRY_NAMES.length);

    private Deck() {
        for (int i = 0; i < NUM_INDIVIDUAL_CARDS; i++) {
            cards.add(new Card(CardType.SOLDIER, COUNTRY_NAMES[i]));
            cards.add(new Card(CardType.CALVARY, COUNTRY_NAMES[i + 14]));
            cards.add(new Card(CardType.ARTILLERY, COUNTRY_NAMES[i + 28]));
        }
        Collections.shuffle(cards);
    }

    public static synchronized Deck getInstance() {
        if (instance == null) {
            instance = new Deck();
        }
        return instance;
    }

    public void addWildcards() {
        for (int i = 0; i < NUM_WILDCARDS; i++) {
            cards.add(new Card(CardType.WILDCARD, "wildcard"));
        }
        Collections.shuffle(cards);
    }

    public Optional<Card> drawCard() {
        Optional<Card> nextCard = Optional.ofNullable(deckSize() > 0 ? cards.get(0) : null);
        nextCard.ifPresent(ignored -> cards.remove(0));

        return nextCard;
    }

    public void add(Card card) throws IllegalArgumentException {
        Objects.requireNonNull(card);

        cards.add(card);
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public List<Card> getCards() {
        return cards;
    }

    public int deckSize() {
        return cards.size();
    }
}
