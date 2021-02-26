package common.validation;

import map.country.Country;
import map.model.MapModel;
import player.Player;
import player.model.PlayerModel;

import java.util.Arrays;
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
        String invalidMessage = null;

        if (inputCountry.isPresent()) {
            Country country = inputCountry.get();
            Player countryOccupier = country.getOccupier();

            if (!countryOccupier.equals(player)) {
                invalidMessage = String.format("%s does not own %s", player.getName(), country.getCountryName());
            }
        } else {
            invalidMessage = String.format("%s is not a valid country name", countryName);
        }

        return invalidMessage;
    }
}
