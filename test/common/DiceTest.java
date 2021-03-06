package common;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

class DiceTest {

    @Test
    void testRollReturnsIntegersInDescendingOrder() {
        List<Integer> rolledDice = Dice.roll(10);
        Predicate<Integer> isGreaterThanNext = index -> {
            int currentDie = rolledDice.get(index);
            int nextDie = rolledDice.size() > index + 1 ?
                    rolledDice.get(index + 1) :
                    -1;
            return currentDie >= nextDie;
        };

        for (int die : rolledDice) {
            assertTrue(isGreaterThanNext.test(die));
        }
    }

    @Test
    void testRollReturnsIntegersInRangeOneToSix() {
        List<Integer> rolledDice = Dice.roll(10);
        Predicate<Integer> isInRangeOneToSix = number -> number >= 1 && number <= 6;

        for (int die : rolledDice) {
            assertTrue(isInRangeOneToSix.test(die));
        }
    }

    @Test
    void testRollBreaksReferenceOnConsecutiveRolls() {
        List<Integer> firstRolledDice = Dice.roll(100000);
        List<Integer> secondRolledDice = Dice.roll(100000);

        // Note: has slim chance of failing!
        assertNotEquals(firstRolledDice, secondRolledDice);
    }

    @Test
    void testRollReturnsDiceCountNumberOfIntegers() {
        int diceCount = 1000;
        List<Integer> rolledDice = Dice.roll(diceCount);

        assertEquals(diceCount, rolledDice.size());
    }

    @Test
    void testRollZeroDiceCountThrowsAssertionError() {
        assertThrows(AssertionError.class, () -> Dice.roll(0));
    }

    @Test
    void testRollNegativeDiceCountThrowsAssertionError() {
        assertThrows(AssertionError.class, () -> Dice.roll(-1));
    }

    @Test
    void testSumDiceReturnsCorrectSum() {
        List<Integer> rolledDice = Dice.roll(10);
        int sum = 0;

        for (int die : rolledDice) {
            sum += die;
        }

        assertEquals(sum, Dice.sumDice(rolledDice));
    }

    @Test
    void testSumDiceNullRolledDiceThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> Dice.sumDice(null));
    }

    // TODO: revise test after Vincent creates rich text module (affecting this).
//    @Test
//    void testToStringShouldReturnDiceRollAsFormattedString() {
//        List<Integer> dice1 = dice.getNextDice(2);
//
//        assertEquals(dice.toString(), String.format("[ %d ] [ %d ] ", dice1.get(0), dice1.get(1)));
//    }
}