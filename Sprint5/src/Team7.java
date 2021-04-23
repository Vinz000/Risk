package src;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Team7 implements Bot {

    private final BoardAPI board;
    private final PlayerAPI player;
    private final Team7HelperFunctions helperFunctions;
    private final int otherPlayerId;

    Team7(BoardAPI inBoard, PlayerAPI inPlayer) {
        board = inBoard;
        player = inPlayer;

        otherPlayerId = player.getId() == 0 ? 1 : 0;
        helperFunctions = new Team7HelperFunctions(board, player, otherPlayerId);
    }

    public String getName() {
        Thread battleCryThread = new Thread(helperFunctions::playBattleCry);
        battleCryThread.start();

        return "Team7";
    }

    public String getReinforcement() {
        helperFunctions.sleep();
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
        helperFunctions.sleep();
        List<Integer> ownedCountries = helperFunctions.getOwnedCountryIds(forPlayer);
        ownedCountries.sort(helperFunctions::compareRatings);

        return helperFunctions.formatCommandForReinforcing(ownedCountries.get(0), 1);
    }

    public String getCardExchange() {
        helperFunctions.sleep();
        boolean isCavalrySizeAtLeast10 = helperFunctions.isGoldenCavalryAtleast10();
        if (!isCavalrySizeAtLeast10) helperFunctions.updateEstimatedGoldenCavalrySize();

        return player.isForcedExchange() || isCavalrySizeAtLeast10 ?
                helperFunctions.getValidCardCombination() :
                "skip";
    }

    public String getBattle() {
        helperFunctions.sleep();
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
        helperFunctions.sleep();
        String command;
        command = String.valueOf(helperFunctions.chooseDefendingArmySize(countryId));
        return command;
    }

    public String getMoveIn(int attackCountryId) {
        helperFunctions.sleep();
        String command;
        int numChosen = helperFunctions.anyEnemyNeighbours(attackCountryId) ?
                3 :
                1;
        command = String.valueOf(board.getNumUnits(attackCountryId) - numChosen);
        return (command);
    }

    public String getFortify() {
        helperFunctions.sleep();
        List<Integer> originCountryOptions = helperFunctions.getInitialOriginCountryOptions();
        List<Integer> destinationCountryOptions = helperFunctions.getDestinationCountryOptions(originCountryOptions);

        Comparator<Integer> descendingAdjacentEnemyCount = (countryIdOne, countryIdTwo) -> {
            Integer countryOneAdjacentEnemyCount = helperFunctions.numSurroundingCountriesByPlayer(countryIdOne, otherPlayerId);
            Integer countryTwoAdjacentEnemyCount = helperFunctions.numSurroundingCountriesByPlayer(countryIdTwo, otherPlayerId);

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
