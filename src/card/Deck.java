package card;

import common.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Deck {
    private final List<Card> deck = new ArrayList<>(Constants.COUNTRY_NAMES.length);

    public Deck() {
        for (int i = 0; i < Constants.NUM_INDIVIDUAL_CARDS; i++) {
            deck.add(new Card(CardType.SOLDIER, Constants.COUNTRY_NAMES[i]));
            deck.add(new Card(CardType.CALVARY, Constants.COUNTRY_NAMES[i + 14]));
            deck.add(new Card(CardType.ARTILLERY, Constants.COUNTRY_NAMES[i + 28]));

        }
        Collections.shuffle(deck);
    }

    public void addWildcards() {
        for (int l = Constants.NUM_MAIN_CARDS; l < Constants.NUM_MAIN_CARDS_PLUS_WILDCARDS; l++) {
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
