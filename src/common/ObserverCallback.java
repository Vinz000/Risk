package common;

import java.util.Observable;

@FunctionalInterface
public interface ObserverCallback {
    void run(Observable o, Object arg);
}
