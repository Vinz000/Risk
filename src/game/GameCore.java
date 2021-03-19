package game;

import common.Constants;
import common.Dice;
import common.validation.Validators;
import deck.Card;
import deck.Deck;
import javafx.application.Platform;
import javafx.concurrent.Task;
import map.country.Country;
import map.model.MapModel;
import player.HumanPlayer;
import player.NeutralPlayer;
import player.Player;
import player.model.PlayerModel;
import shell.model.ShellModel;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

import static common.Constants.*;

public class GameCore extends Task<Void> {

    private static final ShellModel shellModel = ShellModel.getInstance();
    private static final MapModel mapModel = MapModel.getInstance();
    private static final PlayerModel playerModel = PlayerModel.getInstance();

    private String response;

    private void uiAction(Runnable action) {
        Platform.runLater(action);
    }

    @Override
    protected Void call() {

        shellModel.notify(Constants.Notifications.WELCOME);
        Deck deck = Deck.getInstance();

        /*
        Creating players
         */
        shellModel.notify(Constants.Notifications.NAME + "(P1)");
        response = shellModel.prompt(Validators.nonEmpty);
        Player humanPlayerOne = new HumanPlayer(response, Constants.Colors.PLAYER_1_COLOR);
        playerModel.addPlayer(humanPlayerOne);
        shellModel.notify("Welcome " + humanPlayerOne.getName());

        playerModel.createNeutralPlayers();

        shellModel.notify(Constants.Notifications.NAME + "(P2)");
        response = shellModel.prompt(Validators.nonEmpty);
        Player humanPlayerTwo = new HumanPlayer(response, Constants.Colors.PLAYER_2_COLOR);
        playerModel.addPlayer(humanPlayerTwo);
        shellModel.notify("Welcome " + humanPlayerTwo.getName());

        /*
        Drawing initial territory cards
         */
        shellModel.notify(Constants.Notifications.TERRITORY);
        shellModel.notify(Constants.Notifications.TERRITORY_OPTION);
        response = shellModel.prompt(Validators.yesNo);
        boolean showDraw = response.toLowerCase().contains("y");

        for (int i = 0; i < INIT_COUNTRIES_PLAYER; i++) {
            boolean neutralsMissingCards = i < INIT_COUNTRIES_NEUTRAL;

            for (Player player : playerModel.getPlayers()) {
                if (neutralsMissingCards || player instanceof HumanPlayer) {
                    Optional<Card> nullableCard = deck.drawCard();

                    nullableCard.ifPresent(drawnCard -> {
                        player.addCard(drawnCard);
                        if (showDraw) {
                            String drawnCountryName = drawnCard.getCountryName();
                            String playerName = player.getName();
                            shellModel.notify(playerName + Constants.Notifications.DRAWN + drawnCountryName);
                        }
                    });
                }
            }
        }

        playerModel.assignInitialCountries();
        deck.addWildcards();
        deck.shuffle();
        mapModel.showCountryComponents();

        /*
        Roll dice to determine which player chooses countries to reinforce first
         */
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
        String finalCurrentPlayerName1 = currentPlayerName;
        shellModel.notify(String.format("%s rolled higher, so is going first", finalCurrentPlayerName1));

        /*
        Initial reinforcement of countries
         */
        for (int i = 0; i < (NUM_NEUTRAL_PLAYERS + 1) /** 9*/; i++) {
            Player player = playerModel.getCurrentPlayer();

            // Before reinforcing
            boolean isNeutral = (player instanceof NeutralPlayer);

            String prefixMessage = isNeutral ? "Choose country Owned by " : "\nYour Turn ";
            String message = prefixMessage + player.getName();

            shellModel.notify(message);
            shellModel.notify("Place down reinforcements.");

            mapModel.highlightCountries(player.getOwnedCountries());

            // Reinforcing
            response = shellModel.prompt(Validators.currentPlayerOccupies);
            Optional<Country> nullableDestinationCountry = mapModel.getCountryByName(response);
            int reinforcement = player.getReinforcement();

            nullableDestinationCountry.ifPresent(validCountry -> uiAction(() -> mapModel.updateCountryArmyCount(validCountry, reinforcement)));

            shellModel.notify("Successfully placed reinforcements.");

            mapModel.highlightCountries(player.getOwnedCountries());

            playerModel.changeTurn();
        }

        /*
        Roll dice to determine which player has the first turn
         */
        shellModel.notify("\n" + "Rolling dice to determine who goes first...");

        players = playerModel.getPlayers();

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

        currentPlayerName = players.get(0).getName();
        String finalCurrentPlayerName = currentPlayerName;
        shellModel.notify(String.format("%s rolled higher, so is going first", finalCurrentPlayerName));

        /*
        Game loop
         */
        while (true) {
            Player currentPlayer = playerModel.getCurrentPlayer();


            while (!skipCombat(currentPlayer)) {
                selectAttackingCountry(currentPlayer);
                selectDefendingCountry(currentPlayer);

                Country attackingCountry = mapModel.getAttackingCountry();
                Country defendingCountry = mapModel.getDefendingCountry();

                do {
                    setAttackingTroops(attackingCountry);
                    setDefendingTroops(defendingCountry);
                    combat(attackingCountry, defendingCountry);
                } while (!defendingCountry.getOccupier().equals(currentPlayer) &&
                        !stopCombat(currentPlayer));

                mapModel.clearCombatants();
            }
            playerModel.changeTurn();
        }

    }

    private boolean skipCombat(Player player) {
        shellModel.notify(player.getName() + " type fight to engage in combat or type skip to end your turn.");
        response = shellModel.prompt(Validators.skipOrFight);
        return response.toLowerCase().contains("s");
    }

    private boolean stopCombat(Player player) {
        shellModel.notify(player.getName() + " type fight to continue assault or skip to end");
        response = shellModel.prompt(Validators.skipOrFight);
        return response.toLowerCase().contains("s");
    }

    private void selectAttackingCountry(Player player) {
        shellModel.notify(player.getName() + " select which country you want to attack with");

        mapModel.highlightCountries(player.getOwnedCountries());

        response = shellModel.prompt(Validators.compose(Validators.currentPlayerOccupies, Validators.singleUnit));
        Optional<Country> nullableAttackingCountry = mapModel.getCountryByName(response);
        nullableAttackingCountry.ifPresent(mapModel::addCombatant);

        mapModel.highlightCountries(player.getOwnedCountries());
    }

    private void selectDefendingCountry(Player player) {

        shellModel.notify(player.getName() + " select an adjacent country to invade.");

        int[] adjacentCountryIndexes = mapModel.getAttackingCountry().getAdjCountries();
        mapModel.highlightCountries(adjacentCountryIndexes);

        response = shellModel.prompt(Validators.compose(Validators.adjacentCountry,
                Validators.currentPlayerDoesNotOccupy));

        Optional<Country> nullableDefendingCountry = mapModel.getCountryByName(response);
        nullableDefendingCountry.ifPresent(mapModel::addCombatant);

        mapModel.highlightCountries(adjacentCountryIndexes);
    }

    private void setAttackingTroops(Country attackingCountry) {
        String attackingPlayerName = attackingCountry.getOccupier().getName();
        shellModel.notify(attackingPlayerName + " how many troops will you attack with?");
        response = shellModel.prompt(Validators.compose(
                Validators.threeUnitCheck, Validators.appropriateForce, Validators.isInt));

        int invasionForce = Integer.parseInt(response);
        attackingCountry.updateForceCount(invasionForce);
        uiAction(() -> mapModel.updateCountryArmyCount(attackingCountry, -invasionForce));
    }

    private void setDefendingTroops(Country defendingCountry) {
        String defendingPlayerName = defendingCountry.getOccupier().getName();
        shellModel.notify(defendingPlayerName + " how many troops will you defend with?");
        response = shellModel.prompt(Validators.compose(Validators.twoUnitCheck, Validators.isInt));

        int defenderForce = Integer.parseInt(response);
        defendingCountry.updateForceCount(defenderForce);
        uiAction(() -> mapModel.updateCountryArmyCount(defendingCountry, -defenderForce));
    }

    private void combat(Country attackingCountry, Country defendingCountry) {

        int attackingForce = attackingCountry.getForceCount();
        int defendingForce = defendingCountry.getForceCount();

        List<Integer> attackerDice = Dice.roll(attackingForce);
        shellModel.notify(attackerDice.toString());
        List<Integer> defenderDice = Dice.roll(defendingForce);
        shellModel.notify(defenderDice.toString());


        diceComparison(attackerDice, defenderDice, attackingCountry, defendingCountry);
    }

    private void diceComparison(List<Integer> attackerDice, List<Integer> defenderDice,
                                Country attackingCountry, Country defendingCountry) {

        String attackingPlayerName = attackingCountry.getOccupier().getName();
        String defendingPlayerName = defendingCountry.getOccupier().getName();

        int attackerVictoryPoints = 0;
        int defenderVictoryPoints = 0;
        int dicePool = Math.min(defenderDice.size(), attackerDice.size());

        for (int dieIndex = 0; dieIndex < dicePool; dieIndex++) {
            if (attackerDice.get(dieIndex) > defenderDice.get(dieIndex)) {
                shellModel.notify(attackingPlayerName + " won one battle");
                attackerVictoryPoints++;
                defendingCountry.destroyedUnit();
            } else {
                shellModel.notify(defendingPlayerName + " won one battle");
                defenderVictoryPoints++;
                attackingCountry.destroyedUnit();
            }
        }

        int remainingAttackingForce = attackingCountry.getForceCount();
        int remainingDefendingForce = defendingCountry.getForceCount();
        uiAction(() -> mapModel.updateCountryArmyCount(attackingCountry, remainingAttackingForce));
        uiAction(() -> mapModel.updateCountryArmyCount(defendingCountry, remainingDefendingForce));

        victoryChecker(attackerVictoryPoints, defenderVictoryPoints, attackingCountry, defendingCountry);
        attackingCountry.emptyForceCount();
        defendingCountry.emptyForceCount();
    }

    private void victoryChecker(int attackerVictoryPoints, int defenderVictoryPoints,
                                Country attackingCountry, Country defendingCountry) {

        if (attackerVictoryPoints > defenderVictoryPoints) {
            if (defendingCountry.getArmyCount() == 0 && defendingCountry.getForceCount() == 0) {
                countryTakeOver(attackingCountry, defendingCountry);

            } else {
                shellModel.notify("Battle won by " + attackingCountry.getOccupier().getName() +
                        " but no land taken.");
            }
        } else {
            shellModel.notify(defendingCountry.getCountryName() + " has been defended successfully.");
        }
    }

    private void countryTakeOver(Country attackingCountry, Country defendingCountry) {
        shellModel.notify(defendingCountry.getCountryName() + " has been taken by "
                + attackingCountry.getOccupier().getName());
        uiAction(() -> mapModel.setCountryOccupier(defendingCountry, attackingCountry.getOccupier()));
        shellModel.notify("How many units would you like to move to the new country?");
        response = shellModel.prompt(Validators.compose(Validators.enoughTroops, Validators.isInt));

        int force = Integer.parseInt(response);
        uiAction(() -> mapModel.updateCountryArmyCount(defendingCountry, force));
        uiAction(() -> mapModel.updateCountryArmyCount(attackingCountry, -force));
    }

    // Game logic sequence
    public void start() {
        Thread gameThread = new Thread(this);
        gameThread.setDaemon(true);
        gameThread.start();
    }
}
