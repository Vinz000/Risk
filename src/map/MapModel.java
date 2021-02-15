package map;

import shell.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

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
        String temp;

        int counter = 0;
        while (counter < INIT_COUNTRIES_PLAYER * 2) {
            for (int i = 0; i < players.get(currentPlayerIndex).getHand().size(); i++) {
                for (int j = 0; j < NUM_COUNTRIES; j++) {
                    temp = players.get(currentPlayerIndex).getHand().get(i).getCountryName();
                    if (temp.equals(countries.get(j).getCountryName())) {
                        countries.get(j).setCurrentPlayer(players.get(currentPlayerIndex));
                        countries.get(j).setArmy(1);
                        counter++;
                    }
                }
            }
            currentPlayerIndex++;
        }
    }

    public Player getPlayer(int i) {
        return players.get(i);
    }


}

