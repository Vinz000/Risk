package player;

import deck.Card;
import deck.CardType;
import javafx.scene.paint.Color;
import map.country.Country;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static common.Constants.*;
import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    Player player;

    @BeforeEach
    void setUp() {
        player = new PlayerAbstractTest("Test Player", Color.BLACK);
    }

    @Test
    void testShouldCreateNewPlayerObject() {
        try {
            new PlayerAbstractTest("Test Player", Color.BLACK);
            new PlayerAbstractTest("Test Player 3", Color.SILVER);
            new PlayerAbstractTest("Test Player 5", Color.BLUE);
        } catch (IllegalArgumentException e) {
            fail("Should create player object without fail");
        }
    }

    @Test
    void testThrowsIfPlayerNameIsNull() {
        assertThrows(NullPointerException.class, () -> new PlayerAbstractTest(null, Color.BLACK));
    }

    @Test
    void testThrowsIfColorIsNull() {
        assertThrows(NullPointerException.class, () -> new PlayerAbstractTest("Test Player", null));
    }

    @Test
    void testThrowsIfPlayerNameIsEmpty() {
        assertThrows(AssertionError.class, () -> new PlayerAbstractTest("", Color.BLACK));
        assertThrows(AssertionError.class, () -> new PlayerAbstractTest("         ", Color.BLACK));
    }

    @Test
    void testShouldReturnPlayerName() {
        assertEquals(player.getName(), "Test Player");
    }

    @Test
    void testShouldReturnPlayerColor() {
        assertEquals(player.getColor(), Color.BLACK);
    }

    @Test
    void testShouldReturnListOwnedPlayerCountries() {
        assertTrue(player.getOwnedCountries().isEmpty());
    }

    @Test
    void testShouldAddCountryToPlayer() {
        Country testCountry1 = new Country(COUNTRY_NAMES[0], ADJACENT[0], CONTINENT_IDS[0], COUNTRY_COORDS[0]);
        player.addCountry(testCountry1);
        assertEquals(player.getOwnedCountries().get(0), testCountry1);

        Country testCountry2 = new Country(COUNTRY_NAMES[1], ADJACENT[1], CONTINENT_IDS[1], COUNTRY_COORDS[1]);
        player.addCountry(testCountry2);
        assertEquals(player.getOwnedCountries().get(1), testCountry2);
    }

    @Test
    void testThrowsIfAddingNullCountry() {
        assertThrows(NullPointerException.class, () -> player.addCountry(null));
    }

    // TODO: Change since method is going to change
    @Test
    void testShouldReturnReinforcement() {
        assertEquals(player.getReinforcement(), 0);
    }

    @Test
    void testShouldReturnHand() {
        List<Card> returnedPlayerCardList = player.getHand();

        assertTrue(returnedPlayerCardList.isEmpty());
    }

    @Test
    void testShouldAddCardToHand() {
        Card testCard = new Card(CardType.ARTILLERY, "Test Card");
        player.addCard(testCard);

        assertEquals(player.getHand().get(0), testCard);
    }

    @Test
    void testThrowsIfNullCardAdded() {
        assertThrows(NullPointerException.class, () -> player.addCountry(null));
    }

    // TODO: Change (Method is getting modified)
    @Test
    void testShouldRemoveCardFromHand() {
        Card testCard = new Card(CardType.ARTILLERY, "Test Card");
        player.addCard(testCard);

        assertEquals(player.removeCard(), testCard);
    }

    @Test
    void testThrowsIfRemovingFromEmptyHand() {
        assertThrows(AssertionError.class, () -> player.removeCard());
    }


    @Test
    void testTrueIfSamePlayersCompared() {
        assertEquals(player, player);
    }

    @Test
    void testFalseIfDifferentPlayersCompared() {
        Player testPlayer2 = new PlayerAbstractTest("Test Player 2", Color.RED);

        assertNotEquals(testPlayer2, player);
    }
}