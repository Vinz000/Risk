package src;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.Optional;

public class Team7 implements Bot {

    private final BoardAPI board;
    private final PlayerAPI player;
    private final Team7HelperFunctions helperFunctions;

    Team7(BoardAPI inBoard, PlayerAPI inPlayer) {
        board = inBoard;
        player = inPlayer;

        int otherPlayerId = player.getId() == 0 ? 1 : 0;
        helperFunctions = new Team7HelperFunctions(board, player, otherPlayerId);
    }

    public String getName() {
        try {
            helperFunctions.playBattleCry();
        } catch (Exception e) {
            // Catch all exceptions here, if our bot failed
            // because of this it would be *tragic*.
            e.printStackTrace();
        }

        return "Team7";
    }

    public String getReinforcement() {
        List<Integer> almostConqueredContinentIds = helperFunctions.getAlmostConqueredContinentIds(player.getId());

        List<Integer> orderedCountries = helperFunctions.countriesInOrderOfImportance(almostConqueredContinentIds.isEmpty() ?
                almostConqueredContinentIds :
                Team7HelperFunctions.ContinentRanking.getValues());

        Predicate<Integer> currentUnitsLessThan8 = country -> board.getNumUnits(country) < 8;

        Optional<Integer> countryToReinforce = orderedCountries
                .stream()
                .filter(currentUnitsLessThan8)
                .findFirst();

        return countryToReinforce
                .map(integer -> helperFunctions.formatCommandForReinforcing(integer, 8 - board.getNumUnits(integer)))
                .orElseGet(() -> helperFunctions.formatCommandForReinforcing(orderedCountries.get(0), player.getNumUnits()));

    }

    public String getPlacement(int forPlayer) {
        List<Integer> ownedCountries = helperFunctions.getOwnedCountryIds(forPlayer);
        ownedCountries.sort(helperFunctions::compareRatings);

        return helperFunctions.formatCommandForReinforcing(ownedCountries.get(0), 1);
    }

    public String getCardExchange() {

        boolean isCavalrySizeAtLeast10 = helperFunctions.isGoldenCavalryAtleast10();
        if (!isCavalrySizeAtLeast10) helperFunctions.updateEstimatedGoldenCavalrySize();

        return player.isForcedExchange() || isCavalrySizeAtLeast10 ?
                helperFunctions.getValidCardCombination() :
                "skip";
    }

    public String getBattle() {
        String command;
        if (player.getBattleLoss() == 3) {
            command = "skip";
            return command;
        }

        List<Integer> ownedCountriesIds = helperFunctions.getOwnedCountryIds();
        List<Integer> inputCommandList = helperFunctions.checkForSingleArmyCountry(ownedCountriesIds);

        boolean noSingleArmyExists = inputCommandList.contains(-1);
        if (noSingleArmyExists) {
            inputCommandList.clear();
            inputCommandList = helperFunctions.continentSpecificAttack(ownedCountriesIds);
            boolean attackNotAllowed = inputCommandList.contains(-1);
            if (attackNotAllowed) {
                command = "skip";
                return command;
            }
        }
        command = helperFunctions.convertToCommand(inputCommandList);
        return command;
    }

    public String getDefence(int countryId) {
        String command;
        command = String.valueOf(helperFunctions.chooseDefendingArmySize(countryId));
        return command;
    }

    public String getMoveIn(int attackCountryId) {
        String command;
        int numChosen = helperFunctions.anyEnemyNeighbours(attackCountryId) ?
                3 :
                1;
        command = String.valueOf(board.getNumUnits(attackCountryId) - numChosen);
        return (command);
    }

    public String getFortify() {

        List<Integer> originCountryOptions = new ArrayList<>();
        for (int countryId = 0; countryId < GameData.NUM_COUNTRIES; countryId++) {
            boolean team7Occupies = board.getOccupier(countryId) == player.getId();
            boolean enoughUnits = board.getNumUnits(countryId) >= 2;
            boolean isConnectedToAnotherOwnedCountry = false;

            for (int connectedCountryId = 0; connectedCountryId < GameData.NUM_COUNTRIES; connectedCountryId++) {
                if (connectedCountryId == countryId) continue;

                boolean team7OccupiesConnected = board.getOccupier(connectedCountryId) == player.getId();
                boolean isConnected = board.isConnected(connectedCountryId, countryId);

                if (team7OccupiesConnected && isConnected) {
                    isConnectedToAnotherOwnedCountry = true;
                    break;
                }
            }

            if (team7Occupies && enoughUnits && isConnectedToAnotherOwnedCountry)
                originCountryOptions.add(countryId);
        }

        Predicate<Integer> surroundedByFriendlyCountries = countryId -> {
            for (int adjacentCountryId = 0; adjacentCountryId < GameData.NUM_COUNTRIES; adjacentCountryId++) {
                boolean isAdjacent = board.isAdjacent(countryId, adjacentCountryId);
                boolean isSameCountry = adjacentCountryId == countryId;
                if (!isAdjacent || isSameCountry) continue;

                boolean team7OccupiesAdjacent = board.getOccupier(adjacentCountryId) == player.getId();
                boolean neutralOccupiesAdjacent = board.getOccupier(adjacentCountryId) > 1;

                if (!team7OccupiesAdjacent && !neutralOccupiesAdjacent) {
                    return false;
                }
            }
            return true;
        };

        originCountryOptions = originCountryOptions
                .stream()
                .filter(surroundedByFriendlyCountries)
                .collect(Collectors.toList());

        Function<Integer, Integer> countAdjacentEnemyCountries = countryId -> {
            int count = 0;
            for (int adjacentCountryId = 0; adjacentCountryId < GameData.NUM_COUNTRIES; adjacentCountryId++) {
                boolean isAdjacent = board.isAdjacent(countryId, adjacentCountryId);
                boolean isEnemy = board.getOccupier(countryId) != player.getId();

                if (isAdjacent && isEnemy) count++;
            }

            return count;
        };

        List<Integer> destinationCountryOptions = new ArrayList<>();
        for (int countryId = 0; countryId < GameData.NUM_COUNTRIES; countryId++) {
            boolean team7Occupies = board.getOccupier(countryId) == player.getId();
            boolean tooManyUnits = board.getNumUnits(countryId) > 8;
            boolean isConnectedToOriginCountry = false;

            for (int originCountryOption : originCountryOptions) {
                boolean isConnected = board.isConnected(originCountryOption, countryId);
                boolean isSameCountry = originCountryOption == countryId;

                if (isConnected && !isSameCountry) {
                    isConnectedToOriginCountry = true;
                    break;
                }
            }

            if (team7Occupies && !tooManyUnits && isConnectedToOriginCountry)
                destinationCountryOptions.add(countryId);
        }

        Comparator<Integer> descendingAdjacentEnemyCount = (countryIdOne, countryIdTwo) -> {
            Integer countryOneAdjacentEnemyCount = countAdjacentEnemyCountries.apply(countryIdOne);
            Integer countryTwoAdjacentEnemyCount = countAdjacentEnemyCountries.apply(countryIdTwo);

            return countryOneAdjacentEnemyCount.compareTo(countryTwoAdjacentEnemyCount);
        };
        destinationCountryOptions.sort(descendingAdjacentEnemyCount);

        if (destinationCountryOptions.size() == 0) {
            return "skip";
        }

        int destinationCountry = destinationCountryOptions.get(0);

        Predicate<Integer> connectedToDestination = countryId -> board.isConnected(destinationCountry, countryId);
        originCountryOptions = originCountryOptions
                .stream()
                .filter(connectedToDestination)
                .collect(Collectors.toList());

        Comparator<Integer> descendingUnitCount = (countryIdOne, countryIdTwo) -> {
            Integer countryOneNumUnits = board.getNumUnits(countryIdOne);
            Integer countryTwoNumUnits = board.getNumUnits(countryIdTwo);

            return countryOneNumUnits.compareTo(countryTwoNumUnits);
        };
        originCountryOptions.sort(descendingUnitCount);

        if (originCountryOptions.size() == 0) {
            return "skip";
        }

        int originCountry = originCountryOptions.get(0);

        int availableUnits = board.getNumUnits(originCountry) - 1;
        int destinationUnitsCount = board.getNumUnits(destinationCountry);
        boolean unitsWouldGoOverEight = availableUnits + destinationUnitsCount > 8;
        int unitsToMove = unitsWouldGoOverEight ?
                8 - destinationUnitsCount :
                availableUnits;

        return String.format("%s %s %d", originCountry, destinationCountry, unitsToMove);
    }

}
