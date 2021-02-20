package common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Dice {

    private final Random die = new Random();
    private final List<Integer> rolledDice = new ArrayList<>();

    public Dice() {
    }

    public List<Integer> getNextDice(int numDice) {
        rollDice(numDice);
        return rolledDice;
    }

    private void rollDice(int numDice) {
        this.rolledDice.clear();

        for (int i = 0; i < numDice; i++) {
            this.rolledDice.add(die.nextInt(6) + 1);
        }

        // Sort then reverse
        Collections.sort(this.rolledDice);
        Collections.reverse(this.rolledDice);
    }

    public int getRollSum(int numDice) {
        rollDice(numDice);
        return rolledDice.stream().reduce(0, Integer::sum);
    }

    public String printRoll() {
        StringBuilder stringBuilder = new StringBuilder();

        rolledDice.forEach(die -> stringBuilder.append(String.format("[ %d ] ", die)));

        return stringBuilder.toString();
    }

}
