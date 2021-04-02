package game.module;

import common.validation.Validators;
import map.country.Country;
import player.Player;

import java.util.Optional;

public class Reinforcing extends Module {
    // TODO: Refactor response to a more meaningful variable name
    String response;

    public Reinforcing() {
    }

    public void reinforceInitialCountries(Player player, int reinforcement) {

        mapModel.highlightCountries(player.getOwnedCountries());

        shellModel.notify("Place down reinforcements.");

        String response = shellModel.prompt(Validators.validReinforcingCountry);
        Optional<Country> nullableCountry = mapModel.getCountryByName(response);

        nullableCountry.ifPresent(country -> mapModel.updateCountryArmyCount(country, reinforcement));

        shellModel.notify("Successfully placed reinforcements.");

        mapModel.highlightCountries(player.getOwnedCountries());
    }

    public void reinforceOwnedCountries(Player player) {

        mapModel.highlightCountries(player.getOwnedCountries());
        while (player.getReinforcements() > 0) {
            shellModel.notify(String.format("You have %d reinforcements to place.", player.getReinforcements()));
            shellModel.notify("Choose country to reinforce");

            response = shellModel.prompt(Validators.validReinforcingCountry);
            Optional<Country> chosenCountry = mapModel.getCountryByName(response);

            shellModel.notify("How many army to reinforce by?");
            response = shellModel.prompt(Validators.canPlaceTroops);
            int numReinforce = Integer.parseInt(response);

            chosenCountry.ifPresent(validCountry -> mapModel.updateCountryArmyCount(validCountry, numReinforce));

            shellModel.notify("Successfully placed reinforcements.");

            player.setReinforcements(player.getReinforcements() - numReinforce);
        }
        mapModel.highlightCountries(player.getOwnedCountries());
    }
}
