package player.model;

public class PlayerModelArg {
    public final Object arg;
    public final PlayerModelUpdateType updateType;

    public PlayerModelArg(Object arg, PlayerModelUpdateType updateType) {
        this.arg = arg;
        this.updateType = updateType;
    }
}