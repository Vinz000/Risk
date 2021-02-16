package map;

import shell.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.function.Consumer;

import static common.Constants.*;

public class MapModel extends Observable {
    private final List<CountryNode> countries = new ArrayList<>();
    private final List<Player> players = new ArrayList<>(NUM_PLAYERS);

    public MapModel() {
        initializeCountries();
    }

    private void initializeCountries() {
        for (int i = 0; i < NUM_COUNTRIES; i++) {
            countries.add(new CountryNode(COUNTRY_NAMES[i], ADJACENT[i], CONTINENT_IDS[i], COUNTRY_COORDS[i]));
        }
    }

    public List<CountryNode> getCountries() {
        return countries;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void initializeGame() {

        int currentPlayerIndex = 0;
        int playerControlledCountries = 0;

        // While playerControlledCountries is less than the initial starting requirement continue to iterate.
        while (playerControlledCountries < INIT_COUNTRIES_PLAYER * 2) {
            
            // Iterates through the players hand positions and gets the country name on the card.
            for (int i = 0; i < INIT_COUNTRIES_PLAYER; i++) {
                String playerCountryName= players.get(currentPlayerIndex).getHand().get(i).getCountryName();
                
                // Iterates through the country list to find a matching country name.
                for (CountryNode country : countries) {
                    if (playerCountryName.equals(country.getCountryName())) {
                        //Sets that country as a players if a match is found.
                        country.setCurrentPlayer(players.get(currentPlayerIndex));
                        playerControlledCountries++;
                    }
                    country.setArmy(1);
                }
            }
            currentPlayerIndex++;
        }
    }

    public void forEachPlayer(int maxIndex, Consumer<Player> callback) {
        for (int i = 0; i < maxIndex; i++) {
            Player currentPlayer = getPlayer(i);
            callback.accept(currentPlayer);
        }
    }

    public Player getPlayer(int i) {
        return players.get(i);
    }

}

