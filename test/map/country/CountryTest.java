package map.country;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import player.HumanPlayer;
import player.Player;

import static common.Constants.*;

import static org.junit.jupiter.api.Assertions.*;

class CountryTest {
    private Country country;

    @BeforeEach
    void setUp() {
        country = new Country(COUNTRY_NAMES[0], ADJACENT[0], CONTINENT_IDS[0], COUNTRY_COORDS[0]);
    }

    @Test
    public void testCountry() {
        try {
            new Country(COUNTRY_NAMES[0], ADJACENT[0], CONTINENT_IDS[0], COUNTRY_COORDS[0]);
            new Country(COUNTRY_NAMES[2], ADJACENT[2], CONTINENT_IDS[2], COUNTRY_COORDS[2]);
            new Country(COUNTRY_NAMES[8], ADJACENT[8], CONTINENT_IDS[8], COUNTRY_COORDS[8]);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testCountryIllegalParameters() {
        try {
            new Country(null, ADJACENT[0], CONTINENT_IDS[0], COUNTRY_COORDS[0]);
            new Country(COUNTRY_NAMES[2], null, CONTINENT_IDS[2], COUNTRY_COORDS[2]);
            new Country(COUNTRY_NAMES[8], ADJACENT[8], -1, COUNTRY_COORDS[8]);
            new Country(COUNTRY_NAMES[8], ADJACENT[8], CONTINENT_IDS[8], null);
            fail("Illegal values allowed in constructor.");
        } catch (Exception ignored) {
        }
    }

    @Test
    void testGetCoords() {
        assertEquals(country.getCoords().getX(), COUNTRY_COORDS[0][0]);
        assertEquals(country.getCoords().getY(), COUNTRY_COORDS[0][1]);
    }

    @Test
    void testGetCountryName() {
        assertEquals(country.getCountryName(), COUNTRY_NAMES[0]);
    }

    @Test
    void testGetAdjCountries() {
        assertEquals(country.getAdjCountries(), ADJACENT[0]);
    }

    @Test
    void testGetContinentID() {
        assertEquals(country.getContinentID(), CONTINENT_IDS[0]);
    }

    @Test
    void testGetArmy() {
        assertEquals(country.getArmyCount(), 0);
    }

    @Test
    void testUpdateArmy() {
        country.updateArmyCount(3);
        assertEquals(country.getArmyCount(), 3);
        country.updateArmyCount(-2);
        assertEquals(country.getArmyCount(), 1);
        country.updateArmyCount(-1);
        assertEquals(country.getArmyCount(), 0);
    }

    @Test
    void testUpdateArmyLessThanZero() {
        assertThrows(IllegalArgumentException.class, () -> country.updateArmyCount(-1));
    }

    @Test
    void getCurrentPlayer() {
        assertNull(country.getOccupier());
    }

    @Test
    void setCurrentPlayer() {
        Player player = new HumanPlayer("John", Color.valueOf("#000000"));
        country.setOccupier(player);
        assertEquals(country.getOccupier(), player);
    }

    @Test
    void testEquals() {
        Country country2 = new Country(COUNTRY_NAMES[0], ADJACENT[0], CONTINENT_IDS[0], COUNTRY_COORDS[0]);
        assertEquals(country, country2);

        Country country3 = new Country(COUNTRY_NAMES[1], ADJACENT[1], CONTINENT_IDS[1], COUNTRY_COORDS[1]);
        assertNotEquals(country, country3);
    }
}