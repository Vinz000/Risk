package game;

import common.Validator;

import java.util.function.Function;

public enum GameState {
    NEW(input -> true),
    PLAYER_ONE_NAME(Validator.nonEmpty),
    PLAYER_TWO_NAME(Validator.nonEmpty),
    QUIT(input -> true);


    private final Function<String, Boolean> validator;

    private GameState(Function<String, Boolean> validator) {
        this.validator = validator;
    }

    public boolean validate(String input) {
        return validator.apply(input);
    }

    public GameState next() {
        GameState[] values = values();
        return values[(this.ordinal() + 1) % values.length];
    }

}
