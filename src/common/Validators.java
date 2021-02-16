package common;

import java.util.function.Function;

public class Validators {
    public static final Function<String, Boolean> alwaysValid = input -> true;
    public static final Function<String, Boolean> nonEmpty = input -> !input.isEmpty();
    public static final Function<String, Boolean> yesNo = input ->
            input.equalsIgnoreCase("yes") ||
            input.toLowerCase().equals("n") ||
            input.equalsIgnoreCase("no") ||
            input.toLowerCase().equals("y");
}
