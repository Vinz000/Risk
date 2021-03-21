package common;

import java.util.*;
import java.util.stream.Collectors;

public abstract class Dice {

    private static final Random random = new Random();

    public static List<Integer> roll(int diceCount) {
        assert diceCount > 0 : "diceCount must be greater than zero, but was " + diceCount;

        return random
                .ints(diceCount, 1, 7)
                .boxed()
                .sorted(Collections.reverseOrder())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public static int sumDice(List<Integer> rolledDice) {
        return Objects.requireNonNull(rolledDice)
                .stream()
                .reduce(0, Integer::sum);
    }

    public static String toString(List<Integer> rolledDice) {
        StringBuilder stringBuilder = new StringBuilder();

        rolledDice.forEach(die -> {

            switch (die) {
                case 1:
                    stringBuilder.append("⚀ ");
                    break;
                case 2:
                    stringBuilder.append("⚁ ");
                    break;
                case 3:
                    stringBuilder.append("⚂ ");
                    break;
                case 4:
                    stringBuilder.append("⚃ ");
                    break;
                case 5:
                    stringBuilder.append("⚄ ");
                    break;
                case 6:
                    stringBuilder.append("⚅ ");
            }
        });

        return stringBuilder.toString();
    }

}
