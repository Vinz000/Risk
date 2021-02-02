package game;

public enum GameState {
    NEW,
    PLAYER_ONE_NAME,
    PLAYER_TWO_NAME;

    public GameState next() {
        GameState[] values = values();
        return values[(this.ordinal() + 1) % values.length];
    }
}
