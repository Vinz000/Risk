package map.model;

public class MapModelArg {
    public final Object arg;
    public final MapModelUpdateType updateType;

    public MapModelArg(Object arg, MapModelUpdateType updateType) {
        this.arg = arg;
        this.updateType = updateType;
    }
}
