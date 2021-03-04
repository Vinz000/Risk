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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

import static common.Constants.*;
import static jdk.nashorn.internal.objects.NativeArray.length;

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
        for (int i = 0; i < (NUM_NEUTRAL_PLAYERS + 1); i++) {
            Player player = playerModel.getCurrentPlayer();

            // Before reinforcing
            boolean isNeutral = (player instanceof NeutralPlayer);

            String prefixMessage = isNeutral ? "Choose country Owned by " : "\nYour Turn ";
            String message = prefixMessage + player.getName();

            shellModel.notify(message);
            shellModel.notify("Place down reinforcements.");

            // Toggle highlight (on)
            for (Country ownedCountry : player.getOwnedCountries()) {
                uiAction(() -> mapModel.highlightCountry(ownedCountry));
            }

            // Reinforcing
            response = shellModel.prompt(Validators.currentPlayerOccupies);
            Optional<Country> country = mapModel.getCountryByName(response);
            int reinforcement = player.getReinforcement();

            country.ifPresent(validCountry -> uiAction(() -> mapModel.updateCountryArmyCount(validCountry, reinforcement)));

            shellModel.notify("Successfully placed reinforcements.");

            // Toggle highlight (off)
            for (Country ownedCountry : player.getOwnedCountries()) {
                uiAction(() -> mapModel.highlightCountry(ownedCountry));
            }

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

            shellModel.notify("\nYour turn " + currentPlayer.getName());

            boolean skipInvasion = skipOrFight(currentPlayer);
            boolean currentBattle = false;

            //Player chooses to invade or skip combat
            while (!skipInvasion) {
                selectAttackingCountry(currentPlayer);
                selectDefendingCountry(currentPlayer);
                //Safety net if for some reason combat simulation doesnt update occupier
                while (!currentBattle) {
                    if (zeroDefenceCheck(mapModel.getSelectedCountries().get(1))) {
                        setAttackingTroops(mapModel.getSelectedCountries().get(0));
                        setDefendingTroops(mapModel.getSelectedCountries().get(1));
                        combat();
                        currentBattle = continueInvasion(currentPlayer);
                    } else {
                        mapModel.getSelectedCountries().get(1).get().setOccupier(currentPlayer);
                    }
                }
                skipInvasion = skipOrFight(currentPlayer);
            }

            playerModel.changeTurn();
        }
    }

    private boolean skipOrFight(Player player) {
        shellModel.notify(player.getName() + " type fight to engage in combat or type skip to end your turn.");
        response = shellModel.prompt(Validators.skipOrFight);
        return response.toLowerCase().contains("s");
    }

    private boolean continueInvasion(Player player) {
        shellModel.notify(player.getName() + " type fight to continue assault or skip to end");
        response = shellModel.prompt(Validators.skipOrFight);
        return response.toLowerCase().contains("s");
    }

    private void selectAttackingCountry(Player player) {
        shellModel.notify(player.getName() + " select which country you want to attack with");

        // Toggle highlight (on)
        for (Country ownedCountry : player.getOwnedCountries()) {
            uiAction(() -> mapModel.highlightCountry(ownedCountry));
        }

        response = shellModel.prompt(Validators.compose(Validators.currentPlayerOccupies, Validators.singleUnit));
        Optional<Country> attackingCountry = mapModel.getCountryByName(response);
        attackingCountry.ifPresent(isValid -> mapModel.addSelectedCountries(attackingCountry));

        // Toggle highlight (off)
        for (Country ownedCountry : player.getOwnedCountries()) {
            uiAction(() -> mapModel.highlightCountry(ownedCountry));
        }
    }

    private void selectDefendingCountry(Player player) {

        shellModel.notify(player.getName() + " select an adjacent country to invade.");

        /** TO DO
         * I would like to highlight adjacent countries however it had to be stored as
         * an optional country. Any ideas?
         */

        //Toggle highlight for adjacent countries (on)
            /*
            int[] adjacentCountries = attackingCountry.get().getAdjCountries();
            for (int adjacentIndex : adjacentCountries) {
                Optional<Country> adjacentCountry = mapModel.getCountryByName(COUNTRY_NAMES[adjacentIndex]);
                uiAction(() -> mapModel.highlightCountry(adjacentCountry));
           } */

        response = shellModel.prompt(Validators.compose(Validators.adjacentCountry,
                Validators.currentPlayerDoesNotOccupy));

        Optional<Country> defendingCountry = mapModel.getCountryByName(response);
        defendingCountry.ifPresent(isValid -> mapModel.addSelectedCountries(defendingCountry));
    }

    private void setAttackingTroops(Optional<Country> attackingCountry) {
        shellModel.notify("How many troops will you attack with?");
        response = shellModel.prompt(Validators.compose(
                Validators.hasArmy, Validators.isInt, Validators.threeUnitCheck,
                Validators.appropriateForce));

        int invasionForce = Integer.parseInt(response);
        attackingCountry.get().updateForceCount(invasionForce);
        attackingCountry.get().updateArmyCount(attackingCountry.get().getArmyCount() - invasionForce);
    }

    private void setDefendingTroops(Optional<Country> defendingCountry) {
        shellModel.notify("How many troops will you defend with?");
        response = shellModel.prompt(Validators.compose(
                Validators.hasArmy, Validators.isInt, Validators.twoUnitCheck));

        int defenderForce = Integer.parseInt(response);
        defendingCountry.get().updateForceCount(defenderForce);
        defendingCountry.get().updateArmyCount(defendingCountry.get().getArmyCount() - defenderForce);
    }

    private void combat() {
        Dice dice = new Dice();

        Optional<Country> attackingCountry = mapModel.getSelectedCountries().get(0);
        Optional<Country> defendingCountry = mapModel.getSelectedCountries().get(1);

        Player attacker = attackingCountry.get().getOccupier();
        Player defender = defendingCountry.get().getOccupier();

        int attackingForce = attackingCountry.get().getForceCount();
        int defendingForce = defendingCountry.get().getForceCount();

        int[] attackerDices = new int[attackingForce];
        int[] defenderDices = new int[attackingForce];

        String[] attackerRolledNotification = new String[attackingForce];
        String[] defenderRolledNotification = new String[defendingForce];

        Function<Player, String> createRolledNotification = (player) -> player.getName() + " "
                + Constants.Notifications.ROLLED + dice.toString();

        for (int attackRolls = 0; attackRolls < attackingForce; attackRolls++) {
            attackerDices[attackRolls] = dice.getRollSum(1);
            attackerRolledNotification[attackRolls] = createRolledNotification.apply(attacker);
        }
        for (int defendRolls = 0; defendRolls < defendingForce; defendRolls++) {
            defenderDices[defendRolls] = dice.getRollSum(1);
            defenderRolledNotification[defendRolls] = createRolledNotification.apply(defender);
        }

        shellModel.notify(Arrays.toString(attackerRolledNotification));
        shellModel.notify(Arrays.toString(defenderRolledNotification));

        Arrays.sort(attackerDices);
        Arrays.sort(defenderDices);
        reverse(attackerDices, attackerDices.length);
        reverse(defenderDices, defenderDices.length);

        combatSimulation(attackerDices, defenderDices, attackingCountry, defendingCountry);

    }

    private void combatSimulation(int[] attackerDices, int[] defenderDices,
                                  Optional<Country> attackingCountry, Optional<Country> defendingCountry) {
        int attackerVictoryPoints = 0;
        int defenderVictoryPoints = 0;
        int compare = 0;

        for (compare = 0; compare < attackerDices.length; compare++) {
            if (attackerDices[compare] > defenderDices[compare]) {
                attackerVictoryPoints++;
                defendingCountry.get().updateForceCount(-1);
            } else {
                defenderVictoryPoints++;
                attackingCountry.get().updateForceCount(-1);
            }
        }

        defendingCountry.get().updateArmyCount(
                defendingCountry.get().getArmyCount() + defendingCountry.get().getForceCount());
        attackingCountry.get().updateArmyCount(
                attackingCountry.get().getArmyCount() + defendingCountry.get().getForceCount());

        victoryCheck(attackerVictoryPoints, defenderVictoryPoints, attackingCountry, defendingCountry);
    }

    private void victoryCheck(int attackerVictoryPoints, int defenderVictoryPoints,
                              Optional<Country> attackingCountry, Optional<Country> defendingCountry) {

        if (attackerVictoryPoints > defenderVictoryPoints) {
            if (defendingCountry.get().getArmyCount() == 0) {
                countryTakeOver(attackingCountry, defendingCountry);

            } else {
                shellModel.notify("Battle won by " + attackingCountry.get().getOccupier().getName() +
                        " but no land taken.");
            }
        } else {
            shellModel.notify(defendingCountry.get().getCountryName() + " has been defended successfully");
        }
    }

    private void countryTakeOver(Optional<Country> attackingCountry, Optional<Country> defendingCountry) {
        shellModel.notify(defendingCountry.get().getCountryName() + " has been taken by "
                + attackingCountry.get().getOccupier().getName());
        defendingCountry.get().setOccupier(attackingCountry.get().getOccupier());
        shellModel.notify("How many units would you like to move to the new country?");
        response = shellModel.prompt(Validators.compose(
                Validators.isInt, Validators.hasArmy, Validators.singleUnit));
        defendingCountry.get().updateArmyCount(Integer.parseInt(response));
        attackingCountry.get().updateArmyCount(attackingCountry.get().getArmyCount() - Integer.parseInt(response));
    }

    private boolean zeroDefenceCheck(Optional<Country> defendingCountry) {
        boolean defencesLeft = true;

        if (defendingCountry.get().getArmyCount() == 0) {
            shellModel.notify(defendingCountry.get().getCountryName() + " has no troops left to defend.");
            defendingCountry.get().setOccupier(playerModel.getCurrentPlayer());
            defencesLeft = false;
        }
        return defencesLeft;
    }

    private void reverse(int[] attackerDices, int length) {
        int[] temp = new int[length];
        int j = length;
        for (int i = 0; i < length; i++) {
            temp[j - 1] = attackerDices[i];
            j = j - 1;
        }
    }

    // Game logic sequence
    public void start() {
        Thread gameThread = new Thread(this);
        gameThread.setDaemon(true);
        gameThread.start();
    }
}
