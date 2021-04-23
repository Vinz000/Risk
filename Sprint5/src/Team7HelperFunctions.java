package src;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static src.GameData.*;

/**
 * TODO:
 * - Estimate current golden cavalry size by guessing how many troops we should have before
 * - Update currentOccupiers after at fortification
 */

public class Team7HelperFunctions {

    private final BoardAPI board;
    private final PlayerAPI player;
    private final int otherPlayerId;

    Team7HelperFunctions(BoardAPI inBoard, PlayerAPI inPlayer, int otherPlayerId) {
        this.board = inBoard;
        this.player = inPlayer;
        this.otherPlayerId = otherPlayerId;
    }

    public String convertToCommand(List<Integer> inputList) {
        StringBuilder command = new StringBuilder();
        for (int i = 0; i < 2; i++) {
            command.append(GameData.COUNTRY_NAMES[inputList.get(i)].replaceAll("\\s+", ""));
            command.append(" ");
        }
        command.append(inputList.get(2));
        return String.valueOf(command);
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

        for (int i = 0; i < GameData.NUM_COUNTRIES; i++) {
            if (board.getOccupier(i) == player.getId()) {
                ownedCountryIds.add(i);
            }
        }
        return ownedCountryIds;
    }

    public int chooseAttackingArmySize(int attackingCountryId) {
        int availableUnits = board.getNumUnits(attackingCountryId) - 1;
        return Math.min(availableUnits, 3);
    }

    public int chooseDefendingArmySize(int defendingCountryId) {
        int availableUnits = board.getNumUnits(defendingCountryId);
        return Math.min(availableUnits, 2);
    }

    public void playBattleCry() {
        try {
            AudioInputStream audioInputStream = getAudioInputStream();
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            setVolumeToMaximum(clip);
        } catch (Exception ignored) {
        }
    }

    private AudioInputStream getAudioInputStream() throws IOException, UnsupportedAudioFileException {
        // Manually created direct-download link, hosted on Google Drive.
        String path = "https://drive.google.com/uc?export=download&id=1Gt4QWtHpf_PLvENPeP6s-gze6RFxMVMt";
        URL url = new URL(path);

        return AudioSystem.getAudioInputStream(url);
    }

    private void setVolumeToMaximum(Clip clip) {
        float maxVolumeDecibels = 6.0f;
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(maxVolumeDecibels);
    }

    public void sleep() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException ignored) {
        }
    }

    public String getValidCardCombination() {
        List<Integer> insigniaIds = getInsigniaIds();

        for (int i = 0; i < insigniaIds.size() - 2; i++) {
            for (int j = i + 1; j < insigniaIds.size() - 1; j++) {
                for (int k = j + 1; k < insigniaIds.size(); k++) {
                    int[] cards = {insigniaIds.get(i), insigniaIds.get(j), insigniaIds.get(k)};
                    if (Deck.isASet(cards)) {
                        return insigniaIdsToStringCommand(cards);
                    }
                }
            }
        }
        return "skip";
    }

    private List<Integer> getInsigniaIds() {
        List<Card> cards = player.getCards();

        return cards
                .stream()
                .map(Card::getInsigniaId)
                .collect(Collectors.toList());
    }

    private String insigniaIdsToStringCommand(int[] insigniaIds) {
        String[] insigniaShortHand = {"i", "c", "a", "w"};

        return Arrays
                .stream(insigniaIds)
                .mapToObj(insigniaId -> insigniaShortHand[insigniaId])
                .collect(Collectors.joining());
    }

    public List<Integer> getOwnedCountryIds(int playerId) {
        List<Integer> countryIds = new ArrayList<>();

        for (int countryId = 0; countryId < NUM_COUNTRIES; countryId++) {
            int occupier = board.getOccupier(countryId);
            if (occupier == playerId) {
                countryIds.add(countryId);
            }
        }

        return countryIds;
    }

    public List<Integer> getAlmostConqueredContinentIds(int playerId) {
        List<Integer> almostConqueredContinents = new ArrayList<>();

        for (ContinentRanking continent : ContinentRanking.values()) {
            int continentId = continent.continentId;
            int[] countriesInContinent = CONTINENT_COUNTRIES[continentId];

            IntPredicate botOccupiesCountry = country -> board.getOccupier(country) == playerId;

            int ownedCountriesInContinent = (int) Arrays
                    .stream(countriesInContinent)
                    .filter(botOccupiesCountry)
                    .count();


            if (ownedCountriesInContinent > countriesInContinent.length * 0.25) {
                almostConqueredContinents.add(continentId);
            }
        }

        return almostConqueredContinents;
    }

    public int numSurroundingCountriesByPlayer(int countryId, int playerId) {
        return (int) Arrays
                .stream(ADJACENT[countryId])
                .filter(country -> board.getOccupier(country) == playerId)
                .count();
    }

    public boolean continentContainsCountry(int continentId, int countryId) {
        IntPredicate isCountry = country -> country == countryId;

        return Arrays
                .stream(CONTINENT_COUNTRIES[continentId])
                .anyMatch(isCountry);
    }

    public String formatCommandForReinforcing(int country, int numTroops) {
        String countryName = COUNTRY_NAMES[country];
        String commandValidCountryName = countryName.replaceAll("\\s", "");

        return String.format("%s %d", commandValidCountryName, numTroops);
    }

    private boolean isCountryInContinent(int countryId, int continentId) {
        return Arrays
                .stream(CONTINENT_COUNTRIES[continentId])
                .anyMatch(country -> country == countryId);
    }

    public int rateOwnCountryForReinforcement(int countryId) {
        int rating = commonRatings(countryId);

        // We rate by how many surrounding enemy countries there are
        rating += numSurroundingCountriesByPlayer(countryId, otherPlayerId) * 50;

        // We also prioritise if they have less than 8 units
        if (board.getNumUnits(countryId) < 8) {
            rating += 1000;
        }

        // If it is only surround by friendly countries then we try ignore it
        if (isSurroundedByFriendlyCountries(countryId)) {
            rating -= 2500;
        }

        // We also prioritise if they have less than 8 units
        if (board.getNumUnits(countryId) == 1) rating += 2500;

        return -rating;
    }

    public int rateOwnCountryForAttacking(int countryId) {
        int rating = commonRatings(countryId);
        int[] ownedCountries = ADJACENT[countryId];
        List<Integer> adjacentCountries = new ArrayList<>();

        for (int ownedCountry : ownedCountries) {
            if (board.getOccupier(ownedCountry) != player.getId()) {
                adjacentCountries.add(ownedCountry);
            }
        }

        rating += singleArmyEnemyPresent(adjacentCountries).isEmpty() ? 10 : 500;
        rating += hasLessThanThreeAdjacentEnemyCountries(adjacentCountries).isEmpty() ? 10 : 400;
        rating += enemyCountryPresent(adjacentCountries).isEmpty() ? 0 : 200;

        if (checkUnits(countryId) > 3 && checkUnits(countryId) < 5) {
            rating += 50;
        } else if (checkUnits(countryId) >= 5 && checkUnits(countryId) < 10) {
            rating += 100;
        } else if (checkUnits(countryId) >= 10) {
            rating += 1000;
        }

        // We also prioritise if they have less than 8 units
        if (board.getNumUnits(countryId) == 1) rating += 2500;

        return -rating;
    }

    public int rateEnemyDefendingCountry(int countryId) {
        int rating = commonRatings(countryId);

        if (hasSingleArmy(countryId) && !spreadingTooThin(countryId)) {
            rating += 100000;
        }
        if (checkUnits(countryId) < 3) {
            rating += 250;
        }
        if (checkUnits(countryId) < 5) {
            rating += 25;
        }
        if (lessThanThreeAdjacent(countryId)) {
            rating += 250;
        }
        if (lessThanFourAdjacent(countryId)) {
            rating += 25;
        }
        return -rating;
    }

    public int checkUnits(int countryId) {
        return board.getNumUnits(countryId);
    }

    public List<Integer> enemyCountryPresent(List<Integer> adjacentCountries) {
        return adjacentCountries
                .stream()
                .filter(this::isEnemyCountry)
                .collect(Collectors.toList());
    }

    public List<Integer> hasLessThanThreeAdjacentEnemyCountries(List<Integer> adjacentCountries) {
        return adjacentCountries
                .stream()
                .filter(this::lessThanFourAdjacent)
                .collect(Collectors.toList());
    }

    public boolean hasSingleArmy(int countryId) {
        return checkUnits(countryId) == 1;
    }

    public boolean isEnemyCountry(int countryId) {
        return board.getOccupier(countryId) == otherPlayerId;
    }

    public List<Integer> singleArmyEnemyPresent(List<Integer> adjacentCountries) {
        return adjacentCountries
                .stream()
                .filter(countryId -> hasSingleArmy(countryId) && !spreadingTooThin(countryId))
                .collect(Collectors.toList());
    }

    public boolean spreadingTooThin(int countryId) {
        return enemyCountryCount(countryId) > 2;
    }

    public boolean lessThanThreeAdjacent(int countryId) {
        return enemyCountryCount(countryId) < 3;
    }

    public boolean lessThanFourAdjacent(int countryId) {
        return enemyCountryCount(countryId) < 4;
    }

    public int enemyCountryCount(int countryId) {
        int enemyCountryCount = 0;

        int[] adjacentCountries = ADJACENT[countryId];
        for (int adjacentCountry : adjacentCountries) {
            if (board.getOccupier(adjacentCountry) == otherPlayerId) {
                enemyCountryCount++;
            }
        }

        return enemyCountryCount;
    }

    public List<Integer> countriesThatCanAttack() {
        List<Integer> ownedCountryIds = getOwnedCountryIds();

        return ownedCountryIds
                .stream()
                .filter(countryId -> {
                    boolean hasMoreThanOneUnit = board.getNumUnits(countryId) > 1;
                    boolean hasEnemyNeighbours = Arrays.stream(ADJACENT[countryId]).anyMatch(country ->
                            board.getOccupier(country) != player.getId());
                    return hasEnemyNeighbours && hasMoreThanOneUnit;
                }).collect(Collectors.toList());
    }

    public List<Integer> countriesToInvade(int attackingCountry) {
        int[] countryIds = ADJACENT[attackingCountry];
        List<Integer> countriesToInvade = new ArrayList<>();

        for (int countryId : countryIds) {
            if (board.getOccupier(countryId) != player.getId()) {
                countriesToInvade.add(countryId);
            }
        }
        return countriesToInvade;
    }

    public int compareAttackingCountries(int countryId1, int countryId2) {
        Integer countryId1Rating = rateOwnCountryForAttacking(countryId1);
        Integer countryId2Rating = rateOwnCountryForAttacking(countryId2);

        return countryId1Rating.compareTo(countryId2Rating);
    }

    public int compareDefendingCountries(int countryId1, int countryId2) {
        Integer countryId1Rating = rateEnemyDefendingCountry(countryId1);
        Integer countryId2Rating = rateEnemyDefendingCountry(countryId2);

        return countryId1Rating.compareTo(countryId2Rating);
    }

    public int commonRatings(int countryId) {
        List<Integer> almostConqueredContinents = getAlmostConqueredContinentIds(player.getId());

        // We set countries in almost conquered continents as our number one priority
        int rating = almostConqueredContinents
                .stream()
                .filter(continent -> isCountryInContinent(countryId, continent))
                .mapToInt(countryRating -> 1000)
                .sum();

        // We rate them by our most preferred Continents to take over
        List<Integer> continentRankings = ContinentRanking.getContinentIds();
        Collections.reverse(continentRankings);
        rating += IntStream
                .range(0, continentRankings.size())
                .filter(i -> isCountryInContinent(countryId, continentRankings.get(i)))
                .map(i -> (i + 1) * 100)
                .sum();

        // We rate by how many surrounding enemy countries there are
        rating += numSurroundingCountriesByPlayer(countryId, otherPlayerId) * 200;

        return rating;
    }

    public int compareRatingsForOwnReinforcement(int countryId1, int countryId2) {
        Integer country1Rating = rateOwnCountryForReinforcement(countryId1);
        Integer country2Rating = rateOwnCountryForReinforcement(countryId2);

        return country1Rating.compareTo(country2Rating);
    }


    public int rateNeutralCountry(int countryId) {
        int rating = 0;

        if (board.getNumUnits(countryId) == 1) rating += 100;

        rating -= 75 * numSurroundingCountriesByPlayer(countryId, player.getId());
        Predicate<Integer> continentContainsCountry = continent -> continentContainsCountry(continent, countryId);

        List<Integer> opponentCountries = getAlmostConqueredContinentIds(otherPlayerId);
        int opponentRating = opponentCountries
                .stream()
                .anyMatch(continentContainsCountry) ? 1000 : 50;

        rating += opponentRating * numSurroundingCountriesByPlayer(countryId, otherPlayerId);

        return -rating;
    }

    public int compareRatingsForNeutrals(int countryId1, int countryId2) {
        Integer country1Rating = rateNeutralCountry(countryId1);
        Integer country2Rating = rateNeutralCountry(countryId2);

        return country1Rating.compareTo(country2Rating);
    }

    public List<Integer> getDestinationCountryOptions(List<Integer> originCountryOptions) {
        List<Integer> destinationCountryOptions = new ArrayList<>();
        for (int countryId = 0; countryId < GameData.NUM_COUNTRIES; countryId++) {
            if (originCountryOptions.contains(countryId)) continue;

            boolean team7Occupies = board.getOccupier(countryId) == player.getId();
            boolean tooManyUnits = board.getNumUnits(countryId) >= 8;
            boolean isConnectedToOriginCountry = false;

            for (int originCountryOption : originCountryOptions) {
                boolean isConnected = board.isConnected(originCountryOption, countryId);
                boolean isSameCountry = originCountryOption == countryId;

                if (isConnected && !isSameCountry) {
                    isConnectedToOriginCountry = true;
                    break;
                }
            }

            if (team7Occupies && !tooManyUnits && isConnectedToOriginCountry)
                destinationCountryOptions.add(countryId);
        }

        return destinationCountryOptions;
    }

    private boolean isSurroundedByFriendlyCountries(int countryId) {
        for (int adjacentCountryId = 0; adjacentCountryId < GameData.NUM_COUNTRIES; adjacentCountryId++) {
            boolean isAdjacent = board.isAdjacent(countryId, adjacentCountryId);
            boolean isSameCountry = adjacentCountryId == countryId;
            if (!isAdjacent || isSameCountry) continue;

            boolean team7OccupiesAdjacent = board.getOccupier(adjacentCountryId) == player.getId();
            boolean neutralOccupiesAdjacent = board.getOccupier(adjacentCountryId) > 1;

            if (!team7OccupiesAdjacent && !neutralOccupiesAdjacent) {
                return false;
            }
        }

        return true;
    }

    public List<Integer> getInitialOriginCountryOptions() {
        List<Integer> originCountryOptions = new ArrayList<>();

        for (int countryId = 0; countryId < GameData.NUM_COUNTRIES; countryId++) {
            boolean team7Occupies = board.getOccupier(countryId) == player.getId();
            boolean enoughUnits = board.getNumUnits(countryId) >= 2;
            boolean isConnectedToAnotherOwnedCountry = false;
            boolean isSurroundedByFriendlyCountries = isSurroundedByFriendlyCountries(countryId);

            for (int connectedCountryId = 0; connectedCountryId < GameData.NUM_COUNTRIES; connectedCountryId++) {
                if (connectedCountryId == countryId) continue;

                boolean team7OccupiesConnected = board.getOccupier(connectedCountryId) == player.getId();
                boolean isConnected = board.isConnected(connectedCountryId, countryId);

                if (team7OccupiesConnected && isConnected) {
                    isConnectedToAnotherOwnedCountry = true;
                    break;
                }
            }

            if (team7Occupies && enoughUnits && isConnectedToAnotherOwnedCountry && isSurroundedByFriendlyCountries)
                originCountryOptions.add(countryId);
        }

        return originCountryOptions;
    }

    public enum ContinentRanking {
        AUSTRALIA(3),
        ASIA(2),
        SOUTH_AMERICA(4),
        AFRICA(5),
        NORTH_AMERICA(0),
        EUROPE(1);

        public int continentId;

        ContinentRanking(int continentId) {
            this.continentId = continentId;
        }

        public static List<Integer> getContinentIds() {
            return Arrays
                    .stream(ContinentRanking.values())
                    .map(continent -> continent.continentId)
                    .collect(Collectors.toList());
        }
    }
}
