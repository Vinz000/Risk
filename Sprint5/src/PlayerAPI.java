package src;

import java.util.ArrayList;

public interface PlayerAPI {

    boolean isCardsAvailable(int[] cardInsigniaIds);

    boolean isForcedExchange();

    boolean isOptionalExchange();

    int getId();

    String getName();

    int getNumUnits();

    ArrayList<Integer> getDice();

    int getDie(int dieId);

    int getBattleLoss();

    ArrayList<Card> getCards();

}
