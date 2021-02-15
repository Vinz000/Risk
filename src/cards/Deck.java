package cards;

import common.Constants;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    private ArrayList<Card> deck = new ArrayList<>(Constants.COUNTRY_NAMES.length);
    private Card drawCard;

    public Deck() {
        for(int i = 0; i < 14; i++) {
            deck.add(new Card(CardType.SOLDIER, Constants.COUNTRY_NAMES[i]));
            deck.add(new Card(CardType.CALVARY, Constants.COUNTRY_NAMES[i + 14]));
            deck.add(new Card(CardType.ARTILLERY, Constants.COUNTRY_NAMES[i + 28]));

        }
        Collections.shuffle(deck);
    }

    public void addWildcards() {
        for(int l = 42; l < 44; l++){
            deck.add(new Card(CardType.WILDCARD, "wildcard"));
        }
        Collections.shuffle(deck);
    }
    public Card drawCard() {
        drawCard = deck.get(0);
        deck.remove(0);

        return drawCard;
    }

    public void add(Card card) {
        deck.add(card);
    }

    public void shuffle(){
        Collections.shuffle(deck);
    }

    public ArrayList<Card> getDeck() {
        return deck;
    }

    public int deckLength(){
        return deck.size();
    }
}
