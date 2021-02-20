package shell.model;

import common.validation.ValidatorResponse;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class ShellModel extends Observable {

    public static class ShellPrompt {
        public final Consumer<String> handler;
        public final Function<String, ValidatorResponse> validator;

        public ShellPrompt(Consumer<String> handler, Function<String, ValidatorResponse> validator) {
            this.handler = handler;
            this.validator = validator;

        }

    }

    private static ShellModel instance;
    private final Deque<ShellPrompt> prompts = new LinkedList<>();

    private ShellModel() {
    }


    public static ShellModel getInstance() {
        if (instance == null) {
            return instance = new ShellModel();
        }

        return instance;
    }

    public void retryPrompt(ShellPrompt shellPrompt) {
        // Invalid input was provided,
        // the [ShellPrompt] must
        // be pushed to the front of the queue!
        prompts.offerFirst(shellPrompt);
    }

    public ShellPrompt nextPrompt() {
        return prompts.poll();
    }

    public void prompt(ShellPrompt shellPrompt) {
        prompts.offer(shellPrompt);
    }

    public void notify(String notification) {
        ShellModelArg shellModelArg = new ShellModelArg(notification, ShellModelUpdateType.NOTIFICATION);
        setChanged();
        notifyObservers(shellModelArg);
    }

    public void setInputLineText(String text) {
        ShellModelArg shellModelArg = new ShellModelArg(text, ShellModelUpdateType.SET_INPUT_LINE);
        setChanged();
        notifyObservers(shellModelArg);
    }

}
