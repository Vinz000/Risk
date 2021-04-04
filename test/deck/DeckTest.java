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
        List<Card> cardList = deck.getCards();
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
        assertEquals(card, deck.getCards().get(deck.getCards().size() - 1));
    }

    @Test
    void testAddThrowsIfAddingNullCard() {
        assertThrows(IllegalArgumentException.class, () -> deck.add(null));
    }

    @Test
    void testAddWildCardsShouldAddWildCards() {
        deck.addWildCards();
        List<Card> cardList = deck.getCards();

        int wildCardsCount = (int) cardList
                .stream()
                .filter(card -> card.getType() == CardType.WILDCARD)
                .count();

        assertEquals(wildCardsCount, 2);
        assertEquals(cardList.size(), 44);
    }

    @Test
    void testShuffleShouldShuffleDeck() {
        List<Card> cardListCloned = new ArrayList<>(deck.getCards());
        List<Card> cardList = deck.getCards();
        deck.shuffle();

        for (int i = 0; i < deck.getCards().size(); i++) {
            assertNotSame(cardList.get(0), cardListCloned.get(0));
        }
    }

    @Test
    void testDeckSizeShouldReturnDeckSize() {
        int currentDeckSize = deck.deckSize();
        deck.addWildCards();
        assertEquals(deck.deckSize(), currentDeckSize + 2);
    }
}