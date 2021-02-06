package game;

import common.Validators;

import java.util.function.Function;

public enum GameState {
    NEW(input -> true),
    PLAYER_ONE_NAME(Validators.nonEmpty),
    PLAYER_TWO_NAME(Validators.nonEmpty),
    QUIT(input -> true);


    private final Function<String, Boolean> validator;

    GameState(Function<String, Boolean> validator) {
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
