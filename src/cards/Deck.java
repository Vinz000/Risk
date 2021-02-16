package cards;

import common.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public Card drawCard() {
        Card drawCard = deck.get(0);
        deck.remove(0);

        return drawCard;
    }

    public void add(Card card) {
        deck.add(card);
    }

    public void shuffle() {
        Collections.shuffle(deck);
    }

    public List<Card> getDeck() {
        return deck;
    }

    public int deckLength() {
        return deck.size();
    }
}
