package shell;

import common.Constants;
import game.GameRoot;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;

import java.util.Observable;
import java.util.Observer;

public class ShellComponent extends BorderPane implements Observer {
    public ShellComponent(ShellModel shellModel) {
        super();
        build(shellModel);
    }

    private void build(ShellModel shellModel) {

        TextField input = new TextField();
        TilePane tile = new TilePane();
        Label prompts = new Label("Player 1. " + Constants.Prompts.NAME);
        Label log = new Label();

        EventHandler<ActionEvent> event = event1 -> {

            if (shellModel.getPlayer1() == null) {
                shellModel.setPlayer1(input.getText());
                prompts.setText("Player 2. " + Constants.Prompts.NAME);
            } else if (shellModel.getPlayer2() == null) {
                shellModel.setPlayer2(input.getText());
                prompts.setText(Constants.Prompts.OPTION);
            } else {
                log.setText(log.getText() + "\n" + input.getText());
            }
            input.clear();
            System.out.print(shellModel);

        };

        input.setOnAction(event);
        tile.getChildren().add(log);
        tile.getChildren().add(input);
        tile.getChildren().add(prompts);

        tile.setAlignment(Pos.BOTTOM_RIGHT);
        tile.setOrientation(Orientation.HORIZONTAL);

        setCenter(new GameRoot());
        setRight(tile);
    }

    @Override
    public void update(Observable o, Object arg) {
        ShellModel shellModel = ((ShellModel) o);

    }
}
