package shell;

import java.util.function.Consumer;
import java.util.function.Function;

public class ShellPrompt {
    public final Consumer<String> handler;
    public final Function<String, Boolean> validator;

    public ShellPrompt(Consumer<String> handler, Function<String, Boolean> validator) {

        this.handler = handler;
        this.validator = validator;

    }

}
