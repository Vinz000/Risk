package src;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class Team7 implements Bot {

    private final BoardAPI board;
    private final PlayerAPI player;
    private final int otherPlayerId;
    private final Team7HelperFunctions helperFunctions;

    Team7(BoardAPI inBoard, PlayerAPI inPlayer) {
        board = inBoard;
        player = inPlayer;

        otherPlayerId = player.getId() == 0 ? 1 : 0;

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
        List<Integer> otherPlayerAlmostCompletedContinents = helperFunctions.getAlmostConqueredContinentIds(otherPlayerId);

        int countryRating = Integer.MIN_VALUE;
        int countryIdThatNeedsToBeReinforcedASAP = ownedCountries.get(0);

        for (Integer country : ownedCountries) {
            int rating = 0;

            rating -= 75 * helperFunctions.numSurroundingCountriesByPlayer(country, player.getId());

            Predicate<Integer> continentContainsCountry = continent -> helperFunctions.continentContainsCountry(continent, country);

            int opponentRating = otherPlayerAlmostCompletedContinents
                    .stream()
                    .anyMatch(continentContainsCountry) ? 1000 : 50;

            rating += opponentRating * helperFunctions.numSurroundingCountriesByPlayer(country, otherPlayerId);

            if (rating > countryRating) {
                countryRating = rating;
                countryIdThatNeedsToBeReinforcedASAP = country;
            }
        }

        return helperFunctions.formatCommandForReinforcing(countryIdThatNeedsToBeReinforcedASAP, 1);
    }

    public String getCardExchange() {
        return player.isForcedExchange() || helperFunctions.isGoldenCavalryAtleast10() ?
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
        String command = "";
        // put code here
        command = "skip";
        return (command);
    }
}
