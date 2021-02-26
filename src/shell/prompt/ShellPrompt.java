package shell.prompt;

import common.validation.ValidatorResponse;

import java.util.function.Consumer;
import java.util.function.Function;

public class ShellPrompt {
    public final Consumer<String> handler;
    public final Function<String, ValidatorResponse> validator;

    public ShellPrompt(Consumer<String> handler, Function<String, ValidatorResponse> validator) {
        this.handler = handler;
        this.validator = validator;
    }
}