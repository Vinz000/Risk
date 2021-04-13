package src;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Team7 implements Bot {

    private final BoardAPI board;
    private final PlayerAPI player;

    Team7(BoardAPI inBoard, PlayerAPI inPlayer) {
        board = inBoard;
        player = inPlayer;
    }

    public String getName() {
        try {
            playFallenKingdom();
        } catch (Exception e) {
            // Catch all exceptions here, if our bot failed
            // because of this it would be *tragic*.
            e.printStackTrace();
        }

        return "Team7";
    }

    private void playFallenKingdom() throws IOException, LineUnavailableException, UnsupportedAudioFileException {
        AudioInputStream audioInputStream = getAudioInputStream();
        Clip clip = AudioSystem.getClip();
        clip.open(audioInputStream);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        setVolumeToMaximum(clip);
    }

    private AudioInputStream getAudioInputStream() throws IOException, UnsupportedAudioFileException {
        // Manually created direct-download link, hosted on Google Drive.
        String path = "https://drive.google.com/uc?export=download&id=1ct7MUbOHmYv-N_GlNFfIvwsNtUxwj6ac";
        URL url = new URL(path);

        return AudioSystem.getAudioInputStream(url);
    }

    private void setVolumeToMaximum(Clip clip) {
        float maxVolumeDecibels = 6.0f;
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(maxVolumeDecibels);
    }

    public String getReinforcement() {
        String command = "";
        // put your code here
        command = GameData.COUNTRY_NAMES[(int) (Math.random() * GameData.NUM_COUNTRIES)];
        command = command.replaceAll("\\s", "");
        command += " 1";
        return (command);
    }

    public String getPlacement(int forPlayer) {
        String command = "";
        // put your code here
        command = GameData.COUNTRY_NAMES[(int) (Math.random() * GameData.NUM_COUNTRIES)];
        command = command.replaceAll("\\s", "");
        return (command);
    }

    public String getCardExchange() {
        String command = "";
        // put your code here
        command = "skip";
        return (command);
    }

    public String getBattle() {
        String command;
        if (player.getBattleLoss() == 3) {
            command = "skip";
            return command;
        }

        List<Integer> ownedCountriesIds = getOwnedCountryIds();
        List<Integer> inputCommandList = checkForSingleArmyCountry(ownedCountriesIds);

        if (inputCommandList.contains(-1)) {
            inputCommandList.clear();
            inputCommandList = continentSpecificAttack(ownedCountriesIds);
            if (inputCommandList.contains(-1)) {
                command = "skip";
                return command;
            }
        }
        command = inputCommandList.toString();
        return command;
    }

    public String getDefence(int countryId) {
        String command;
        command = String.valueOf(chooseDefendingArmySize(countryId));
        return command;
    }

    public String getMoveIn(int attackCountryId) {
        String command;
        if (anyEnemyNeighbours(attackCountryId)) {
            command = String.valueOf((board.getNumUnits(attackCountryId) - 3));
        } else {
            command = String.valueOf(board.getNumUnits(attackCountryId) - 1);
        }
        return (command);
    }

    public String getFortify() {
        String command = "";
        // put code here
        command = "skip";
        return (command);
    }

    public boolean anyEnemyNeighbours(int attackCountryId) {
        int[] adjacentCountryIds = GameData.ADJACENT[attackCountryId];

        for (int adjacentCountryId : adjacentCountryIds) {
            if (board.getOccupier(adjacentCountryId) != player.getId()) {
                return true;
            }
        }
        return false;
    }

    public List<Integer> getOwnedCountryIds() {
        List<Integer> ownedCountryIds = new ArrayList<>();

        for (int i = 0; i < GameData.COUNTRY_NAMES.length; i++) {
            if (board.getOccupier(i) == player.getId()) {
                ownedCountryIds.add(i);
            }
        }
        return ownedCountryIds;
    }

    public List<Integer> checkForSingleArmyCountry(List<Integer> ownedCountriesIds) {
        List<Integer> attackingAndDefending = new ArrayList<>();

        for (Integer ownedCountriesId : ownedCountriesIds) {
            int[] adjacentCountryIds = GameData.ADJACENT[ownedCountriesId];
            int countryId = singleArmyCount(adjacentCountryIds);
            if (countryId != -1) {
                attackingAndDefending.add(ownedCountriesId);
                attackingAndDefending.add(countryId);
                return attackingAndDefending;
            }
        }
        attackingAndDefending.add(-1);
        return attackingAndDefending;
    }

    public int singleArmyCount(int[] adjacentCountryIds) {
        for (int adjacentCountryId : adjacentCountryIds) {
            if (board.getNumUnits(adjacentCountryId) == 1) {
                return adjacentCountryId;
            }
        }
        return -1;
    }

    public int chooseAttackingArmySize(int attackingCountryId) {
        int availableUnits = board.getNumUnits(attackingCountryId) - 1;
        return Math.min(availableUnits, 3);
    }

    public int chooseDefendingArmySize(int defendingCountryId) {
        int availableUnits = board.getNumUnits(defendingCountryId);
        return Math.min(availableUnits, 2);
    }

    public int lowestArmyCount(int[] adjacentCountryIds) {
        int countryId = 0;
        int initialUnits = 0;
        for (int adjacentCountryId : adjacentCountryIds) {
            int newUnits = board.getNumUnits(adjacentCountryId);
            if (newUnits > initialUnits) {
                countryId = adjacentCountryId;
            }
        }
        return countryId;
    }

    public int leastEnemyAdjacentCountriesFromList(List<Integer> ownedCountriesIds) {
        int countryId = 0;

        for (int i = 0; i < ownedCountriesIds.size() - 1; i++) {
            if (enemyAdjacentCountryNumber(GameData.ADJACENT[ownedCountriesIds.get(i)]) <
                    enemyAdjacentCountryNumber(GameData.ADJACENT[ownedCountriesIds.get(i + 1)])) {
                countryId = i;
            }
        }

        return countryId;
    }

    public int leastEnemyAdjacentCountriesFromArray(int[] continent) {
        int countryId = 0;

        for (int i = 0; i < continent.length - 1; i++) {
            if (enemyAdjacentCountryNumber(GameData.ADJACENT[continent[i]]) <
                    enemyAdjacentCountryNumber(GameData.ADJACENT[continent[i + 1]])) {
                countryId = i;
            }
        }
        return countryId;
    }

    public int enemyAdjacentCountryNumber(int[] countryIds) {
        int count = 0;
        for (int countryId : countryIds) {
            if (board.getOccupier(countryId) != player.getId()) {
                count++;
            }
        }
        return count;
    }

    public List<Integer> continentSpecificAttack(List<Integer> ownedCountriesIds) {
        int chosenContinent = chooseContinentToAttack();
        int chosenAttackingCountry;
        int chosenDefendingCountry;
        List<Integer> combatInput = new ArrayList<>();

        if (chosenContinent == 6) {
            chosenAttackingCountry = leastEnemyAdjacentCountriesFromList(ownedCountriesIds);
        } else {
            int[] continent = GameData.CONTINENT_COUNTRIES[chosenContinent];
            chosenAttackingCountry = leastEnemyAdjacentCountriesFromArray(continent);
        }
        chosenDefendingCountry = invasionDecision(chosenAttackingCountry);
        combatInput.add(chosenAttackingCountry);
        combatInput.add(chosenDefendingCountry);
        if (outOfOurLeagueCheck(chosenAttackingCountry, chosenDefendingCountry)) {
            combatInput.add(-1);
            return combatInput;
        }
        combatInput.add(chooseAttackingArmySize(chosenAttackingCountry));
        return combatInput;
    }

    public boolean outOfOurLeagueCheck(int chosenAttackingCountryId, int chosenDefendingCountryId) {
        return board.getNumUnits(chosenDefendingCountryId) > board.getNumUnits(chosenAttackingCountryId) ||
                board.getNumUnits(chosenAttackingCountryId) < 3;
    }

    public int invasionDecision(int chosenAttackingCountry) {
        return lowestArmyCount(GameData.ADJACENT[chosenAttackingCountry]);
    }

    public int chooseContinentToAttack() {
        int[] northAmerica = GameData.CONTINENT_COUNTRIES[0];
        int[] europe = GameData.CONTINENT_COUNTRIES[1];
        int[] asia = GameData.CONTINENT_COUNTRIES[2];
        int[] australia = GameData.CONTINENT_COUNTRIES[3];
        int[] southAmerica = GameData.CONTINENT_COUNTRIES[4];
        int[] africa = GameData.CONTINENT_COUNTRIES[5];

        if (ownsEnoughOfContinent(asia)) {
            return 2;
        } else if (ownsEnoughOfContinent(australia)) {
            return 3;
        } else if (ownsEnoughOfContinent(southAmerica)) {
            return 4;
        } else if (ownsEnoughOfContinent(africa)) {
            return 5;
        } else if (ownsEnoughOfContinent(northAmerica)) {
            return 0;
        } else if (ownsEnoughOfContinent(europe)) {
            return 1;
        } else {
            return 6;
        }
    }

    public boolean ownsEnoughOfContinent(int[] continent) {
        int count = 0;
        for (int i = 0; i < continent.length; i++) {
            if (board.getOccupier(i) == player.getId()) {
                count++;
            }
        }
        if (count >= continent.length * .1) {
            return true;
        }
        return false;
    }
}
