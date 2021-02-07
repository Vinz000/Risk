package shell;

import java.util.*;

public class ShellModel extends Observable {
    private final Deque<ShellPrompt> promptQueue = new LinkedList<>();

    public ShellModel() {
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
        setChanged();
        notifyObservers(notification);
    }
}
