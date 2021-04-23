package src;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.*;
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

    private final List<Integer> currentOccupiers = new ArrayList<>();
    private int estimatedGoldenCavalrySize = 2;
    private int numCardsOtherPlayerHas = 0;

    Team7HelperFunctions(BoardAPI inBoard, PlayerAPI inPlayer, int otherPlayerId) {
        this.board = inBoard;
        this.player = inPlayer;
        this.otherPlayerId = otherPlayerId;
    }

    public String convertToCommand(List<Integer> inputList) {
        StringBuilder command = new StringBuilder();
        for (int i = 0; i < 2; i++) {
            command.append(GameData.COUNTRY_NAMES[inputList.get(i)]);
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
            boolean hasOneUnit = board.getNumUnits(adjacentCountryId) == 1;
            boolean notPlayer = board.getOccupier(adjacentCountryId) != player.getId();
            boolean isEnemyPlayer = board.getOccupier(adjacentCountryId) == otherPlayerId;
            if (hasOneUnit && notPlayer && isEnemyPlayer) {
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

    public int lowestArmyCount(int[] countryIds) {
        int lowestCountryId = 0;
        for (int countryId : countryIds) {
            int numUnits = board.getNumUnits(countryId);
            int lowestNumUnits = board.getNumUnits(lowestCountryId);
            if (lowestNumUnits > numUnits) {
                lowestCountryId = countryId;
            }
        }
        return lowestCountryId;
    }

    public int leastEnemyAndNeutralAdjacentCountries(List<Integer> ownedCountriesIds) {
        int countryId = -1;
        int leastEnemyAdjacentCount = Integer.MAX_VALUE;

        for (Integer ownedCountriesId : ownedCountriesIds) {
            int enemyCount = totalEnemyCountryCount(GameData.ADJACENT[ownedCountriesId]);
            if (enemyCount < leastEnemyAdjacentCount) {
                countryId = ownedCountriesId;
                leastEnemyAdjacentCount = enemyCount;
            }
        }
        return countryId;
    }

    public int leastEnemyAdjacentCountries(List<Integer> ownedCountriesIds) {
        int countryId = 0;
        int leastEnemyAdjacentCount = Integer.MAX_VALUE;

        for (Integer ownedCountriesId : ownedCountriesIds) {
            if (enemyCountryPresent(GameData.ADJACENT[ownedCountriesId])) {
                int enemyCount = enemyPlayerCount(GameData.ADJACENT[ownedCountriesId]);
                if (enemyCount < leastEnemyAdjacentCount) {
                    countryId = ownedCountriesId;
                    leastEnemyAdjacentCount = enemyCount;
                }
            }
        }
        return countryId;
    }

    public boolean enemyCountryPresent(int[] ownedCountriesIds) {
        int enemyCount = 0;
        for (Integer ownedCountriesId : ownedCountriesIds) {
            if (board.getOccupier(ownedCountriesId) == otherPlayerId) {
                enemyCount = enemyPlayerCount(GameData.ADJACENT[ownedCountriesId]);
            }
        }
        return enemyCount > 0;
    }

    public int leastEnemyAdjacentCountries(int[] continent) {
        List<Integer> ownedCountriesIds = Arrays.stream(continent).boxed().collect(Collectors.toList());
        return leastEnemyAdjacentCountries(ownedCountriesIds);
    }

    public int leastEnemyAndNeutralAdjacentCountries(int[] continent) {
        List<Integer> ownedCountriesId = Arrays.stream(continent).boxed().collect(Collectors.toList());
        return leastEnemyAndNeutralAdjacentCountries(ownedCountriesId);
    }

    public int totalEnemyCountryCount(int[] countryIds) {
        IntPredicate isEnemyCountryId = countryId -> board.getOccupier(countryId) != player.getId();
        return (int) Arrays.stream(countryIds).filter(isEnemyCountryId).count();
    }

    public int enemyPlayerCount(int[] countryIds) {
        IntPredicate isEnemyCountryId = countryId -> board.getOccupier(countryId) != player.getId() &&
                board.getOccupier(countryId) == otherPlayerId;
        return (int) Arrays.stream(countryIds).filter(isEnemyCountryId).count();
    }

    public List<Integer> continentSpecificAttack(List<Integer> ownedCountriesIds) {
        int chosenContinent = chooseContinentToAttack();
        int chosenAttackingCountry;
        int chosenDefendingCountry;
        List<Integer> combatInput = new ArrayList<>();

        if (chosenContinent == -1) {
            chosenAttackingCountry = leastEnemyAdjacentCountries(ownedCountriesIds);
        } else {
            int[] continent = CONTINENT_COUNTRIES[chosenContinent];
            chosenAttackingCountry = leastEnemyAdjacentCountries(continent);
            if (chosenAttackingCountry == -1) {
                chosenAttackingCountry = leastEnemyAndNeutralAdjacentCountries(continent);
            }
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
        for (ContinentRanking continent : ContinentRanking.values()) {
            if (ownsEnoughOfContinent(CONTINENT_COUNTRIES[continent.value])) {
                return continent.value;
            }
        }
        return -1;
    }

    public boolean ownsEnoughOfContinent(int[] continent) {
        int count = 0;
        for (int i = 0; i < continent.length; i++) {
            if (board.getOccupier(i) == player.getId()) {
                count++;
            }
        }
        return count >= continent.length * .1;
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
        String path = "https://drive.google.com/uc?export=download&id=1ct7MUbOHmYv-N_GlNFfIvwsNtUxwj6ac";
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
            Thread.sleep(300);
        } catch (InterruptedException ignored) {
        }
    }

    public String getValidCardCombination() {
        List<Integer> insigniaIds = getInsigniaIds();

        return IntStream.range(0, insigniaIds.size() - 3)
                .mapToObj(i -> new int[]{insigniaIds.get(i), insigniaIds.get(i + 1), insigniaIds.get(i + 3)})
                .filter(Deck::isASet)
                .findFirst()
                .map(this::insigniaIdsToStringCommand)
                .orElse("skip");
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
            int continentId = continent.value;
            int[] countriesInContinent = CONTINENT_COUNTRIES[continentId];

            IntPredicate botOccupiesCountry = country -> board.getOccupier(country) == playerId;

            int ownedCountriesInContinent = (int) Arrays
                    .stream(countriesInContinent)
                    .filter(botOccupiesCountry)
                    .count();

            if (ownedCountriesInContinent > countriesInContinent.length * 0.3) {
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

    private List<Integer> orderMostImportantCountriesInContinent(int continent) {

        List<Integer> countriesInContinent = Arrays
                .stream(CONTINENT_COUNTRIES[continent])
                .boxed()
                .collect(Collectors.toList());
        Comparator<Integer> howManySurroundingEnemies = this::compareSurroundingEnemies;
        countriesInContinent.sort(howManySurroundingEnemies);

        return countriesInContinent;
    }

    private int compareSurroundingEnemies(Integer country1, Integer country2) {
        Integer country1SurroundingEnemies = numSurroundingCountriesByPlayer(country1, otherPlayerId);
        Integer country2SurroundingEnemies = numSurroundingCountriesByPlayer(country2, otherPlayerId);

        return country1SurroundingEnemies.compareTo(country2SurroundingEnemies);
    }

    public List<Integer> countriesInOrderOfImportance(List<Integer> continents) {
        List<Integer> orderedCountries = new ArrayList<>();

        continents
                .stream()
                .map(this::orderMostImportantCountriesInContinent)
                .forEachOrdered(orderedCountries::addAll);

        return orderedCountries;
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

    /**
     * With this function we can also see what continent/country the other bot is prioritising
     * We can maybe work with this
     */
    public void updateCurrentOccupiers() {
        currentOccupiers.clear();
        for (int i = 0; i < NUM_COUNTRIES; i++) {
            int occupier = board.getOccupier(i);
            currentOccupiers.add(occupier);
        }
    }

    private int getNumOfCountriesEnemyHasTakenOver() {
        IntPredicate hasOccupierChanged = country -> board.getOccupier(country) != currentOccupiers.get(country);

        return (int) IntStream
                .range(0, NUM_COUNTRIES)
                .filter(hasOccupierChanged)
                .count();
    }

    private void increaseGoldenCavalrySize() {
        if (estimatedGoldenCavalrySize < 60) {
            int cavalryIncrease = estimatedGoldenCavalrySize >= 10 ? 5 : 2;

            estimatedGoldenCavalrySize += cavalryIncrease;
        }
    }

    public boolean isGoldenCavalryAtleast10() {
        return estimatedGoldenCavalrySize >= 10;
    }

    public void updateEstimatedGoldenCavalrySize() {
        if (numCardsOtherPlayerHas > 5) {
            increaseGoldenCavalrySize();
            numCardsOtherPlayerHas -= 3;
        }

        numCardsOtherPlayerHas += getNumOfCountriesEnemyHasTakenOver();
    }

    public int rateCountry(int countryId) {
        int rating = 0;

        rating -= 75 * numSurroundingCountriesByPlayer(countryId, player.getId());

        Predicate<Integer> continentContainsCountry = continent -> continentContainsCountry(continent, countryId);

        List<Integer> opponentCountries = getAlmostConqueredContinentIds(otherPlayerId);
        int opponentRating = opponentCountries
                .stream()
                .anyMatch(continentContainsCountry) ? 1000 : 50;

        rating += opponentRating * numSurroundingCountriesByPlayer(countryId, otherPlayerId);

        return rating;
    }

    public int compareRatings(int countryId1, int countryId2) {

        Integer country1Rating = rateCountry(countryId1);
        Integer country2Rating = rateCountry(countryId2);

        return country1Rating.compareTo(country2Rating);
    }

    public List<Integer> getDestinationCountryOptions(List<Integer> originCountryOptions) {
        List<Integer> destinationCountryOptions = new ArrayList<>();
        for (int countryId = 0; countryId < GameData.NUM_COUNTRIES; countryId++) {
            if (originCountryOptions.contains(countryId)) continue;

            boolean team7Occupies = board.getOccupier(countryId) == player.getId();
            boolean tooManyUnits = board.getNumUnits(countryId) > 8;
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

        public int value;

        ContinentRanking(int value) {
            this.value = value;
        }

        public static List<Integer> getValues() {
            return Arrays
                    .stream(ContinentRanking.values())
                    .map(continent -> continent.value)
                    .collect(Collectors.toList());
        }
    }
}
