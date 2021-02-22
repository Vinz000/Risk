package player;

import card.Card;
import card.CardType;
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
        player = new Player("Test Player", Color.BLACK);
    }

    @Test
    void playerCreatingNewPlayerObjectShouldCreateNewPlayerObject() {
        try {
            new Player("Test Player", Color.BLACK);
            new Player("Test Player 3", Color.SILVER);
            new Player("Test Player 5", Color.BLUE);
        } catch (IllegalArgumentException e) {
            fail("Should create player object without fail");
        }
    }

    @Test
    void playerNullAs1stParamShouldReturnException() {
        assertThrows(IllegalArgumentException.class, () -> new Player(null, Color.BLACK));
    }

    @Test
    void playerEmptyStringAs1stParamShouldReturnException() {
        assertThrows(IllegalArgumentException.class, () -> new Player("", Color.BLACK));
        assertThrows(IllegalArgumentException.class, () -> new Player("         ", Color.BLACK));
    }

    @Test
    void playerNullAs2ndParamShouldReturnException() {
        assertThrows(IllegalArgumentException.class, () -> new Player("Test Player", null));
    }

    @Test
    void getNameGettingPlayerNameShouldReturnPlayerName() {
        assertEquals(player.getName(), "Test Player");
    }

    @Test
    void getColorGettingPlayerColorShouldReturnPlayerColor() {
        assertEquals(player.getColor(), Color.BLACK);
    }

    @Test
    void getOwnedCountriesGettingListOfCountriesOccupiedShouldReturnListOfOccupiedCountries() {
        assertTrue(player.getOwnedCountries().isEmpty());
    }

    @Test
    void addCountryAddingCountryToPlayerShouldAddCountry() {
        Country testCountry1 = new Country(COUNTRY_NAMES[0], ADJACENT[0], CONTINENT_IDS[0], COUNTRY_COORDS[0]);
        player.addCountry(testCountry1);
        assertEquals(player.getOwnedCountries().get(0), testCountry1);

        Country testCountry2 = new Country(COUNTRY_NAMES[1], ADJACENT[1], CONTINENT_IDS[1], COUNTRY_COORDS[1]);
        player.addCountry(testCountry2);
        assertEquals(player.getOwnedCountries().get(1), testCountry2);
    }

    @Test
    void addCountryNullAsParamShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> player.addCard(null));
    }

    @Test
    void getReinforcementGettingPlayersCurrentAvailableReinforcementShouldReturnAvailableReinforcement() {
        assertEquals(player.getReinforcement(), 0);
    }

    @Test
    void updateReinforcementUpdatingReinforcementShouldUpdateAvailableReinforcement() {
        player.updateReinforcement(3);
        assertEquals(player.getReinforcement(), 3);
        player.updateReinforcement(-2);
        assertEquals(player.getReinforcement(), 1);
        player.updateReinforcement(-1);
        assertEquals(player.getReinforcement(), 0);
    }

    @Test
    void updateReinforcementUpdatingReinforceSoItsLessThanZeroShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> player.updateReinforcement(-1));
    }

    @Test
    void getHandGettingPlayerHandShouldReturnEmptyListOfCards() {
        List<Card> returnedPlayerCardList = player.getHand();

        assertTrue(returnedPlayerCardList.isEmpty());
    }

    @Test
    void addCardAddingCardToPlayerHandShouldAddCardToPlayerHand() {
        Card testCard = new Card(CardType.ARTILLERY, "Test Card");
        player.addCard(testCard);

        assertEquals(player.getHand().get(0), testCard);
    }

    @Test
    void addCardNullCardAsParamShouldReturnException() {
        assertThrows(IllegalArgumentException.class, () -> player.addCard(null));
    }

    @Test
    void removeCardRemovingCardFromPlayerHandShouldRemoveCardFromPlayerHand() {
        Card testCard = new Card(CardType.ARTILLERY, "Test Card");
        player.addCard(testCard);

        assertEquals(player.removeCard(), testCard);
    }

    @Test
    void removeCardRemovingCardFromEmptyHandShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> player.removeCard());
    }

    @Test
    void testEqualsTestingIfSamePlayersAreEqualShouldBeEqual() {
        assertEquals(player, player);
    }

    @Test
    void testEqualsTestingIfDifferentPlayersAreEqualShouldReturnFalse() {
        Player testPlayer2 = new Player("Test Player 2", Color.RED);

        assertNotEquals(testPlayer2, player);
    }
}