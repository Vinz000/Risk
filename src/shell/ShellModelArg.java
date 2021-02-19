package shell;

public class ShellModelArg {
    public final Object arg;
    public final ShellModelUpdateType updateType;

    public ShellModelArg(Object arg, ShellModelUpdateType updateType) {
        this.arg = arg;
        this.updateType = updateType;
    }
}
