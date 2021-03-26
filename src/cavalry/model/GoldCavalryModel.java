package cavalry.model;

import java.util.Observable;

import static common.Constants.*;

public class GoldCavalryModel extends Observable {
    private static GoldCavalryModel instance;
    private int bonus = STARTING_CAVALRY_BONUS;

    private GoldCavalryModel() {
    }

    public static synchronized GoldCavalryModel getInstance() {
        if (instance == null) {
            return instance = new GoldCavalryModel();
        }

        return instance;
    }

    public void showGoldCavalry() {
        GoldCavalryModelArg goldCavalryModelArg = new GoldCavalryModelArg(GoldCavalryModelUpdateType.VISIBLE);
        setChanged();
        notifyObservers(goldCavalryModelArg);
    }

    private void updateBonusCount(int bonusCount) {
        GoldCavalryModelArg goldCavalryModelArg = new GoldCavalryModelArg(bonusCount, GoldCavalryModelUpdateType.BONUS);
        setChanged();
        notifyObservers(goldCavalryModelArg);
    }

    private void updateBonus() {

        if (bonus < 60) {
            int bonusIncrement = bonus < STARTING_BONUS_LIMIT ?
                    INIT_CAVALRY_BONUS_INCREMENT :
                    CAVALRY_BONUS_INCREMENT;

            bonus += bonusIncrement;

            updateBonusCount(bonus);
        }
    }

    public int getBonus() {
        int currentBonus = bonus;
        updateBonus();
        return currentBonus;
    }
}
