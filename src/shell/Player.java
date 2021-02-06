package shell;

public class Player {
    private static int numPlayer = 1;
    private final String name;
    private final String playerID;

    public Player(String name) {
        this.name = name;
        playerID = "player-" + numPlayer;
        numPlayer++;
    }

    public String getPlayerID() {
        return playerID;
    }


}
