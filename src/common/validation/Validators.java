package common.validation;

import common.Constants;
import map.country.Country;
import map.model.MapModel;
import player.Player;
import player.model.PlayerModel;

import javax.xml.validation.Validator;
import java.util.Optional;
import java.util.function.Function;

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
            return ValidatorResponse.validNoMessage();
        } catch (NumberFormatException e) {
            return ValidatorResponse.invalid("Must be an integer");
        }
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

    public static final Function<String, ValidatorResponse> hasAdjacentOpposingCountry = input -> {
        MapModel mapModel = MapModel.getInstance();
        PlayerModel playerModel = PlayerModel.getInstance();

        Player currentPlayer = playerModel.getCurrentPlayer();
        Optional<Country> nullableCountry = mapModel.getCountryByName(input);

        if (nullableCountry.isPresent()) {
            Country country = nullableCountry.get();
            int[] adjacentCountriesIds = country.getAdjCountries();

            for (int adjacentCountryId : adjacentCountriesIds) {
                Optional<Country> nullableAdjacentCountry = mapModel.getCountryById(adjacentCountryId);

                if(nullableAdjacentCountry.isPresent()) {
                    Country adjacentCountry = nullableAdjacentCountry.get();

                    if (!adjacentCountry.getOccupier().equals(currentPlayer)) {
                        return ValidatorResponse.validNoMessage();
                    }
                }
            }
        }

        return ValidatorResponse.invalid("Choose country that is not surrounded by only your country.");
    };

    public static final Function<String, ValidatorResponse> enoughTroops = input -> {
        MapModel mapModel = MapModel.getInstance();
        Country attackingCountry = mapModel.getAttackingCountry();

        int desiredAttackingForce = Integer.parseInt(input);
        boolean hasEnoughTroops = attackingCountry.getArmyCount() > desiredAttackingForce;
        return new ValidatorResponse(hasEnoughTroops, "You do not have enough troops");
    };

    private static final Function<String, ValidatorResponse> validCountryName = input -> {
        MapModel mapModel = MapModel.getInstance();
        Optional<Country> nullableCountry = mapModel.getCountryByName(input);

        if (nullableCountry.isPresent()) {
            return ValidatorResponse.validNoMessage();
        }

        return ValidatorResponse.invalid("Not a valid country name");
    };

    private static final Function<String, ValidatorResponse> hasEnoughReinforcements = input -> {
        PlayerModel playerModel = PlayerModel.getInstance();
        Player currentPlayer = playerModel.getCurrentPlayer();
        int desiredReinforcements = Integer.parseInt(input);

        if (desiredReinforcements > currentPlayer.getReinforcements()) {
            return ValidatorResponse.invalid("Not enough reinforcements");
        }

        return ValidatorResponse.validNoMessage();
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

    public static Function<String, ValidatorResponse> hasAtLeastOneTroopLeft = input -> {
        MapModel mapModel = MapModel.getInstance();
        Country attackingCountry = mapModel.getAttackingCountry();
        int troops = Integer.parseInt(input);

        boolean hasAtLeastOneTroopLeft = false;

        if (attackingCountry.getArmyCount() - troops >= 1) {
            hasAtLeastOneTroopLeft = true;
        }

        return new ValidatorResponse(hasAtLeastOneTroopLeft, "You need to keep at least one troop back.");
    };

    public static Function<String, ValidatorResponse> defenderCanDefend = input -> {
        MapModel mapModel = MapModel.getInstance();
        int defendingForce = Integer.parseInt(input);
        int defendingCountryArmy = mapModel.getDefendingCountry().getArmyCount();

        boolean validDefendingForce = defendingForce <= defendingCountryArmy;

        return new ValidatorResponse(validDefendingForce, String.format("You don't have %d army, you only have %d.", defendingForce, defendingCountryArmy));

    };

    public static final Function<String, ValidatorResponse> validReinforcingCountry = input -> compose(input, nonEmpty, validCountryName, currentPlayerOccupies);
    public static final Function<String, ValidatorResponse> validAttackingCountry = input -> compose(input, nonEmpty, validCountryName, currentPlayerOccupies, hasAdjacentOpposingCountry, singleUnit);
    public static final Function<String, ValidatorResponse> canPlaceTroops = input -> compose(input, nonEmpty, isInt, appropriateForce, hasEnoughReinforcements);
    public static final Function<String, ValidatorResponse> validReinforcement = input -> compose(input, nonEmpty, isInt, appropriateForce, enoughTroops);
    public static final Function<String, ValidatorResponse> validAttackingTroops = input -> compose(input, nonEmpty, isInt, appropriateForce, enoughTroops, hasAtLeastOneTroopLeft, threeUnitCheck);
    public static final Function<String, ValidatorResponse> validDefendingTroops = input -> compose(input, nonEmpty, isInt, appropriateForce, defenderCanDefend,twoUnitCheck);
    public static final Function<String, ValidatorResponse> validDefendingCountry = input -> compose(input, nonEmpty, validCountryName, currentPlayerDoesNotOccupy, adjacentCountry);

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

        if (nullableInputCountry.isPresent()) {
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

        if (nullableInputCountry.isPresent()) {
            Country country = nullableInputCountry.get();

            if (country.getOccupier().equals(player)) {
                invalidMessage = String.format("%s owns %s", player.getName(), country.getCountryName());
            }
        }

        return invalidMessage;
    }

    public static final Function<String, ValidatorResponse> fortificationOriginCountry = rawInput -> compose(rawInput, nonEmpty, currentPlayerOccupies, input -> {
        MapModel mapModel = MapModel.getInstance();
        PlayerModel playerModel = PlayerModel.getInstance();
        Player currentPlayer = playerModel.getCurrentPlayer();
        Optional<Country> nullableOriginCountry = mapModel.getCountryByName(input);

        if (nullableOriginCountry.isPresent()) {
            Country originCountry = nullableOriginCountry.get();

            boolean ownsMoreThanZeroAdjacentCountries = false;
            for (int adjacentCountryIndex : originCountry.getAdjCountries()) {
                String adjacentCountryName = Constants.COUNTRY_NAMES[adjacentCountryIndex];
                Optional<Country> nullableAdjacentCountry = mapModel.getCountryByName(adjacentCountryName);

                if (nullableAdjacentCountry.isPresent()) {
                    Country adjacentCountry = nullableAdjacentCountry.get();

                    if (currentPlayer.equals(adjacentCountry.getOccupier())) {
                        ownsMoreThanZeroAdjacentCountries = true;
                        break;
                    }

                }
            }

            boolean hasMoreThanOneArmy = originCountry.getArmyCount() > 1;

            if (!ownsMoreThanZeroAdjacentCountries) {
                return ValidatorResponse.invalid("You do not own an adjacent countries.");
            } else if (!hasMoreThanOneArmy) {
                return ValidatorResponse.invalid("Not enough armies in " + originCountry.getCountryName());
            }
        }

        return ValidatorResponse.validNoMessage();
    });

    private static boolean isConnected(Country previous, Country from, Country to, int limit) {
        if (limit < 0) return false;

        MapModel mapModel = MapModel.getInstance();
        PlayerModel playerModel = PlayerModel.getInstance();
        Player currentPlayer = playerModel.getCurrentPlayer();
        int[] adjacentCountries = from.getAdjCountries();

        for (int adjacentCountryIndex : adjacentCountries) {
            String adjacentCountryName = Constants.COUNTRY_NAMES[adjacentCountryIndex];
            Optional<Country> nullableAdjacentCountry = mapModel.getCountryByName(adjacentCountryName);

            if (nullableAdjacentCountry.isPresent()) {
                Country adjacentCountry = nullableAdjacentCountry.get();

                if (adjacentCountry.getOccupier().equals(currentPlayer) && !previous.equals(adjacentCountry)) {
                    if (adjacentCountry.equals(to)) return true;
                    return isConnected(from, adjacentCountry, to, --limit);
                }
            } else {
                // Should never be reached (adjacent country always present)
                return false;
            }
        }
        return false;
    }

    public static final Function<Country, Function<String, ValidatorResponse>> fortificationDestinationCountry = originCountry -> rawInput -> compose(rawInput, nonEmpty, currentPlayerOccupies, input -> {
        MapModel mapModel = MapModel.getInstance();
        Optional<Country> nullableDestinationCountry = mapModel.getCountryByName(input);

        if (nullableDestinationCountry.isPresent()) {
            Country destinationCountry = nullableDestinationCountry.get();

            boolean connected = isConnected(originCountry, originCountry, destinationCountry, 10);

            if (!connected) return ValidatorResponse.invalid(originCountry.getCountryName() + " is not connected to " + destinationCountry.getCountryName());
            return ValidatorResponse.validNoMessage();
        }
        return ValidatorResponse.validNoMessage();
    });

    public static final Function<Country, Function<String, ValidatorResponse>> validFortification = originCountry -> rawInput -> compose(rawInput, nonEmpty, isInt, appropriateForce, input -> {
        int desiredNumberOfTroops = Integer.parseInt(input);

        return new ValidatorResponse(desiredNumberOfTroops < originCountry.getArmyCount(), "Not enough troops in " + originCountry.getCountryName());
    });

    @SafeVarargs
    private static ValidatorResponse compose(String input, Function<String, ValidatorResponse>... validators) {

        for (Function<String, ValidatorResponse> validator : validators) {
            ValidatorResponse validatorResponse = validator.apply(input);
            if (!validatorResponse.isValid()) {
                return validatorResponse;
            }
        }

        return new ValidatorResponse(null);
    }
}
