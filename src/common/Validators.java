package common;

import java.util.function.Function;

public class Validators {
    public static final Function<String, Boolean> alwaysValid = input -> true;
    public static final Function<String, Boolean> nonEmpty = input -> !input.isEmpty();
}
