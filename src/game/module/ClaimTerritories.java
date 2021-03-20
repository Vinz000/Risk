package game.module;

import common.Constants;
import common.validation.Validators;
import deck.Card;
import deck.Deck;
import map.country.Country;
import player.NeutralPlayer;
import player.Player;

import java.util.List;
import java.util.Optional;

import static common.Constants.*;

// TODO: Change response to more meaningful variable name
public class ClaimTerritories extends Module{
    String response;

    public ClaimTerritories() {
    }

    public void assignInitialCountries() {
        Deck deck = Deck.getInstance();
        shellModel.notify(Constants.Notifications.TERRITORY);
        shellModel.notify(Constants.Notifications.TERRITORY_OPTION);
        String response = shellModel.prompt(Validators.yesNo);
        boolean showDraw = response.toLowerCase().contains("y");


        List<Player> players = playerModel.getPlayers();
        assignPlayersCountries(showDraw, players);

        deck.addWildcards();
        deck.shuffle();
        mapModel.showCountryComponents();
    }

    // HELPER METHOD
    private void assignPlayersCountries(boolean showDraw, List<Player> players) {
        Deck deck = Deck.getInstance();

        players.forEach(player -> {
            Optional<Card> nullableCard;
            int numCountriesToAssign = player instanceof NeutralPlayer ? INIT_COUNTRIES_NEUTRAL : INIT_COUNTRIES_PLAYER;

            for (int i = 0; i < numCountriesToAssign; i++) {
                nullableCard = deck.drawCard();

                nullableCard.ifPresent(drawnCard -> {
                    String drawnCountryName = drawnCard.getCountryName();
                    Optional<Country> nullableCountry = mapModel.getCountryByName(drawnCountryName);
                    nullableCountry.ifPresent(country -> {
                        mapModel.setCountryOccupier(country, player);
                        mapModel.updateCountryArmyCount(country, 1);
                        player.addCountry(country);
                    });

                    if (showDraw) {
                        String playerName = player.getName();
                        shellModel.notify(playerName + Notifications.DRAWN + drawnCountryName);
                    }

                    deck.add(drawnCard);
                });
            }
        });
    }


}
