package cavalry.model;

import cavalry.GoldCavalry;

import java.util.Observable;

import static common.Constants.*;

public class GoldCavalryModel extends Observable {
    private static GoldCavalryModel instance;
    private final GoldCavalry goldCavalry = new GoldCavalry();

    private GoldCavalryModel() {
    }

    public static synchronized GoldCavalryModel getInstance() {
        if (instance == null) {
            return instance = new GoldCavalryModel();
        }

        return instance;
    }

    public void showGoldCavalry() {
        GoldCavalryModelArg goldCavalryModelArg = new GoldCavalryModelArg(null, GoldCavalryModelUpdateType.VISIBLE);
        setChanged();
        notifyObservers(goldCavalryModelArg);
    }

    private void updateBonusCount(int bonusCount) {
        GoldCavalryModelArg goldCavalryModelArg = new GoldCavalryModelArg(bonusCount, GoldCavalryModelUpdateType.BONUS);
        setChanged();
        notifyObservers(goldCavalryModelArg);
    }

    private void updateBonus() {
        int bonus = goldCavalry.getBonus();

        if (bonus < 60) {
            int bonusIncrement = bonus < STARTING_BONUS_LIMIT ? INIT_CAVALRY_BONUS : CAVALRY_BONUS;

            bonus += bonusIncrement;

            updateBonusCount(bonus);
            goldCavalry.setBonus(bonus);
        }
    }

    public int getBonus() {
        int currentBonus = goldCavalry.getBonus();
        updateBonus();
        return currentBonus;
    }
}
