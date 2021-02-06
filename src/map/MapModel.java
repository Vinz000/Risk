package map;

import common.Constants;
import shell.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;

import static common.Constants.*;

public class MapModel extends Observable {
    private List<CountryNode> countries = new ArrayList<>();
    private List<Player> players = new ArrayList<>();

    public MapModel() {
        initializeCountries();
    }

    private void initializeCountries() {
        for (int i = 0; i < NUM_COUNTRIES; i++) {
            countries.add(new CountryNode(COUNTRY_NAMES[i], ADJACENT[i], CONTINENT_IDS[i], COUNTRY_COORD[i]));
        }
    }

    public List<CountryNode> getCountries() {
        return countries;
    }

    public void addPlayer(String name) {
        players.add(new Player(name));
    }

    public void initializeGame() {
        Collections.shuffle(countries);
        int currPlayer = 0;
        for (int i = 0; i < NUM_COUNTRIES; i++) {
            if (i < INIT_COUNTRIES_PLAYER * 2) {
                countries.get(i).setCurrentPlayer(players.get(currPlayer));
                currPlayer++;
                currPlayer %= 2;
            }
            countries.get(i).setArmy(1);
        }
    }

}

