package src;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Team7Test {

    private static class TestBoardAPI implements BoardAPI {

        @Override
        public boolean isAdjacent(int fromCountry, int toCountry) {
            return new Board().isAdjacent(fromCountry, toCountry);
        }

        @Override
        public boolean isConnected(int fromCountry, int toCountry) {
            return fromCountry + toCountry % 2 == 0;
        }

        @Override
        public boolean isOccupied(int country) {
            return country % 2 == 0;
        }

        @Override
        public boolean isInvasionSuccess() {
            return true;
        }

        @Override
        public boolean isEliminated(int playerId) {
            return false;
        }

        @Override
        public int getOccupier(int countryId) {
            return countryId % GameData.NUM_PLAYERS_PLUS_NEUTRALS;
        }

        @Override
        public int getNumUnits(int countryId) {
            return countryId;
        }
    }

    private static class TestPlayerAPI implements PlayerAPI {

        @Override
        public boolean isCardsAvailable(int[] cardInsigniaIds) {
            return false;
        }

        @Override
        public boolean isForcedExchange() {
            return false;
        }

        @Override
        public boolean isOptionalExchange() {
            return false;
        }

        @Override
        public int getId() {
            return 0;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public int getNumUnits() {
            return 0;
        }

        @Override
        public ArrayList<Integer> getDice() {
            return null;
        }

        @Override
        public int getDie(int dieId) {
            return 0;
        }

        @Override
        public int getBattleLoss() {
            return 0;
        }

        @Override
        public ArrayList<Card> getCards() {
            return null;
        }
    }

    private final BoardAPI board = new TestBoardAPI();
    private final PlayerAPI player = new TestPlayerAPI();
    Team7HelperFunctions helperFunctions;

    @BeforeEach
    void setUp() {
        helperFunctions = new Team7HelperFunctions(board, player, 0);
    }

    @Test
    void testGetDestinationCountryOptions() {
        List<Integer> originCountryOptions = IntStream
                .range(0, GameData.NUM_COUNTRIES)
                .boxed()
                .collect(Collectors.toList());
        List<Integer> destinationCountryOptions = helperFunctions.getDestinationCountryOptions(originCountryOptions);
        assertEquals(new ArrayList<Integer>(), destinationCountryOptions);
    }

    @Test
    void testIsSurroundedByFriendlyCountries() {
        boolean[] expected = new boolean[]{
                false, true, true, true,
                false, false, false, true,
                true, false, false, false,
                false, true, true, false,
                true, true, true, true,
                false, false, false, false,
                false, true, false, false,
                true, false, false, true,
                false, true, false, true,
                false, true, true, false,
                false, true
        };

        for (int i = 0; i < GameData.NUM_COUNTRIES; i++) {
            boolean result = helperFunctions.isSurroundedByFriendlyCountries(i);
            assertEquals(expected[i], result);
        }
    }

    @Test
    void testGetInitialOriginCountryOptions() {
        List<Integer> originCountryOptions = helperFunctions.getInitialOriginCountryOptions();
        assertEquals(1, originCountryOptions.size());
        assertEquals(18, originCountryOptions.get(0));
    }
}
