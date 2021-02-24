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
    public void testCountryShouldCreateNewCountryObject() {
        try {
            new Country(COUNTRY_NAMES[0], ADJACENT[0], CONTINENT_IDS[0], COUNTRY_COORDS[0]);
            new Country(COUNTRY_NAMES[2], ADJACENT[2], CONTINENT_IDS[2], COUNTRY_COORDS[2]);
            new Country(COUNTRY_NAMES[8], ADJACENT[8], CONTINENT_IDS[8], COUNTRY_COORDS[8]);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testCountryThrowsIfCountryNameIsNull() {
        assertThrows(NullPointerException.class, () -> new Country(null, ADJACENT[0], CONTINENT_IDS[0], COUNTRY_COORDS[0]));
    }

    @Test
    public void testCountryThrowsIfAdjCountriesIsNull() {
        assertThrows(NullPointerException.class, () -> new Country(COUNTRY_NAMES[2], null, CONTINENT_IDS[2], COUNTRY_COORDS[2]));
    }

    @Test
    public void testCountryThrowsIfContinentIdIsNegative() {
        assertThrows(AssertionError.class, () -> new Country(COUNTRY_NAMES[8], ADJACENT[8], -1, COUNTRY_COORDS[8]));

    }

    @Test
    public void testCountryThrowsIfCoordsIsNull() {
        assertThrows(NullPointerException.class, () -> new Country(COUNTRY_NAMES[8], ADJACENT[8], CONTINENT_IDS[8], null));
    }

    @Test
    void testGetCoordsShouldReturnCoords() {
        assertEquals(country.getCoords().getX(), COUNTRY_COORDS[0][0]);
        assertEquals(country.getCoords().getY(), COUNTRY_COORDS[0][1]);
    }

    @Test
    void testGetCountryNameShouldReturnCountryName() {
        assertEquals(country.getCountryName(), COUNTRY_NAMES[0]);
    }

    @Test
    void testGetAdjCountriesShouldReturnAdjCountries() {
        assertEquals(country.getAdjCountries(), ADJACENT[0]);
    }

    @Test
    void testGetContinentIDShouldReturnContinentId() {
        assertEquals(country.getContinentID(), CONTINENT_IDS[0]);
    }

    @Test
    void testGetArmyCountShouldReturnArmyCount() {
        assertEquals(country.getArmyCount(), 0);
    }

    @Test
    void testUpdateArmyCountShouldUpdateArmyCount() {
        country.updateArmyCount(3);
        assertEquals(country.getArmyCount(), 3);
        country.updateArmyCount(-2);
        assertEquals(country.getArmyCount(), 1);
        country.updateArmyCount(-1);
        assertEquals(country.getArmyCount(), 0);
    }

    @Test
    void testUpdateArmyCountThrowsIfArmyCountGoesBelowZero() {
        assertThrows(IllegalArgumentException.class, () -> country.updateArmyCount(-1));
    }

    @Test
    void testGetOccupierShouldReturnOccupier() {
        assertNull(country.getOccupier());
    }

    @Test
    void testSetOccupierShouldSetOccupier() {
        Player player = new HumanPlayer("John", Color.valueOf("#000000"));
        country.setOccupier(player);
        assertEquals(country.getOccupier(), player);
    }

    @Test
    void testSetOccupierThrowsIfSettingNullOccupier() {
        assertThrows(NullPointerException.class, () -> country.setOccupier(null));
    }

    @Test
    void testEqualsShouldReturnTrueIfSameCountries() {
        Country country2 = new Country(COUNTRY_NAMES[0], ADJACENT[0], CONTINENT_IDS[0], COUNTRY_COORDS[0]);
        assertEquals(country, country2);
    }

    @Test
    void testEqualsShouldReturnFalseIfDifferentCountries() {
        Country country3 = new Country(COUNTRY_NAMES[1], ADJACENT[1], CONTINENT_IDS[1], COUNTRY_COORDS[1]);
        assertNotEquals(country, country3);
    }
}