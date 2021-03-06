package shell.model;

import common.validation.Validator;
import common.validation.ValidatorResponse;
import common.validation.Validators;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Observable;

public class ShellModel extends Observable {

    private static ShellModel instance;
    private final Deque<String> prompts = new LinkedList<>();
    private Validator currentValidator = Validators.alwaysValid;

    private ShellModel() {
    }

    public static synchronized ShellModel getInstance() {
        if (instance == null) {
            return instance = new ShellModel();
        }

        return instance;
    }

    public void setCurrentValidator(Validator validator) {
        currentValidator = validator;
    }

    public String prompt(Validator validator) {

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
        ValidatorResponse validatorResponse = currentValidator.validate(userInput);

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
