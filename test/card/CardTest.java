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
    void card_CalledNewConstructor_ShouldCreateNewInstance() {
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
    void card_NullAs1stParam_ShouldThrowException() {
        assertThrows(NullPointerException.class, () -> {
            new Card(null, "testCountry1");
        });
    }

    @Test
    void card_NullAs2ndParam_ShouldThrowException() {
        assertThrows(NullPointerException.class, () -> {
            new Card(CardType.ARTILLERY, null);
        });
    }


    @Test
    void getType_GetCardType_ShouldReturnCardType() {
        assertEquals(card.getType(), CardType.ARTILLERY);
    }

    @Test
    void getCountryName_GetCountryName_ShouldReturnCorrectCountryName() {
        assertEquals(card.getCountryName(), "testCountry");
    }

    @Test
    void toString_GetObjectAsString_ShouldReturnAppropriateString() {
        assertEquals(card.toString(), "testCountry, ARTILLERY");
    }
}