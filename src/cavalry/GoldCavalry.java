package cavalry;

import static common.Constants.*;
import static common.Constants.CAVALRY_BONUS;

public class GoldCavalry {
    private int bonus = INIT_CAVALRY_BONUS;

    public GoldCavalry() {

    }

    public int getBonus() {
        return bonus;
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }
}
