package src;

public interface BoardAPI {

    boolean isAdjacent(int fromCountry, int toCountry);

    boolean isConnected(int fromCountry, int toCountry);

    boolean isOccupied(int country);

    boolean isInvasionSuccess();

    boolean isEliminated(int playerId);

    int getOccupier(int countryId);

    int getNumUnits(int countryId);

}
