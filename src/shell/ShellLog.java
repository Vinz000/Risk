package shell;

import java.util.function.Function;

public class ShellLog {
    public final String message;
    public final boolean isPrompt;
    public Function<String, Boolean> validator;

    public ShellLog(String message, boolean isPrompt) {
        this.message = message;
        this.isPrompt = isPrompt;
    }
    public ShellLog(String message, boolean isPrompt, Function<String, Boolean> validator) {
        this.message = message;
        this.isPrompt = isPrompt;
        this.validator = validator;
    }
}

