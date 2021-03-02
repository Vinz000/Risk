package shell.model;

import common.validation.ValidatorResponse;
import common.validation.Validators;
import shell.prompt.ShellPrompt;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.function.Function;

public class ShellModel extends Observable {

    private static ShellModel instance;
    private final Deque<String> prompts = new LinkedList<>();
    private Function<String, ValidatorResponse> currentValidator = Validators.alwaysValid;

    private ShellModel() {
    }

    public static synchronized ShellModel getInstance() {
        if (instance == null) {
            return instance = new ShellModel();
        }

        return instance;
    }

    public void setCurrentValidator(Function<String, ValidatorResponse> validator) {
        currentValidator = validator;
    }

    public String prompt(Function<String, ValidatorResponse> validator) {

        setCurrentValidator(validator);

        String userInput;
        synchronized (prompts) {
            while (prompts.isEmpty()) {
                try {
                    prompts.wait();
                } catch (InterruptedException ignored) {
                }
            }
            userInput = prompts.pollFirst();
        }

        return userInput;
    }

    public void handleUserResponse(String userInput) {
        ValidatorResponse validatorResponse = currentValidator.apply(userInput);

        if (validatorResponse.isValid()) {
            synchronized (prompts) {
                prompts.offer(userInput);
                prompts.notify();
            }
        } else {
            this.notify(validatorResponse.getMessage());
        }
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
