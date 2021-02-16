package common;

import player.Player;
import map.CountryNode;
import map.MapModel;

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
    private static final MapModel mapModel = MapModel.getMapModel();

    public static final Function<String, Boolean> currentPlayerOwns = input -> {
        Player player = mapModel.getCurrentPlayer();

        return ownsCountryNode(player, input);
    };

    public static final Function<String, Boolean> neutralPlayerOwns = input -> {
        Player player = mapModel.getNeutralPlayers().get(0);

        return ownsCountryNode(player, input);
    };

    // Helper functions
    private static boolean validCountry(String input) {
        int numMatches = (int) mapModel
                .getCountries()
                .stream()
                .filter(countryNode -> countryNode.getCountryName().toLowerCase().contains(input))
                .count();

        return numMatches == 1;
    }

    private static boolean ownsCountryNode(Player player, String input) {
        if (validCountry(input)) {

            Optional<CountryNode> countryNode = mapModel.fetchCountry(input);
            return countryNode.map(node -> node.getCurrentPlayer().equals(player)).orElse(false);

        }

        return false;
    }
}
