package common;

import java.util.function.Function;

// add string and player as input
public class Validator {
    public static final Function<String, Boolean> nonEmpty = input -> !input.isEmpty();
}
