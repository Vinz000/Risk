package cavalry.model;

public class GoldCavalryModelArg {
    public final Object arg;
    public final GoldCavalryModelUpdateType updateType;

    public GoldCavalryModelArg(Object arg, GoldCavalryModelUpdateType updateType) {
        this.arg = arg;
        this.updateType = updateType;
    }
}
