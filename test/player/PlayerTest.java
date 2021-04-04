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
    void testPlayerShouldCreateNewPlayerObject() {
        try {
            new PlayerAbstractTest("Test Player", Color.BLACK);
            new PlayerAbstractTest("Test Player 3", Color.SILVER);
            new PlayerAbstractTest("Test Player 5", Color.BLUE);
        } catch (IllegalArgumentException e) {
            fail("Should create player object without fail");
        }
    }

    @Test
    void testPlayerThrowsIfPlayerNameIsNull() {
        assertThrows(NullPointerException.class, () -> new PlayerAbstractTest(null, Color.BLACK));
    }

    @Test
    void testTPlayerhrowsIfColorIsNull() {
        assertThrows(NullPointerException.class, () -> new PlayerAbstractTest("Test Player", null));
    }

    @Test
    void testPlayerThrowsIfPlayerNameIsEmpty() {
        assertThrows(AssertionError.class, () -> new PlayerAbstractTest("", Color.BLACK));
        assertThrows(AssertionError.class, () -> new PlayerAbstractTest("         ", Color.BLACK));
    }

    @Test
    void testGetNameShouldReturnPlayerName() {
        assertEquals(player.getName(), "Test Player");
    }

    @Test
    void testGetColorShouldReturnPlayerColor() {
        assertEquals(player.getColor(), Color.BLACK);
    }

    @Test
    void testGetOwnedCountriesShouldReturnListOwnedPlayerCountries() {
        assertTrue(player.getOwnedCountries().isEmpty());
    }

    @Test
    void testAddCountryShouldAddCountryToPlayer() {
        Country testCountry1 = new Country(COUNTRY_NAMES[0], ADJACENT[0], CONTINENT_IDS[0], 0, COUNTRY_COORDS[0]);
        player.addCountry(testCountry1);
        assertEquals(player.getOwnedCountries().get(0), testCountry1);

        Country testCountry2 = new Country(COUNTRY_NAMES[1], ADJACENT[1], CONTINENT_IDS[1], 1, COUNTRY_COORDS[1]);
        player.addCountry(testCountry2);
        assertEquals(player.getOwnedCountries().get(1), testCountry2);
    }

    @Test
    void testAddCountryThrowsIfAddingNullCountry() {
        assertThrows(NullPointerException.class, () -> player.addCountry(null));
    }

    @Test
    void testGetCardsShouldReturnHand() {
        List<Card> returnedPlayerCardList = player.getCards();

        assertTrue(returnedPlayerCardList.isEmpty());
    }

    @Test
    void testAddCardShouldAddCardToHand() {
        Card testCard = new Card(CardType.ARTILLERY, "Test Card");
        player.addCard(testCard);

        assertEquals(player.getCards().get(0), testCard);
    }

    @Test
    void testAddCountryThrowsIfNullCardAdded() {
        assertThrows(NullPointerException.class, () -> player.addCountry(null));
    }

    // TODO: Change (Method is getting modified)
   /* @Test
    void testRemoveCardShouldRemoveCardFromHand() {
        Card testCard = new Card(CardType.ARTILLERY, "Test Card");
        player.addCard(testCard);

        assertEquals(player.removeCard(testCard), testCard);
    }*/

    @Test
    void testRemoveCardThrowsIfRemovingFromEmptyHand() {
        assertThrows(AssertionError.class, () -> player.removeCard(null));
    }


    @Test
    void testEqualsTrueIfSamePlayersCompared() {
        assertEquals(player, player);
    }

    @Test
    void testEqualsFalseIfDifferentPlayersCompared() {
        Player testPlayer2 = new PlayerAbstractTest("Test Player 2", Color.RED);

        assertNotEquals(testPlayer2, player);
    }
}