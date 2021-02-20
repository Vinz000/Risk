package card;

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
    void testCard() {
        try {
            new Card(CardType.ARTILLERY, "testCountry");
            new Card(CardType.CALVARY, "testCountry1");
            new Card(CardType.SOLDIER, "testCountry2");
            new Card(CardType.WILDCARD, "testCountry3");
        } catch (IllegalArgumentException e) {
            fail("Should create new instance of card.");
        }
    }

    @Test
    void testCardNullParameters() {
        assertThrows(NullPointerException.class, () -> {
            new Card(null, "testCountry1");
            new Card(CardType.ARTILLERY, null);
        });
    }

    @Test
    void testGetType() {
        assertEquals(card.getType(), CardType.ARTILLERY);
    }

    @Test
    void getCountryName() {
        assertEquals(card.getCountryName(), "testCountry");
    }

    @Test
    void testToString() {
        assertEquals(card.toString(), "testCountry, ARTILLERY");
    }
}