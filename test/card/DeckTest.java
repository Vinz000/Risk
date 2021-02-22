package card;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {
    Deck deck;

    @BeforeEach
    void setUp() {
        deck = Deck.getInstance();
    }

    @Test
    void getDeck_GettingInstanceOfDeck_ShouldGetInstance() {
        List<Card> cardList = deck.getDeck();

        assertNotNull(cardList);
    }

    @Test
    void drawCard_DrawCardFromTopOfDeck_ShouldReturnFirstCard() {
        List<Card> cardList = deck.getDeck();
        Card card2 = cardList.get(0);
        Optional<Card> card = deck.drawCard();

        card.ifPresent(card1 -> assertEquals(card1, card2));

    }

    @Test
    void drawCard_DrawingCardFromEmptyDeck_ShouldReturnNull() {
        while(deck.deckSize() > 0) {
            deck.drawCard();
        }

        Optional<Card> card = deck.drawCard();

        assertFalse(card.isPresent());
    }

    @Test
    void add_AddingCardToDeck_ShouldAddToBottomOfDeck() {
        Card card = new Card(CardType.ARTILLERY, "VLL");
        deck.add(card);
        assertEquals(card, deck.getDeck().get(deck.getDeck().size() - 1));
    }

    @Test
    void add_NullAsParam_ShouldReturnException() {
        assertThrows(IllegalArgumentException.class, () -> deck.add(null));
    }

    @Test
    void addWildcards_AddWildcardsToDeck_ShouldHaveWildCardsInDeck() {
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
    void shuffle_ShufflingDeck_ShouldShuffleTheDeck() {
        List<Card> cardListCloned = new ArrayList<>(deck.getDeck());
        List<Card> cardList = deck.getDeck();
        deck.shuffle();

        for (int i = 0; i < deck.getDeck().size(); i++) {
            assertNotSame(cardList.get(0), cardListCloned.get(0));
        }
    }

    @Test
    void deckSize_GettingDeckSize_ShouldReturnAccurateDeckSize() {
        int currentDeckSize = deck.deckSize();
        deck.addWildcards();
        assertEquals(deck.deckSize(), currentDeckSize + 2);
    }
}