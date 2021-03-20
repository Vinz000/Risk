package game.module;

import common.Constants;
import common.Dice;
import common.validation.Validators;
import deck.Card;
import deck.Deck;
import map.country.Country;
import player.HumanPlayer;
import player.NeutralPlayer;
import player.Player;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

import static common.Constants.*;

public class SetUp extends Module{

    public SetUp() {}

    public void getPlayerOne() {
        shellModel.notify(Notifications.NAME + "(P1)");
        String playerName = shellModel.prompt(Validators.nonEmpty);
        Player humanPlayerOne = new HumanPlayer(playerName, Colors.PLAYER_1_COLOR);
        playerModel.addPlayer(humanPlayerOne);

        playerModel.createNeutralPlayers();
        shellModel.notify("Welcome " + humanPlayerOne.getName());
    }

    public void getPlayerTwo() {
        shellModel.notify(Constants.Notifications.NAME + "(P2)");
        String name = shellModel.prompt(Validators.nonEmpty);
        Player humanPlayerTwo = new HumanPlayer(name, Constants.Colors.PLAYER_2_COLOR);
        playerModel.addPlayer(humanPlayerTwo);
        shellModel.notify("Welcome " + humanPlayerTwo.getName());
    }

    public void selectFirstPlayer() {
        shellModel.notify("\n" + "Rolling dice to determine who goes first...");

        int playerOneDiceSum;
        int playerTwoDiceSum;
        List<Player> players = playerModel.getPlayers();

        BiFunction<Player, List<Integer>, String> createRolledNotification = (player, rolledDice) -> player.getName() + " "
                + Constants.Notifications.ROLLED + Dice.toString(rolledDice);

        do {
            List<Integer> playerOneRolledDice = Dice.roll(2);
            playerOneDiceSum = Dice.sumDice(playerOneRolledDice);
            String playerOneRolledNotification = createRolledNotification.apply(players.get(0), playerOneRolledDice);
            shellModel.notify(playerOneRolledNotification);

            List<Integer> playerTwoRolledDice = Dice.roll(2);
            playerTwoDiceSum = Dice.sumDice(playerTwoRolledDice);
            String playerTwoRolledNotification = createRolledNotification.apply(players.get(players.size() - 1), playerTwoRolledDice);
            shellModel.notify(playerTwoRolledNotification);

            if (playerOneDiceSum == playerTwoDiceSum) {
                shellModel.notify("\nRolled the same number\nRolling again!");
            } else if (playerOneDiceSum < playerTwoDiceSum) {
                // player One goes first by default but if player One rolls a lower sum,
                // then we change the turn so that the current player is now player Two.
                Collections.swap(players, 0, players.size() - 1);
            }

            uiAction(playerModel::updatePlayerIndicator);
            uiAction(playerModel::showPlayerIndicator);

        } while (playerOneDiceSum == playerTwoDiceSum);

        String currentPlayerName = players.get(0).getName();
        shellModel.notify(String.format("%s rolled higher, so is going first", currentPlayerName));

    }
}
