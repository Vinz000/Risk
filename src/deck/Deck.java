package deck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static common.Constants.*;

public class Deck {
    private static Deck instance;

    private final List<Card> deck = new ArrayList<>(COUNTRY_NAMES.length);

    private Deck() {
        for (int i = 0; i < NUM_INDIVIDUAL_CARDS; i++) {
            deck.add(new Card(CardType.SOLDIER, COUNTRY_NAMES[i]));
            deck.add(new Card(CardType.CALVARY, COUNTRY_NAMES[i + 14]));
            deck.add(new Card(CardType.ARTILLERY, COUNTRY_NAMES[i + 28]));
        }
        Collections.shuffle(deck);
    }

    public static synchronized Deck getInstance() {
        if (instance == null) {
            instance = new Deck();
        }
        return instance;
    }

    public void addWildcards() {
        for (int i = 0; i < NUM_WILDCARDS; i++) {
            deck.add(new Card(CardType.WILDCARD, "wildcard"));
        }
        Collections.shuffle(deck);
    }

    public Optional<Card> drawCard() {
        Optional<Card> nextCard = Optional.ofNullable(deckSize() > 0 ? deck.get(0) : null);
        nextCard.ifPresent(ignored -> deck.remove(0));

        return nextCard;
    }

    public void add(Card card) throws IllegalArgumentException {
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null.");
        }

        deck.add(card);
    }

    public void shuffle() {
        Collections.shuffle(deck);
    }

    public List<Card> getDeck() {
        return deck;
    }

    public int deckSize() {
        return deck.size();
    }
}
