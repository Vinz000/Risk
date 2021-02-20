package card;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {
    Deck deck = new Deck();

    @BeforeEach
    void setUp() {
        deck = new Deck();
    }

    @Test
    void testDeck() {
        try {
            new Deck();
        } catch (IllegalArgumentException e) {
            fail("Constructor should create instance of a new Deck.");
        }
    }

    @Test
    void getDeck() {
        List<Card> cardList = deck.getDeck();
        assertEquals(cardList.size(), 42);

        int soldierTypeCardsCount = (int) cardList
                .stream()
                .filter(card -> card.getType() == CardType.SOLDIER)
                .count();
        int artilleryTypeCardsCount = (int) cardList
                .stream()
                .filter(card -> card.getType() == CardType.ARTILLERY)
                .count();
        int cavalryTypeCardsCount = (int) cardList
                .stream()
                .filter(card -> card.getType() == CardType.CALVARY)
                .count();
        int wildCardsCount = (int) cardList
                .stream()
                .filter(card -> card.getType() == CardType.WILDCARD)
                .count();

        assertEquals(soldierTypeCardsCount, 14);
        assertEquals(artilleryTypeCardsCount, 14);
        assertEquals(cavalryTypeCardsCount, 14);
        assertEquals(wildCardsCount, 0);
    }

    @Test
    void testDrawCard() {
        List<Card> cardList = deck.getDeck();
        Card card2 = cardList.get(0);
        Optional<Card> card = deck.drawCard();

        card.ifPresent(card1 -> assertEquals(card1, card2));

    }

    @Test
    void testDrawCardShouldReturnNull() {
        while(deck.deckSize() > 0) {
            deck.drawCard();
        }

        Optional<Card> card = deck.drawCard();

        assertFalse(card.isPresent());
    }

    @Test
    void testAdd() {
        Card card = new Card(CardType.ARTILLERY, "VLL");
        deck.add(card);
        assertEquals(card, deck.getDeck().get(deck.getDeck().size() - 1));
    }

    @Test
    void testAddNullParameters() {
        assertThrows(IllegalArgumentException.class, () -> deck.add(null));
    }

    @Test
    void addWildcards() {
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
    void shuffle() {
        List<Card> cardListCloned = new ArrayList<>(deck.getDeck());
        List<Card> cardList = deck.getDeck();
        deck.shuffle();

        for (int i = 0; i < deck.getDeck().size(); i++) {
            assertNotSame(cardList.get(0), cardListCloned.get(0));
        }
    }

    @Test
    void deckSize() {
        assertEquals(deck.deckSize(), 42);
        deck.drawCard();
        assertEquals(deck.deckSize(), 41);
    }
}