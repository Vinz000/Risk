package shell;

import java.util.*;

public class ShellModel extends Observable {
    private final Deque<ShellPrompt> promptQueue = new LinkedList<>();
    private static ShellModel instance;

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
        promptQueue.offerFirst(shellPrompt);
    }

    public ShellPrompt nextPrompt() {
        return promptQueue.poll();
    }

    public void prompt(ShellPrompt shellPrompt) {
        promptQueue.offer(shellPrompt);
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
