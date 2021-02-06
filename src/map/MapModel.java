package map;

import shell.Player;

import java.util.ArrayList;
import java.util.Collections;
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
        Collections.shuffle(countries);
        int currentPlayerIndex = 0;
        for (int i = 0; i < NUM_COUNTRIES; i++) {
            if (i < INIT_COUNTRIES_PLAYER * 2) {
                countries.get(i).setCurrentPlayer(players.get(currentPlayerIndex));
                currentPlayerIndex++;
                currentPlayerIndex %= 2;
            }
            countries.get(i).setArmy(1);
        }
    }

}

