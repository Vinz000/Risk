package common.validation;

import common.Constants;
import map.country.Country;
import map.model.MapModel;
import player.Player;
import player.model.PlayerModel;

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
        String invalidMessage = null;

        try {
            Integer.parseInt(input);
        } catch (NumberFormatException e){
            invalidMessage = "Not a number";
        }

        return new ValidatorResponse(invalidMessage);
    };

    private static final Function<String, ValidatorResponse> validCountryName = input -> {
        MapModel mapModel = MapModel.getInstance();
        Optional<Country> nullableCountry = mapModel.getCountryByName(input);
        String invalidMessage = null;

        if(!nullableCountry.isPresent()) {
            invalidMessage = "Not a valid country name.";
        }

        return new ValidatorResponse(invalidMessage);
    };

    public static final Function<String, ValidatorResponse> currentPlayerOccupies = input -> {
        PlayerModel playerModel = PlayerModel.getInstance();
        Player currentPlayer = playerModel.getCurrentPlayer();
        String invalidMessage = playerOccupies(input, currentPlayer);

        return new ValidatorResponse(invalidMessage);
    };

    public static final Function<String, ValidatorResponse> currentPlayerDoesNotOccupy = input -> {
        PlayerModel playerModel = PlayerModel.getInstance();
        Player currentPlayer = playerModel.getCurrentPlayer();
        String invalidMessage = playerDoesNotOccupy(input, currentPlayer);

        return new ValidatorResponse(invalidMessage);
    };

    private static final Function<String, ValidatorResponse> hasEnoughReinforcements = input -> {
        PlayerModel playerModel = PlayerModel.getInstance();
        Player currentPlayer = playerModel.getCurrentPlayer();
        String invalidMessage = null;

        if (Integer.parseInt(input) > currentPlayer.getReinforcements()) {
            invalidMessage = "Not enough reinforcements";
        }

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
                String adjacentCountryName = Constants.COUNTRY_NAMES[adjacentCountryId];
                String defendingCountryName = defendingCountry.getCountryName();
                if (adjacentCountryName.equals(defendingCountryName)) {
                    return ValidatorResponse.validNoMessage();
                }
            }

        }

        return ValidatorResponse.invalid(input + " is not adjacent to " + attackingCountry.getCountryName());
    };

    public static final Function<String, ValidatorResponse> enoughTroops = input -> {
        MapModel mapModel = MapModel.getInstance();
        Country attackingCountry = mapModel.getAttackingCountry();

        int desiredAttackingForce = Integer.parseInt(input);
        boolean hasEnoughTroops = attackingCountry.getArmyCount() > desiredAttackingForce;
        return new ValidatorResponse(hasEnoughTroops, "You do not have enough troops");
    };

    public static Function<String, ValidatorResponse> singleUnit = input -> {
        MapModel mapModel = MapModel.getInstance();
        Optional<Country> nullableAttackingCountry = mapModel.getCountryByName(input);
        if (nullableAttackingCountry.isPresent()) {
            Country attackingCountry = nullableAttackingCountry.get();
            boolean sufficientArmy = attackingCountry.getArmyCount() > 1;

            return new ValidatorResponse(sufficientArmy,
                    attackingCountry.getCountryName() + " must have at least 1 unit leftover.");
        }

        return ValidatorResponse.invalid(input + " is not a valid country name.");
    };

    public static Function<String, ValidatorResponse> appropriateForce = input -> {
        int force = Integer.parseInt(input);
        boolean appropriateForce = force >= 1;
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
        Optional<Country> nullableInputCountry = mapModel.getCountryByName(countryName);
        String invalidMessage = null;

        if(nullableInputCountry.isPresent()) {
            Country country = nullableInputCountry.get();

            if (!country.getOccupier().equals(player)) {
                invalidMessage = String.format("%s does not own %s", player.getName(), country.getCountryName());
            }
        }

        return invalidMessage;

    }

    private static String playerDoesNotOccupy(String countryName, Player player) {
        MapModel mapModel = MapModel.getInstance();
        Optional<Country> nullableInputCountry = mapModel.getCountryByName(countryName);
        String invalidMessage = null;

        if(nullableInputCountry.isPresent()) {
            Country country = nullableInputCountry.get();

            if (country.getOccupier().equals(player)) {
                invalidMessage = String.format("%s owns %s", player.getName(), country.getCountryName());
            }
        }

        return invalidMessage;
    }

    public static final Function<String, ValidatorResponse> canPlaceTroops = input -> compose(input, isInt, appropriateForce, hasEnoughReinforcements);

    public static final Function<String, ValidatorResponse> validAttackingCountry = input -> compose(input, validCountryName,  currentPlayerOccupies, singleUnit);

    public static final Function<String, ValidatorResponse> validDefendingCountry = input -> compose(input, validCountryName, currentPlayerDoesNotOccupy, adjacentCountry);

    public static final Function<String, ValidatorResponse> validAttackingTroops = input -> compose(input, isInt, appropriateForce, threeUnitCheck);

    public static final Function<String, ValidatorResponse> validDefendingTroops = input -> compose(input, isInt, appropriateForce, twoUnitCheck);

    public static final Function<String, ValidatorResponse> validReinforcement = input -> compose(input, isInt, appropriateForce, enoughTroops);

    @SafeVarargs
    private static ValidatorResponse compose(String input, Function<String, ValidatorResponse>... validators) {

        for (Function<String, ValidatorResponse> validator: validators) {
            ValidatorResponse validatorResponse = validator.apply(input);
            if (!validatorResponse.isValid()) {
                return validatorResponse;
            }
        }

        return new ValidatorResponse(null);
    }
}
