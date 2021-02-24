package common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DiceTest {
    private Dice dice;

    @BeforeEach
    void setUp() {
        dice = new Dice();
    }

    @Test
    void testGetNextDiceShouldReturnListOfDiceRollInOrder() {
        List<Integer> dice1 = dice.getNextDice(10);

        for (int i = 1; i < dice1.size() - 1; i++) {
            assertTrue(dice1.get(i + 1) <= dice1.get(i));
            assertTrue(dice1.get(i) >= 1);
            assertTrue(dice1.get(i) <= 6);
        }

    }

    @Test
    void testGetRollSumShouldReturnSumOfDiceRoll() {
        for (int i = 0; i < 36; i++) {
            int diceSum = dice.getRollSum(2);
            assertTrue(diceSum >= 2 && diceSum <= 12);
        }
    }

    @Test
    void testToStringShouldReturnDiceRollAsFormattedString() {
        List<Integer> dice1 = dice.getNextDice(2);

        assertEquals(dice.printRoll(), String.format("[ %d ] [ %d ] ", dice1.get(0), dice1.get(1)));
    }
}