package shell;

import java.util.Observable;

public class ShellModel extends Observable {

    public ShellModel() {
    }

    private String player1 = null;
    private String player2 = null;

    public void setPlayer1(String player1) {
        this.player1 = player1;
        setChanged();
        notifyObservers(player1);
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
        setChanged();
        notifyObservers(player2);
    }

    public String getPlayer1() {
        return player1;
    }

    public String getPlayer2() {
        return player2;
    }

    @Override
    public String toString() {
        return String.format("%s %s", player1, player2);
    }
}
