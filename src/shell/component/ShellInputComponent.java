package shell.component;

import common.Component;
import common.Constants;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import shell.model.ShellModel;
import shell.model.ShellModelArg;

import java.util.Observable;
import java.util.Observer;

public class ShellInputComponent extends TextField implements Observer, Component {

    public ShellInputComponent() {
        setCssId();
        observe();
    }

    private void onEnterPressed(Event event) {

        ShellModel shellModel = ShellModel.getInstance();
        String userInput = getText();
        ShellModel.ShellPrompt nextPrompt = shellModel.nextPrompt();

        // If there is a value in the queue...
        if (nextPrompt != null) {
            if (nextPrompt.validator.apply(userInput)) {
                nextPrompt.handler.accept(userInput);
            } else {
                shellModel.notify("Invalid Input.");
                shellModel.retryPrompt(nextPrompt);
            }
        } else {
            shellModel.notify(Constants.Notifications.GG);
            setDisable(true);
        }

        clear();
    }

    @Override
    public void build() {
        setOnAction(this::onEnterPressed);
    }

    @Override
    public void setCssId() {
        setId(Constants.ComponentIds.SHELL_INPUT);
    }

    @Override
    public void observe() {
        ShellModel shellModel = ShellModel.getInstance();
        shellModel.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        ShellModelArg updateArg = (ShellModelArg) arg;

        switch (updateArg.updateType) {
            case SET_INPUT_LINE:
                String inputText = (String) updateArg.arg;
                setText(inputText);

                // Sets position of cursor at the end of line
                positionCaret(inputText.length());
        }
    }
}
