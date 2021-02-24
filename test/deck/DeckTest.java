package deck;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {
    Deck deck = Deck.getInstance();

    @Test
    void testDeckShouldReturnTopCard() {
        List<Card> cardList = deck.getDeck();
        Card card2 = cardList.get(0);
        Optional<Card> card = deck.drawCard();

        card.ifPresent(card1 -> assertEquals(card1, card2));
    }

    @Test
    void testDrawCardReturnNullIfDrawingFromEmptyDeck() {
        while(deck.deckSize() > 0) {
            deck.drawCard();
        }

        Optional<Card> card = deck.drawCard();

        assertFalse(card.isPresent());
    }

    @Test
    void testAddShouldAddNewCardToBottomOfDeck() {
        Card card = new Card(CardType.ARTILLERY, "VLL");
        deck.add(card);
        assertEquals(card, deck.getDeck().get(deck.getDeck().size() - 1));
    }

    @Test
    void testAddThrowsIfAddingNullCard() {
        assertThrows(IllegalArgumentException.class, () -> deck.add(null));
    }

    @Test
    void testAddWildCardsShouldAddWildCards() {
        deck.addWildcards();
        List<Card> cardList = deck.getDeck();

        int wildCardsCount = (int) cardList
                .stream()
                .filter(card -> card.getType() == CardType.WILDCARD)
                .count();

        assertEquals(wildCardsCount, 2);
        assertEquals(cardList.size(), 44);
    }

    @Test
    void testShuffleShouldShuffleDeck() {
        List<Card> cardListCloned = new ArrayList<>(deck.getDeck());
        List<Card> cardList = deck.getDeck();
        deck.shuffle();

        for (int i = 0; i < deck.getDeck().size(); i++) {
            assertNotSame(cardList.get(0), cardListCloned.get(0));
        }
    }

    @Test
    void testDeckSizeShouldReturnDeckSize() {
        int currentDeckSize = deck.deckSize();
        deck.addWildcards();
        assertEquals(deck.deckSize(), currentDeckSize + 2);
    }
}