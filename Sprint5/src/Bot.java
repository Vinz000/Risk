package src;

public interface Bot {

    String getName();

    String getReinforcement();

    String getPlacement(int forPlayer);

    String getCardExchange();

    String getBattle();

    String getDefence(int countryId);

    String getMoveIn(int attackCountryId);

    String getFortify();

}
