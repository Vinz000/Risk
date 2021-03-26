package shell.component;

import common.BaseComponent;
import common.Constants;
import javafx.event.Event;
import javafx.scene.control.TextField;
import shell.model.ShellModel;
import shell.model.ShellModelArg;

import java.util.Observable;

public class ShellInputComponent extends TextField {

    public ShellInputComponent() {
        BaseComponent.build(this::build, this::setCssId, this::modelsToObserve, this::update);
    }

    private void onEnterPressed(Event event) {
        ShellModel shellModel = ShellModel.getInstance();
        String userInput = getText();
        shellModel.handleUserResponse(userInput);
        clear();
    }

    public void build() {
        setOnAction(this::onEnterPressed);
    }

    public void setCssId() {
        setId(Constants.ComponentIds.SHELL_INPUT);
    }

    public Observable[] modelsToObserve() {
        return new Observable[]{
                ShellModel.getInstance()
        };
    }

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
