package deck;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {
    private Card card;

    @BeforeEach
    void setUp() {
        card = new Card(CardType.ARTILLERY, "testCountry");
    }

    @Test
    void testCardShouldCreateNewObject() {
        try {
            new Card(CardType.ARTILLERY, "testCountry");
            new Card(CardType.CALVARY, "testCountry1");
            new Card(CardType.SOLDIER, "testCountry2");
            new Card(CardType.WILDCARD, "testCountry3");
        } catch (IllegalArgumentException e) {
            fail("Should create new instance of deck.");
        }
    }

    @Test
    void testCardThrowsIfCardTypeNull() {
        assertThrows(NullPointerException.class, () -> {
            new Card(null, "testCountry1");
        });
    }

    @Test
    void testCardThrowsIfCountryNameNull() {
        assertThrows(NullPointerException.class, () -> {
            new Card(CardType.ARTILLERY, null);
        });
    }

    @Test
    void testGetTypeShouldReturnCardType() {
        assertEquals(card.getType(), CardType.ARTILLERY);
    }

    @Test
    void testGetCountryNameShouldReturnCountryName() {
        assertEquals(card.getCountryName(), "testCountry");
    }

    @Test
    void testToStringShouldReturnCorrectlyFormattedCard() {
        assertEquals(card.toString(), "testCountry, ARTILLERY");
    }
}