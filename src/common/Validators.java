package common;

import player.Player;
import map.Country;
import map.MapModel;
import player.PlayerModel;

import java.util.Optional;
import java.util.function.Function;

public class Validators {
    public static final Function<String, Boolean> alwaysValid = input -> true;

    public static final Function<String, Boolean> nonEmpty = input -> !input.isEmpty();

    public static final Function<String, Boolean> yesNo = input ->
            input.equalsIgnoreCase("yes") ||
                    input.toLowerCase().equals("n") ||
                    input.equalsIgnoreCase("no") ||
                    input.toLowerCase().equals("y");

    public static final Function<String, Boolean> currentPlayerOwns = input -> {
        PlayerModel playerModel = PlayerModel.getInstance();
        Player currentHumanPlayer = playerModel.getCurrentHumanPlayer();

        return ownsCountryNode(currentHumanPlayer, input);
    };

    public static final Function<String, Boolean> neutralPlayerOwns = input -> {
        PlayerModel playerModel = PlayerModel.getInstance();
        Player firstNeutralPlayer = playerModel.getNeutralPlayers().get(0);

        return ownsCountryNode(firstNeutralPlayer, input);
    };

    // Helper functions
    private static boolean validCountry(String input) {
        MapModel mapModel = MapModel.getInstance();
        int numMatches = (int) mapModel
                .getCountries()
                .stream()
                .filter(countryNode -> countryNode.getCountryName().toLowerCase().contains(input))
                .count();

        return numMatches == 1;
    }

    private static boolean ownsCountryNode(Player player, String input) {
        if (validCountry(input)) {

            MapModel mapModel = MapModel.getInstance();
            Optional<Country> countryNode = mapModel.getCountryByName(input);
            return countryNode.map(node -> node.getCurrentPlayer().equals(player)).orElse(false);

        }

        return false;
    }
}
