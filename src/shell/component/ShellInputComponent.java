package shell.component;

import common.Component;
import common.Constants;
import common.validation.ValidatorResponse;
import javafx.event.Event;
import javafx.scene.control.TextField;
import shell.model.ShellModel;
import shell.model.ShellModelArg;
import shell.prompt.ShellPrompt;

import java.util.Observable;
import java.util.Observer;

public class ShellInputComponent extends TextField implements Observer, Component {

    public ShellInputComponent() {
        setCssId();
        observe();
        build();
    }

    private void onEnterPressed(Event event) {

        ShellModel shellModel = ShellModel.getInstance();
        String userInput = getText();
        ShellPrompt nextPrompt = shellModel.nextPrompt();

        // If there is a value in the queue...
        if (nextPrompt != null) {
            ValidatorResponse validatorResponse = nextPrompt.validator.apply(userInput);
            if (validatorResponse.isValid()) {
                nextPrompt.handler.accept(userInput);
            } else {
                shellModel.notify(validatorResponse.getMessage());
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
