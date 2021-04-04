package common.validation;

import common.Constants;
import deck.Card;
import deck.CardSet;
import deck.CardType;
import map.country.Country;
import map.model.MapModel;
import player.Player;
import player.model.PlayerModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


public class Validators {
    public static final Validator alwaysValid = input -> ValidatorResponse.validNoMessage();

    public static final Validator nonEmpty = input -> {
        boolean isValid = !input.trim().isEmpty();
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

    public static final Validator validPlayerName = input -> compose(input, nonEmpty, is15CharactersMax);

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
                return ValidatorResponse.invalid("You do not own adjacent countries.");
            } else if (!hasMoreThanOneArmy) {
                return ValidatorResponse.invalid("Not enough armies in " + originCountry.getCountryName());
            }
        }

        return ValidatorResponse.validNoMessage();
    });

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

    private static final Validator validCountryName = input -> {
        MapModel mapModel = MapModel.getInstance();
        Optional<Country> nullableCountry = mapModel.getCountryByName(input);

        if (nullableCountry.isPresent()) {
            return ValidatorResponse.validNoMessage();
        }

        return ValidatorResponse.invalid("Not a valid country name");
    };

    public static final Validator validReinforcingCountry = input -> compose(input, nonEmpty, validCountryName, currentPlayerOccupies);

    public static final Validator validDefendingCountry = input -> compose(input, nonEmpty, validCountryName, currentPlayerDoesNotOccupy, adjacentCountry);

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

    public static final Validator validAttackingCountry = input -> compose(input, nonEmpty, validCountryName, currentPlayerOccupies, hasAdjacentOpposingCountry, singleUnit);

    public static Validator appropriateForce = input -> {
        int force = Integer.parseInt(input);
        boolean appropriateForce = force >= 1;
        return new ValidatorResponse(appropriateForce, "You did not select an appropriately sized force.");
    };

    public static final Validator canPlaceTroops = input -> compose(input, nonEmpty, isInt, appropriateForce, hasEnoughReinforcements);

    public static final Validator validReinforcement = input -> compose(input, nonEmpty, isInt, appropriateForce, enoughTroops);

    public static final ValidatorBuilder<Country> validFortification = originCountry -> rawInput -> compose(rawInput, nonEmpty, isInt, appropriateForce, input -> {
        int desiredNumberOfTroops = Integer.parseInt(input);

        return new ValidatorResponse(desiredNumberOfTroops < originCountry.getArmyCount(), "Not enough troops in " + originCountry.getCountryName());
    });

    public static final Validator threeUnitCheck = input -> {
        boolean lessThanFour = Integer.parseInt(input) < 4;
        return new ValidatorResponse(lessThanFour, "You cannot attack with more than 3 units per battle.");
    };

    public static final Validator twoUnitCheck = input -> {
        boolean lessThanThree = Integer.parseInt(input) < 3;
        return new ValidatorResponse(lessThanThree, "You cannot defend with more than 2 units per battle.");
    };

    public static final Validator hasAtLeastOneTroopLeft = input -> {
        MapModel mapModel = MapModel.getInstance();
        Country attackingCountry = mapModel.getAttackingCountry();
        int troops = Integer.parseInt(input);

        boolean hasAtLeastOneTroopLeft = false;

        if (attackingCountry.getArmyCount() - troops >= 1) {
            hasAtLeastOneTroopLeft = true;
        }

        return new ValidatorResponse(hasAtLeastOneTroopLeft, "You need to keep at least one troop back.");
    };

    public static final Validator validAttackingTroops = input -> compose(input, nonEmpty, isInt, appropriateForce, enoughTroops, hasAtLeastOneTroopLeft, threeUnitCheck);

    public static final Validator defenderCanDefend = input -> {
        MapModel mapModel = MapModel.getInstance();
        int defendingForce = Integer.parseInt(input);
        int defendingCountryArmy = mapModel.getDefendingCountry().getArmyCount();

        boolean validDefendingForce = defendingForce <= defendingCountryArmy;

        return new ValidatorResponse(validDefendingForce, String.format("You don't have %d army, you only have %d.", defendingForce, defendingCountryArmy));
    };
    public static final Validator validDefendingTroops = input -> compose(input, nonEmpty, isInt, appropriateForce, defenderCanDefend, twoUnitCheck);

    public static final Validator cardChoiceCheck = input -> {
        List<Card> cardsSelected = parseInsignias(input);

        boolean selectedThreeCards = cardsSelected.size() == 3;
        if (!selectedThreeCards) return ValidatorResponse.invalid("You did not select 3 cards");

        boolean isValidCardSet = validCombinations(cardsSelected);
        if (!isValidCardSet) return ValidatorResponse.invalid("Not a valid combination of cards.");

        boolean playerHasSelectedCards = playerHasCards(cardsSelected);
        if (!playerHasSelectedCards) return ValidatorResponse.invalid("You do not have the selected cards.");

        return ValidatorResponse.validNoMessage();
    };

    /**
     * Converts card insignias to a List<Card>.
     *
     * @param insignias A combination of the first letters of
     *                  CardTypes in one string.
     * @return The list of cards which is represented by the
     * insignias.
     */
    public static List<Card> parseInsignias(String insignias) {
        List<Card> cards = new ArrayList<>();

        for (CardType cardType : CardType.values()) {
            int occurrences = countCharOccurrences(insignias.toLowerCase(), cardType.toString().charAt(0));

            Card card = new Card(cardType);

            for (int i = 0; i < occurrences; i++) {
                cards.add(card);
            }
        }

        return cards;
    }

    private static boolean playerHasCards(List<Card> cards) {
        PlayerModel playerModel = PlayerModel.getInstance();
        Player currentPlayer = playerModel.getCurrentPlayer();

        return CardSet.containsAll(currentPlayer.getCards(), cards);
    }

    private static int countCharOccurrences(String searchString, char target) {
        return (int) searchString.chars().filter(character -> character == target).count();
    }

    public static boolean validCombinations(List<Card> cards) {
        return Arrays.stream(CardSet.values()).anyMatch(cardSet -> cardSet.matches(cards));
    }

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
