package src;

import java.util.ArrayList;
import java.util.List;

public class Team7HelperFunctions {

    private static BoardAPI board;
    private static PlayerAPI player;

    Team7HelperFunctions(BoardAPI inBoard, PlayerAPI inPlayer) {
        board = inBoard;
        player = inPlayer;
    }

    public static boolean anyEnemyNeighbours(int attackCountryId) {
        int[] adjacentCountryIds = GameData.ADJACENT[attackCountryId];

        for (int adjacentCountryId : adjacentCountryIds) {
            if (board.getOccupier(adjacentCountryId) != player.getId()) {
                return true;
            }
        }
        return false;
    }

    public static List<Integer> getOwnedCountryIds() {
        List<Integer> ownedCountryIds = new ArrayList<>();

        for (int i = 0; i < GameData.COUNTRY_NAMES.length; i++) {
            if (board.getOccupier(i) == player.getId()) {
                ownedCountryIds.add(i);
            }
        }
        return ownedCountryIds;
    }

    public static List<Integer> checkForSingleArmyCountry(List<Integer> ownedCountriesIds) {
        List<Integer> attackingAndDefending = new ArrayList<>();

        for (Integer ownedCountriesId : ownedCountriesIds) {
            int[] adjacentCountryIds = GameData.ADJACENT[ownedCountriesId];
            int countryId = singleArmyCount(adjacentCountryIds);
            if (countryId != -1) {
                attackingAndDefending.add(ownedCountriesId);
                attackingAndDefending.add(countryId);
                return attackingAndDefending;
            }
        }
        attackingAndDefending.add(-1);
        return attackingAndDefending;
    }

    public static int singleArmyCount(int[] adjacentCountryIds) {
        for (int adjacentCountryId : adjacentCountryIds) {
            if (board.getNumUnits(adjacentCountryId) == 1) {
                return adjacentCountryId;
            }
        }
        return -1;
    }

    public static int chooseAttackingArmySize(int attackingCountryId) {
        int availableUnits = board.getNumUnits(attackingCountryId) - 1;
        return Math.min(availableUnits, 3);
    }

    public static int chooseDefendingArmySize(int defendingCountryId) {
        int availableUnits = board.getNumUnits(defendingCountryId);
        return Math.min(availableUnits, 2);
    }

    public static int lowestArmyCount(int[] adjacentCountryIds) {
        int countryId = 0;
        int initialUnits = 0;
        for (int adjacentCountryId : adjacentCountryIds) {
            int newUnits = board.getNumUnits(adjacentCountryId);
            if (newUnits > initialUnits) {
                countryId = adjacentCountryId;
            }
        }
        return countryId;
    }

    public static int leastEnemyAdjacentCountriesFromList(List<Integer> ownedCountriesIds) {
        int countryId = 0;

        for (int i = 0; i < ownedCountriesIds.size() - 1; i++) {
            if (enemyAdjacentCountryNumber(GameData.ADJACENT[ownedCountriesIds.get(i)]) <
                    enemyAdjacentCountryNumber(GameData.ADJACENT[ownedCountriesIds.get(i + 1)])) {
                countryId = i;
            }
        }

        return countryId;
    }

    public static int leastEnemyAdjacentCountriesFromArray(int[] continent) {
        int countryId = 0;

        for (int i = 0; i < continent.length - 1; i++) {
            if (enemyAdjacentCountryNumber(GameData.ADJACENT[continent[i]]) <
                    enemyAdjacentCountryNumber(GameData.ADJACENT[continent[i + 1]])) {
                countryId = i;
            }
        }
        return countryId;
    }

    public static int enemyAdjacentCountryNumber(int[] countryIds) {
        int count = 0;
        for (int countryId : countryIds) {
            if (board.getOccupier(countryId) != player.getId()) {
                count++;
            }
        }
        return count;
    }

    public static List<Integer> continentSpecificAttack(List<Integer> ownedCountriesIds) {
        int chosenContinent = chooseContinentToAttack();
        int chosenAttackingCountry;
        int chosenDefendingCountry;
        List<Integer> combatInput = new ArrayList<>();

        if (chosenContinent == 6) {
            chosenAttackingCountry = leastEnemyAdjacentCountriesFromList(ownedCountriesIds);
        } else {
            int[] continent = GameData.CONTINENT_COUNTRIES[chosenContinent];
            chosenAttackingCountry = leastEnemyAdjacentCountriesFromArray(continent);
        }
        chosenDefendingCountry = invasionDecision(chosenAttackingCountry);
        combatInput.add(chosenAttackingCountry);
        combatInput.add(chosenDefendingCountry);
        if (outOfOurLeagueCheck(chosenAttackingCountry, chosenDefendingCountry)) {
            combatInput.add(-1);
            return combatInput;
        }
        combatInput.add(chooseAttackingArmySize(chosenAttackingCountry));
        return combatInput;
    }

    public static boolean outOfOurLeagueCheck(int chosenAttackingCountryId, int chosenDefendingCountryId) {
        return board.getNumUnits(chosenDefendingCountryId) > board.getNumUnits(chosenAttackingCountryId) ||
                board.getNumUnits(chosenAttackingCountryId) < 3;
    }

    public static int invasionDecision(int chosenAttackingCountry) {
        return lowestArmyCount(GameData.ADJACENT[chosenAttackingCountry]);
    }

    public static int chooseContinentToAttack() {
        int[] northAmerica = GameData.CONTINENT_COUNTRIES[0];
        int[] europe = GameData.CONTINENT_COUNTRIES[1];
        int[] asia = GameData.CONTINENT_COUNTRIES[2];
        int[] australia = GameData.CONTINENT_COUNTRIES[3];
        int[] southAmerica = GameData.CONTINENT_COUNTRIES[4];
        int[] africa = GameData.CONTINENT_COUNTRIES[5];

        if (ownsEnoughOfContinent(asia)) {
            return 2;
        } else if (ownsEnoughOfContinent(australia)) {
            return 3;
        } else if (ownsEnoughOfContinent(southAmerica)) {
            return 4;
        } else if (ownsEnoughOfContinent(africa)) {
            return 5;
        } else if (ownsEnoughOfContinent(northAmerica)) {
            return 0;
        } else if (ownsEnoughOfContinent(europe)) {
            return 1;
        } else {
            return 6;
        }
    }

    public static boolean ownsEnoughOfContinent(int[] continent) {
        int count = 0;
        for (int i = 0; i < continent.length; i++) {
            if (board.getOccupier(i) == player.getId()) {
                count++;
            }
        }
        return count >= continent.length * .1;
    }
}
