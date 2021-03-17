package common.validation;

import common.Constants;
import map.country.Country;
import map.model.MapModel;
import player.Player;
import player.model.PlayerModel;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * TODO:
 * - Change so that input must pass all validators
 * - Make a validator to check input length for playerName (15 characters max)
 */

public class Validators {
    public static final Function<String, ValidatorResponse> alwaysValid = input -> ValidatorResponse.validNoMessage();

    public static final Function<String, ValidatorResponse> nonEmpty = input -> {
        boolean isValid = !input.isEmpty();
        return new ValidatorResponse(isValid, "cannot be empty");
    };

    public static final Function<String, ValidatorResponse> yesNo = input -> {
        boolean isValid = input.equalsIgnoreCase("yes") ||
                input.toLowerCase().equals("y") ||
                input.equalsIgnoreCase("no") ||
                input.toLowerCase().equals("n");

        return new ValidatorResponse(isValid, "must be 'yes' or 'no'");
    };

    public static final Function<String, ValidatorResponse> isInt = input -> {
        try {
            Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return new ValidatorResponse(false, "Must be an integer");
        }

        return ValidatorResponse.validNoMessage();
    };

    public static final Function<String, ValidatorResponse> currentPlayerOccupies = input -> {
        PlayerModel playerModel = PlayerModel.getInstance();
        Player currentPlayer = playerModel.getCurrentPlayer();
        String invalidMessage = playerOccupies(input, currentPlayer);

        return new ValidatorResponse(invalidMessage);
    };

    @SafeVarargs
    public static Function<String, ValidatorResponse> compose(Function<String, ValidatorResponse>... validators) {
        return Arrays.stream(validators).reduce(Validators.alwaysValid, (acc, cur) -> (input) -> {
            ValidatorResponse accResponse = acc.apply(input);
            ValidatorResponse curResponse = cur.apply(input);

            boolean isValid = accResponse.isValid() && curResponse.isValid();
            String message = accResponse.getMessage() == null ?
                    curResponse.getMessage() :
                    accResponse.getMessage();

            return new ValidatorResponse(isValid, message);
        });
    }

    public static final Function<String, ValidatorResponse> currentPlayerDoesNotOccupy = input -> {
        PlayerModel playerModel = PlayerModel.getInstance();
        Player currentPlayer = playerModel.getCurrentPlayer();
        String invalidMessage = playerDoesNotOccupy(input, currentPlayer);

        return new ValidatorResponse(invalidMessage);
    };

    public static final Function<String, ValidatorResponse> skipOrFight = input -> {
        boolean isValid = input.equalsIgnoreCase("skip") ||
                input.toLowerCase().contains("s") ||
                input.equalsIgnoreCase("fight") ||
                input.toLowerCase().contains("f");
        return new ValidatorResponse(isValid, "Input must be 'fight' or 'skip'");
    };

    public static final Function<String, ValidatorResponse> adjacentCountry = input -> {
        MapModel mapModel = MapModel.getInstance();

        Country attackingCountry = mapModel.getAttackingCountry();
        int[] adjacentCountries = attackingCountry.getAdjCountries();

        Optional<Country> nullableDefendingCountry = mapModel.getCountryByName(input);

        if (nullableDefendingCountry.isPresent()) {

            Country defendingCountry = nullableDefendingCountry.get();

            for (int adjacentCountryId : adjacentCountries) {
                if(Constants.COUNTRY_NAMES[adjacentCountryId].equals(defendingCountry.getCountryName())){
                    ValidatorResponse.invalid(defendingCountry.getCountryName() + " is not adjacent");
                }
            }

        }

        return ValidatorResponse.validNoMessage();
    };

    public static final Function<String, ValidatorResponse> hasArmy = input -> {
        MapModel mapModel = MapModel.getInstance();
        Country attackingCountry = mapModel.getAttackingCountry();

        int desiredAttackingForce = Integer.parseInt(input);
        boolean hasEnoughTroops = attackingCountry.getArmyCount() > desiredAttackingForce;
        return new ValidatorResponse(hasEnoughTroops, "You do not have enough troops");
    };

    public static Function<String, ValidatorResponse> singleUnit = input -> {
        MapModel mapModel = MapModel.getInstance();
        Optional<Country> attackingCountry = mapModel.getCountryByName(input);
        boolean sufficientArmy = attackingCountry.get().getArmyCount() > 1;
        return new ValidatorResponse(sufficientArmy, "You must have 1 unit leftover.");
    };

    public static Function<String, ValidatorResponse> appropriateForce = input -> {
        MapModel mapModel = MapModel.getInstance();
        Country attackingCountry = mapModel.getAttackingCountry();
        int army = attackingCountry.getArmyCount();
        int force = Integer.parseInt(input);
        boolean appropriateForce;

        appropriateForce = (force >= 1) && (army >= 2);
        return new ValidatorResponse(appropriateForce, "You did not select an appropriately sized force.");
    };

    public static Function<String, ValidatorResponse> threeUnitCheck = input -> {
        boolean lessThanFour = Integer.parseInt(input) < 4;
        return new ValidatorResponse(lessThanFour, "You cannot attack with more than 3 units per battle.");
    };

    public static Function<String, ValidatorResponse> twoUnitCheck = input -> {
        boolean lessThanThree = Integer.parseInt(input) < 3;
        return new ValidatorResponse(lessThanThree, "You cannot defend with more than 2 units per battle.");

    };

    /**
     * Determines if the given player occupies the country
     * with the given name.
     *
     * @param countryName name of country in question
     * @param player      name of player in question
     * @return null if player occupies country else invalid message.
     */
    private static String playerOccupies(String countryName, Player player) {
        MapModel mapModel = MapModel.getInstance();
        Optional<Country> inputCountry = mapModel.getCountryByName(countryName);
        Country country = inputCountry.get();
        String invalidMessage = null;

        if (!occupationCheck(country, player)) {
            invalidMessage = String.format("%s owns %s", player.getName(), country.getCountryName());
        }

        if (!validCountry(countryName)) {
            invalidMessage = String.format("%s is not a valid country name", countryName);
        }

        return invalidMessage;
    }

    private static String playerDoesNotOccupy(String countryName, Player player) {
        MapModel mapModel = MapModel.getInstance();
        Optional<Country> inputCountry = mapModel.getCountryByName(countryName);
        Country country = inputCountry.get();
        String invalidMessage = null;

        if (occupationCheck(country, player)) {
            invalidMessage = String.format("%s owns %s", player.getName(), country.getCountryName());
        }

        if (!validCountry(countryName)) {
            invalidMessage = String.format("%s is not a valid country name", countryName);
        }

        return invalidMessage;
    }

    private static boolean occupationCheck(Country country, Player player) {
        boolean playerOccupies;
        Player countryOccupier = country.getOccupier();
        playerOccupies = player.equals(countryOccupier);
        return playerOccupies;
    }

    private static boolean validCountry(String countryName) {
        return Arrays.asList(Constants.COUNTRY_NAMES).contains(countryName);
    }
}
