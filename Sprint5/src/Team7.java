package src;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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

        for (int i = 0; i < 10; i++) {
            helperFunctions.sleep();
        }

        return "Team7";
    }

    public String getReinforcement() {
        helperFunctions.sleep();

        List<Integer> ownedCountries = helperFunctions.getOwnedCountryIds(player.getId());
        ownedCountries.sort(helperFunctions::compareRatingsForOwnReinforcement);

        int countryToReinforce = ownedCountries.get(0);

        int unitsCurrentlyInCountry = board.getNumUnits(countryToReinforce);
        int numUnitsToReinforce = unitsCurrentlyInCountry < 8 ?
                Math.min(8 - unitsCurrentlyInCountry, player.getNumUnits()) :
                player.getNumUnits();

        return helperFunctions.formatCommandForReinforcing(countryToReinforce, numUnitsToReinforce);
    }

    public String getPlacement(int forPlayer) {
        helperFunctions.sleep();
        List<Integer> ownedCountries = helperFunctions.getOwnedCountryIds(forPlayer);
        ownedCountries.sort(helperFunctions::compareRatingsForNeutrals);

        return helperFunctions.formatCommandForReinforcing(ownedCountries.get(0), 1);
    }

    public String getCardExchange() {
        return helperFunctions.getValidCardCombination();
    }

    public String getBattle() {
        helperFunctions.sleep();
        List<Integer> inputCommand = new ArrayList<>();
        List<Integer> countriesThatCanAttack = helperFunctions.countriesThatCanAttack();
        if (countriesThatCanAttack.isEmpty()) {
            return "skip";
        }

        countriesThatCanAttack.sort(helperFunctions::compareAttackingCountries);

        inputCommand.add(countriesThatCanAttack.get(0));
        List<Integer> countriesToInvade = helperFunctions.countriesToInvade(countriesThatCanAttack.get(0));
        countriesToInvade.sort(helperFunctions::compareDefendingCountries);
        inputCommand.add(countriesToInvade.get(0));
        inputCommand.add(helperFunctions.chooseAttackingArmySize(inputCommand.get(0)));


        return helperFunctions.convertToCommand(inputCommand);
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

        List<Integer> input = new ArrayList<>();
        input.add(originCountry);
        input.add(destinationCountry);
        input.add(unitsToMove);

        return helperFunctions.convertToCommand(input);
    }

}
