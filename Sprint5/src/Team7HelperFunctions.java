package src;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;

import static src.GameData.CONTINENT_COUNTRIES;

public class Team7HelperFunctions {

    private static BoardAPI board;
    private static PlayerAPI player;
    private static int otherPlayerId;

    Team7HelperFunctions(BoardAPI inBoard, PlayerAPI inPlayer) {
        board = inBoard;
        player = inPlayer;
        otherPlayerId = inPlayer.getId() == 0 ? 1 : 0;
    }


    public static String convertToCommand(List<Integer> inputList) {
        StringBuilder command = new StringBuilder();
        for (int i = 0; i < 2; i++) {
            command.append(GameData.COUNTRY_NAMES[inputList.get(i)]);
            command.append(" ");
        }
        command.append(inputList.get(2));
        return String.valueOf(command);
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

        for (int i = 0; i < GameData.NUM_COUNTRIES; i++) {
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
            if (board.getNumUnits(adjacentCountryId) == 1 && board.getOccupier(adjacentCountryId) != player.getId() &&
                    board.getOccupier(adjacentCountryId) == getOtherPlayerId()) {
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

    public static int lowestArmyCount(int[] countryIds) {
        int lowestCountryId = 0;
        for (int countryId : countryIds) {
            int numUnits = board.getNumUnits(countryId);
            int lowestNumUnits = board.getNumUnits(lowestCountryId);
            if (lowestNumUnits > numUnits) {
                lowestCountryId = countryId;
            }
        }
        return lowestCountryId;
    }

    public static int leastEnemyAndNeutralAdjacentCountries(List<Integer> ownedCountriesIds) {
        int countryId = -1;
        int leastEnemyAdjacentCount = Integer.MAX_VALUE;

        for (Integer ownedCountriesId : ownedCountriesIds) {
            int enemyCount = totalEnemyCountryCount(GameData.ADJACENT[ownedCountriesId]);
            if (enemyCount < leastEnemyAdjacentCount) {
                countryId = ownedCountriesId;
                leastEnemyAdjacentCount = enemyCount;
            }
        }
        return countryId;
    }

    public static int leastEnemyAdjacentCountries(List<Integer> ownedCountriesIds) {
        int countryId = 0;
        int leastEnemyAdjacentCount = Integer.MAX_VALUE;

        for (Integer ownedCountriesId : ownedCountriesIds) {
            int enemyCount;
            if (enemyCountryPresent(GameData.ADJACENT[ownedCountriesId])) {
                enemyCount = enemyPlayerCount(GameData.ADJACENT[ownedCountriesId]);
                if (enemyCount < leastEnemyAdjacentCount) {
                    countryId = ownedCountriesId;
                    leastEnemyAdjacentCount = enemyCount;
                }
            }
        }
        return countryId;
    }

    public static boolean enemyCountryPresent(int[] ownedCountriesIds) {
        int otherPlayerId = getOtherPlayerId();
        int enemyCount = 0;
        for (Integer ownedCountriesId : ownedCountriesIds) {
            if (board.getOccupier(ownedCountriesId) == otherPlayerId) {
                enemyCount = enemyPlayerCount(GameData.ADJACENT[ownedCountriesId]);
            }
        }
        return enemyCount > 0;
    }

    public static int leastEnemyAdjacentCountries(int[] continent) {
        List<Integer> ownedCountriesIds = Arrays.stream(continent).boxed().collect(Collectors.toList());
        return leastEnemyAdjacentCountries(ownedCountriesIds);
    }

    public static int leastEnemyAndNeutralAdjacentCountries(int[] continent) {
        List<Integer> ownedCountriesId = Arrays.stream(continent).boxed().collect(Collectors.toList());
        return leastEnemyAndNeutralAdjacentCountries(ownedCountriesId);
    }

    public static int totalEnemyCountryCount(int[] countryIds) {
        IntPredicate isEnemyCountryId = countryId -> board.getOccupier(countryId) != player.getId();
        return (int) Arrays.stream(countryIds).filter(isEnemyCountryId).count();
    }

    public static int enemyPlayerCount(int[] countryIds) {
        IntPredicate isEnemyCountryId = countryId -> board.getOccupier(countryId) != player.getId() &&
                board.getOccupier(countryId) == getOtherPlayerId();
        return (int) Arrays.stream(countryIds).filter(isEnemyCountryId).count();
    }

    public static List<Integer> continentSpecificAttack(List<Integer> ownedCountriesIds) {
        int chosenContinent = chooseContinentToAttack();
        int chosenAttackingCountry;
        int chosenDefendingCountry;
        List<Integer> combatInput = new ArrayList<>();

        if (chosenContinent == -1) {
            chosenAttackingCountry = leastEnemyAdjacentCountries(ownedCountriesIds);
        } else {
            int[] continent = CONTINENT_COUNTRIES[chosenContinent];
            chosenAttackingCountry = leastEnemyAdjacentCountries(continent);
            if (chosenAttackingCountry == -1) {
                chosenAttackingCountry = leastEnemyAndNeutralAdjacentCountries(continent);
            }
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
        for (ContinentRanking continent : ContinentRanking.values()) {
            if (ownsEnoughOfContinent(CONTINENT_COUNTRIES[continent.value])) {
                return continent.value;
            }
        }
        return -1;
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

    public static int getOtherPlayerId() {
        return otherPlayerId;
    }

    private enum ContinentRanking {
        AUSTRALIA(3),
        ASIA(2),
        SOUTH_AMERICA(4),
        AFRICA(5),
        NORTH_AMERICA(0),
        EUROPE(1);

        public int value;

        ContinentRanking(int value) {
            this.value = value;
        }
    }
}
