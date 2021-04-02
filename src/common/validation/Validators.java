package common.validation;

import common.Constants;
import deck.Card;
import deck.CardSet;
import deck.CardType;
import map.country.Country;
import map.model.MapModel;
import player.Player;
import player.model.PlayerModel;
import shell.model.ShellModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class Validators {
    public static final Validator alwaysValid = input -> ValidatorResponse.validNoMessage();

    public static final Validator nonEmpty = input -> {
        boolean isValid = !input.isEmpty();
        return new ValidatorResponse(isValid, "cannot be empty");
    };

    public static final Validator is15CharactersMax = input -> {
        boolean isValid = input.length() < 15;
        return new ValidatorResponse(isValid, "Cannot be more than 15 characters long");
    };

    public static final Validator yesNo = input -> {
        boolean isValid = input.equalsIgnoreCase("yes") ||
                input.toLowerCase().equals("y") ||
                input.equalsIgnoreCase("no") ||
                input.toLowerCase().equals("n");

        return new ValidatorResponse(isValid, "must be 'yes' or 'no'");
    };

    public static final Validator isInt = input -> {
        try {
            Integer.parseInt(input);
            return ValidatorResponse.validNoMessage();
        } catch (NumberFormatException e) {
            return ValidatorResponse.invalid("Must be an integer");
        }
    };

    public static final Validator currentPlayerOccupies = input -> {
        PlayerModel playerModel = PlayerModel.getInstance();
        Player currentPlayer = playerModel.getCurrentPlayer();
        String invalidMessage = playerOccupies(input, currentPlayer);

        return new ValidatorResponse(invalidMessage);
    };

    public static final Validator currentPlayerDoesNotOccupy = input -> {
        PlayerModel playerModel = PlayerModel.getInstance();
        Player currentPlayer = playerModel.getCurrentPlayer();
        String invalidMessage = playerDoesNotOccupy(input, currentPlayer);

        return new ValidatorResponse(invalidMessage);
    };

    public static final Validator skipOrFight = input -> {
        boolean isValid = input.equalsIgnoreCase("skip") ||
                input.toLowerCase().contains("s") ||
                input.equalsIgnoreCase("fight") ||
                input.toLowerCase().contains("f");
        return new ValidatorResponse(isValid, "Input must be 'fight' or 'skip'");
    };

    public static final Validator adjacentCountry = input -> {
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

    public static final Validator hasAdjacentOpposingCountry = input -> {
        MapModel mapModel = MapModel.getInstance();
        PlayerModel playerModel = PlayerModel.getInstance();

        Player currentPlayer = playerModel.getCurrentPlayer();
        Optional<Country> nullableCountry = mapModel.getCountryByName(input);

        if (nullableCountry.isPresent()) {
            Country country = nullableCountry.get();
            int[] adjacentCountriesIds = country.getAdjCountries();

            for (int adjacentCountryId : adjacentCountriesIds) {
                Optional<Country> nullableAdjacentCountry = mapModel.getCountryById(adjacentCountryId);

                if (nullableAdjacentCountry.isPresent()) {
                    Country adjacentCountry = nullableAdjacentCountry.get();

                    if (!adjacentCountry.getOccupier().equals(currentPlayer)) {
                        return ValidatorResponse.validNoMessage();
                    }
                }
            }
        }

        return ValidatorResponse.invalid("Choose country that is not surrounded by only your country.");
    };

    public static final Validator enoughTroops = input -> {
        MapModel mapModel = MapModel.getInstance();
        Country attackingCountry = mapModel.getAttackingCountry();

        int desiredAttackingForce = Integer.parseInt(input);
        boolean hasEnoughTroops = attackingCountry.getArmyCount() > desiredAttackingForce;
        return new ValidatorResponse(hasEnoughTroops, "You do not have enough troops");
    };

    private static final Validator validCountryName = input -> {
        MapModel mapModel = MapModel.getInstance();
        Optional<Country> nullableCountry = mapModel.getCountryByName(input);

        if (nullableCountry.isPresent()) {
            return ValidatorResponse.validNoMessage();
        }

        return ValidatorResponse.invalid("Not a valid country name");
    };

    private static final Validator hasEnoughReinforcements = input -> {
        PlayerModel playerModel = PlayerModel.getInstance();
        Player currentPlayer = playerModel.getCurrentPlayer();
        int desiredReinforcements = Integer.parseInt(input);

        if (desiredReinforcements > currentPlayer.getReinforcements()) {
            return ValidatorResponse.invalid("Not enough reinforcements");
        }

        return ValidatorResponse.validNoMessage();
    };

    public static Validator singleUnit = input -> {
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

    public static Validator appropriateForce = input -> {
        int force = Integer.parseInt(input);
        boolean appropriateForce = force >= 1;
        return new ValidatorResponse(appropriateForce, "You did not select an appropriately sized force.");
    };

    public static Validator threeUnitCheck = input -> {
        boolean lessThanFour = Integer.parseInt(input) < 4;
        return new ValidatorResponse(lessThanFour, "You cannot attack with more than 3 units per battle.");
    };

    public static Validator twoUnitCheck = input -> {
        boolean lessThanThree = Integer.parseInt(input) < 3;
        return new ValidatorResponse(lessThanThree, "You cannot defend with more than 2 units per battle.");
    };

    public static Validator hasAtLeastOneTroopLeft = input -> {
        MapModel mapModel = MapModel.getInstance();
        Country attackingCountry = mapModel.getAttackingCountry();
        int troops = Integer.parseInt(input);

        boolean hasAtLeastOneTroopLeft = false;

        if (attackingCountry.getArmyCount() - troops >= 1) {
            hasAtLeastOneTroopLeft = true;
        }

        return new ValidatorResponse(hasAtLeastOneTroopLeft, "You need to keep at least one troop back.");
    };

    public static Validator defenderCanDefend = input -> {
        MapModel mapModel = MapModel.getInstance();
        int defendingForce = Integer.parseInt(input);
        int defendingCountryArmy = mapModel.getDefendingCountry().getArmyCount();

        boolean validDefendingForce = defendingForce <= defendingCountryArmy;

        return new ValidatorResponse(validDefendingForce, String.format("You don't have %d army, you only have %d.", defendingForce, defendingCountryArmy));
    };

    public static Validator canSpendCards = input -> {
        PlayerModel playerModel = PlayerModel.getInstance();
        Player currentPlayer = playerModel.getCurrentPlayer();

        boolean canSpend;

        if (input.toLowerCase().contains("y")) {
            canSpend = validCombinations(currentPlayer.getCards());
            return new ValidatorResponse(canSpend, "You do not have the correct selection of cards.");
        } else if (input.toLowerCase().contains("n")) {
            return ValidatorResponse.validNoMessage();
        } else {
            return ValidatorResponse.invalid("Please enter yes or no.");
        }

    };

    public static Validator cardChoiceCheck = input -> {
        PlayerModel playerModel = PlayerModel.getInstance();
        Player currentPlayer = playerModel.getCurrentPlayer();

        List<Card> cardsOwned = currentPlayer.getCards();

        Optional<CardSet> nullableCardSet = CardSet.fromString(input);
        if (nullableCardSet.isPresent()) {
            CardSet cardSet = nullableCardSet.get();
            return new ValidatorResponse(cardSet.logic.test(cardsOwned), "Please choose a valid option.");
        }

        return ValidatorResponse.invalid("Invalid");
    };

    private static boolean validCombinations(List<Card> cards) {
        return CardSet.atLeastOneOfEach().test(cards) ||
                CardSet.atLeastThreeOfType(CardType.ARTILLERY).test(cards) ||
                CardSet.atLeastThreeOfType(CardType.SOLDIER).test(cards) ||
                CardSet.atLeastThreeOfType(CardType.CALVARY).test(cards) ||
                CardSet.oneWildCard().test(cards) ||
                CardSet.twoWildCards().test(cards);
    }

    public static final Validator validPlayerName = input -> compose(input, nonEmpty, is15CharactersMax);
    public static final Validator validReinforcingCountry = input -> compose(input, nonEmpty, validCountryName, currentPlayerOccupies);
    public static final Validator validAttackingCountry = input -> compose(input, nonEmpty, validCountryName, currentPlayerOccupies, hasAdjacentOpposingCountry, singleUnit);
    public static final Validator canPlaceTroops = input -> compose(input, nonEmpty, isInt, appropriateForce, hasEnoughReinforcements);
    public static final Validator validReinforcement = input -> compose(input, nonEmpty, isInt, appropriateForce, enoughTroops);
    public static final Validator validAttackingTroops = input -> compose(input, nonEmpty, isInt, appropriateForce, enoughTroops, hasAtLeastOneTroopLeft, threeUnitCheck);
    public static final Validator validDefendingTroops = input -> compose(input, nonEmpty, isInt, appropriateForce, defenderCanDefend, twoUnitCheck);
    public static final Validator validDefendingCountry = input -> compose(input, nonEmpty, validCountryName, currentPlayerDoesNotOccupy, adjacentCountry);
    public static final Validator validUseOfCards = input -> compose(input, yesNo, nonEmpty, canSpendCards);

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

    public static final Validator fortificationOriginCountry = rawInput -> compose(rawInput, nonEmpty, currentPlayerOccupies, input -> {
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

    private static boolean isConnected(Country from, Country to, List<Integer> visited) {
        if (from.equals(to)) return true;

        int[] adjacentCountries = from.getAdjCountries();
        boolean foundLink = false;
        visited.add(from.getId());

        MapModel mapModel = MapModel.getInstance();
        PlayerModel playerModel = PlayerModel.getInstance();
        Player currentPlayer = playerModel.getCurrentPlayer();

        for (int adjacentCountryIndex : adjacentCountries) {
            String adjacentCountryName = Constants.COUNTRY_NAMES[adjacentCountryIndex];
            Optional<Country> nullableAdjacentCountry = mapModel.getCountryByName(adjacentCountryName);

            if (nullableAdjacentCountry.isPresent()) {
                Country adjacentCountry = nullableAdjacentCountry.get();
                boolean haveVisited = visited.contains(adjacentCountry.getId());
                if (adjacentCountry.getOccupier().equals(currentPlayer) && !haveVisited) {
                    foundLink = foundLink || isConnected(adjacentCountry, to, visited);
                }
            } else {
                // Should never be reached (adjacent country always present)
                return false;
            }
        }
        return foundLink;
    }

    public static final ValidatorBuilder<Country> fortificationDestinationCountry = originCountry -> rawInput -> compose(rawInput, nonEmpty, currentPlayerOccupies, input -> {
        MapModel mapModel = MapModel.getInstance();
        Optional<Country> nullableDestinationCountry = mapModel.getCountryByName(input);

        if (nullableDestinationCountry.isPresent()) {
            Country destinationCountry = nullableDestinationCountry.get();

            boolean connected = isConnected(originCountry, destinationCountry, new ArrayList<>());

            if (!connected)
                return ValidatorResponse.invalid(originCountry.getCountryName() + " is not connected to " + destinationCountry.getCountryName());
        }
        return ValidatorResponse.validNoMessage();
    });

    public static final ValidatorBuilder<Country> validFortification = originCountry -> rawInput -> compose(rawInput, nonEmpty, isInt, appropriateForce, input -> {
        int desiredNumberOfTroops = Integer.parseInt(input);

        return new ValidatorResponse(desiredNumberOfTroops < originCountry.getArmyCount(), "Not enough troops in " + originCountry.getCountryName());
    });

    private static ValidatorResponse compose(String input, Validator... validators) {

        for (Validator validator : validators) {
            ValidatorResponse validatorResponse = validator.validate(input);
            if (!validatorResponse.isValid()) {
                return validatorResponse;
            }
        }

        return new ValidatorResponse(null);
    }
}
