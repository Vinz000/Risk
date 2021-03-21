package game.module;

import common.validation.Validators;
import map.country.Country;
import player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Fortification extends Module {
    public Fortification() {
    }

    public boolean prompt() {

        boolean canFortify = false;
        for (Country playerOwnedCountry : playerModel.getCurrentPlayer().getOwnedCountries()) {
            boolean canFortifyThisCountry = Validators.fortificationOriginCountry
                    .apply(playerOwnedCountry.getCountryName()).isValid();

            if (canFortifyThisCountry) {
                canFortify = true;
                break;
            }

        }

        if (!canFortify) {
            shellModel.notify("Cannot fortify.");
            return false;
        }

        shellModel.notify("Do you want to fortify?");
        String response = shellModel.prompt(Validators.yesNo);

        return response.contains("y");
    }

    public Optional<Country> selectOriginCountry() {
        shellModel.notify("Which country will you pull troops from?");
        List<Country> availableOriginCountries = playerModel
                .getCurrentPlayer()
                .getOwnedCountries()
                .stream()
                .filter(country -> country.getArmyCount() > 1)
                .collect(Collectors.toList());
        mapModel.highlightCountries(availableOriginCountries);
        String response = shellModel.prompt(Validators.fortificationOriginCountry);
        mapModel.highlightCountries(availableOriginCountries);

        return mapModel.getCountryByName(response);
    }

    public Optional<Country> selectDestinationCountry(Country from) {
        shellModel.notify("Which country will you move troops to?");
        Player currentPlayer = playerModel.getCurrentPlayer();
        mapModel.highlightCountries(currentPlayer.getOwnedCountries());
        String response = shellModel.prompt(Validators.fortificationDestinationCountry.apply(from));
        mapModel.highlightCountries(currentPlayer.getOwnedCountries());

        return mapModel.getCountryByName(response);
    }

    public int selectNumberOfTroops(Country from) {
        shellModel.notify("How many troops will you move?");
        String response = shellModel.prompt(Validators.validFortification.apply(from));

        return Integer.parseInt(response);
    }

    public void moveTroops(Country from, Country to, int troopsCount) {
        mapModel.updateCountryArmyCount(from, -troopsCount);
        mapModel.updateCountryArmyCount(to, troopsCount);
    }
}
