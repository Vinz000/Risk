package shell;

import common.Constants;
import game.GameState;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import map.MapModel;

import java.util.Observable;
import java.util.Observer;


public class ShellComponent extends VBox implements Observer {

    private final Label log = new Label(Constants.Prompts.WELCOME);
    private final TextField inputLine = new TextField();
    private GameState gameState = GameState.NEW;

    public ShellComponent(ShellModel shellModel, MapModel mapModel) {
        super();
        build(shellModel, mapModel);
    }

    public void appendLogText(String message) {
        log.setText(log.getText() + "\n" + message);
    }

    // TODO: have a set of colours that may be used, randomly pick from that set.
    private Color randomColor() {
        return Color.color(Math.random(), Math.random(), Math.random());
    }

    private void build(ShellModel shellModel, MapModel mapModel) {

        setId(Constants.ComponentIds.SHELL);

        log.setId(Constants.ComponentIds.SHELL_LOG);

        log.setWrapText(true);
        log.setMinWidth(Constants.SHELL_WIDTH);
        log.setMaxWidth(Constants.SHELL_WIDTH);
        setVgrow(log, Priority.ALWAYS);

        EventHandler<ActionEvent> eventHandler = event -> {
            if (gameState.validate(inputLine.getText())) {
                switch (gameState) {
                    case NEW:
                        appendLogText(Constants.Prompts.NAME + "(Player one)");
                        break;
                    case PLAYER_ONE_NAME:
                        appendLogText("Nice to meet you, " + inputLine.getText());
                        appendLogText(Constants.Prompts.NAME + "(Player two)");

                        Player playerOne = new Player(inputLine.getText(), randomColor());
                        mapModel.addPlayer(playerOne);
                        break;
                    case PLAYER_TWO_NAME:
                        appendLogText("And you, " + inputLine.getText());
                        appendLogText("Lets get started!");

                        Player playerTwo = new Player(inputLine.getText(), randomColor());
                        mapModel.addPlayer(playerTwo);

                        mapModel.initializeGame();
                        break;
                    case QUIT:
                        appendLogText("Thanks for playing!");
                        inputLine.setDisable(true);
                    default:
                        System.out.println("Should never be reached");
                }

                // Move to next game state
                gameState = gameState.next();
            } else {
                appendLogText("Enter Valid Input!");
            }
            inputLine.clear();
        };
        inputLine.setOnAction(eventHandler);
        inputLine.setId(Constants.ComponentIds.SHELL_INPUT);

        getChildren().addAll(log, inputLine);
        setAlignment(Pos.BOTTOM_RIGHT);
    }

    @Override
    public void update(Observable o, Object arg) {
        ShellModel shellModel = ((ShellModel) o);

        appendLogText("ShellComponent update");
    }
}
