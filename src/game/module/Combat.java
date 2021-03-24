package game.module;

import common.Dice;
import common.validation.Validators;
import deck.Card;
import deck.Deck;
import map.country.Country;
import player.NeutralPlayer;
import player.Player;
import player.model.PlayerModel;

import java.util.List;
import java.util.Optional;

public class Combat extends Module {
    // TODO: Change response to more meaningful variable name
    String response;

    public Combat() {
    }

    public boolean skipCombat(Player player) {
        shellModel.notify(player.getName() + " type fight to engage in combat or type skip to end your turn.");
        response = shellModel.prompt(Validators.skipOrFight);
        return response.toLowerCase().contains("s");
    }

    public boolean stopCombat(Player player) {
        shellModel.notify(player.getName() + " type fight to continue assault or skip to end");
        response = shellModel.prompt(Validators.skipOrFight);
        return response.toLowerCase().contains("s");
    }

    public void selectAttackingCountry(Player player) {
        shellModel.notify(player.getName() + " select which country you want to attack with");

        mapModel.highlightCountries(player.getOwnedCountries());

        response = shellModel.prompt(Validators.validAttackingCountry);
        Optional<Country> nullableAttackingCountry = mapModel.getCountryByName(response);
        nullableAttackingCountry.ifPresent(mapModel::addCombatant);

        mapModel.highlightCountries(player.getOwnedCountries());
    }

    public void selectDefendingCountry(Player player) {

        shellModel.notify(player.getName() + " select an adjacent country to invade.");

        int[] adjacentCountryIndexes = mapModel.getAttackingCountry().getAdjCountries();
        mapModel.highlightCountries(adjacentCountryIndexes);

        response = shellModel.prompt(Validators.validDefendingCountry);

        Optional<Country> nullableDefendingCountry = mapModel.getCountryByName(response);
        nullableDefendingCountry.ifPresent(mapModel::addCombatant);

        mapModel.highlightCountries(adjacentCountryIndexes);
    }

    public void setAttackingTroops(Country attackingCountry) {
        String attackingPlayerName = attackingCountry.getOccupier().getName();
        shellModel.notify(attackingPlayerName + " how many troops will you attack with?");
        response = shellModel.prompt(Validators.validAttackingTroops);

        int invasionForce = Integer.parseInt(response);
        attackingCountry.updateForceCount(invasionForce);
        mapModel.updateCountryArmyCount(attackingCountry, -invasionForce);
    }

    public void setDefendingTroops(Country defendingCountry) {
        String defendingPlayerName = defendingCountry.getOccupier().getName();
        shellModel.notify(defendingPlayerName + " how many troops will you defend with?");
        response = shellModel.prompt(Validators.validDefendingTroops);

        int defenderForce = Integer.parseInt(response);
        defendingCountry.updateForceCount(defenderForce);
        mapModel.updateCountryArmyCount(defendingCountry, -defenderForce);
    }

    public boolean initiateCombat(Country attackingCountry, Country defendingCountry) {
        int attackingForce = attackingCountry.getForceCount();
        int defendingForce = defendingCountry.getForceCount();

        List<Integer> attackerDice = Dice.roll(attackingForce);
        shellModel.notify(attackerDice.toString());
        List<Integer> defenderDice = Dice.roll(defendingForce);
        shellModel.notify(defenderDice.toString());

        boolean humanPlayerDefeated = diceComparison(attackerDice, defenderDice, attackingCountry, defendingCountry);
        return humanPlayerDefeated;
    }

    public boolean diceComparison(List<Integer> attackerDice, List<Integer> defenderDice,
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
        mapModel.updateCountryArmyCount(attackingCountry, remainingAttackingForce);
        mapModel.updateCountryArmyCount(defendingCountry, remainingDefendingForce);

        boolean humanPlayerDefeated = victoryChecker(
                attackerVictoryPoints, defenderVictoryPoints, attackingCountry, defendingCountry);
        attackingCountry.emptyForceCount();
        defendingCountry.emptyForceCount();
        return humanPlayerDefeated;
    }

    public boolean victoryChecker(int attackerVictoryPoints, int defenderVictoryPoints,
                                  Country attackingCountry, Country defendingCountry) {

        boolean humanPlayerDefeated = false;
        if (attackerVictoryPoints > defenderVictoryPoints) {
            if (defendingCountry.getArmyCount() == 0 && defendingCountry.getForceCount() == 0) {
                humanPlayerDefeated = countryTakeOver(attackingCountry, defendingCountry);
            } else {
                shellModel.notify("Battle won by " + attackingCountry.getOccupier().getName() +
                        " but no land taken.");
            }
        } else {
            shellModel.notify(defendingCountry.getCountryName() + " has been defended successfully.");
        }
        return humanPlayerDefeated;
    }

    public boolean countryTakeOver(Country attackingCountry, Country defendingCountry) {

        shellModel.notify(defendingCountry.getCountryName() + " has been taken by "
                + attackingCountry.getOccupier().getName());
        Player defendingPlayer = defendingCountry.getOccupier();
        Player attackingPlayer = attackingCountry.getOccupier();

        defendingPlayer.removeCountry(defendingCountry);
        attackingPlayer.addCountry(defendingCountry);

        mapModel.setCountryOccupier(defendingCountry, attackingPlayer);

        playerEliminationCheck(defendingPlayer);

        if (defendingPlayer instanceof NeutralPlayer) {
            playerEliminationCheck(defendingPlayer);
        } else {
            if (playerEliminationCheck(defendingPlayer)) {
                return true;
            }
        }
        shellModel.notify("How many units would you like to move to the new country?");

        // TODO: Check if there is at least 1 troop left over when moving troops to new country
        response = shellModel.prompt(Validators.validReinforcement);

        int force = Integer.parseInt(response);
        mapModel.updateCountryArmyCount(defendingCountry, force);
        mapModel.updateCountryArmyCount(attackingCountry, -force);

        CardUsage.addCard(attackingPlayer);

        Card drawnCard = attackingPlayer.getMostRecentCard(attackingPlayer.getCards());
        shellModel.notify("You have earned the card: " +
                drawnCard.getType().toString() + " " + drawnCard.getCountryName());

        return false;
    }

    public boolean playerEliminationCheck(Player player) {
        boolean playerWasEliminated = false;
        PlayerModel playerModel = PlayerModel.getInstance();
        if (player.getOwnedCountries().isEmpty()) {
            shellModel.notify(player.getName() + " has been eliminated.");
            playerModel.removePlayer(player);
            playerWasEliminated = true;
        }
        return playerWasEliminated;
    }
}
