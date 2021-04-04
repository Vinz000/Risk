package player;

import game.module.CardUsage;
import game.module.Combat;
import game.module.Fortification;
import game.module.Reinforcing;
import javafx.scene.paint.Color;
import map.country.Country;
import map.model.MapModel;
import player.model.PlayerModel;
import shell.model.ShellModel;

import java.util.Optional;
import java.util.UUID;

import static common.Constants.INIT_HUMAN_PLAYER_REINFORCEMENTS;

public class HumanPlayer extends Player {
    private final String id;

    public HumanPlayer(String name, Color color) {
        super(name, color);
        this.id = UUID.randomUUID().toString();
    }

    @Override
    public void startTurn() {
        ShellModel shellModel = ShellModel.getInstance();
        shellModel.notify("Your turn " + getName());
    }

    @Override
    public void startReinforcement() {
        startTurn();
    }

    @Override
    public void initReinforce() {
        Reinforcing reinforcing = new Reinforcing();
        reinforcing.reinforceInitialCountries(this, INIT_HUMAN_PLAYER_REINFORCEMENTS);
    }

    @Override
    public void reinforce() {
        PlayerModel playerModel = PlayerModel.getInstance();
        playerModel.calculateReinforcements(this);

        Reinforcing reinforcing = new Reinforcing();
        reinforcing.reinforceOwnedCountries(this);
    }

    @Override
    public boolean combat() {
        MapModel mapModel = MapModel.getInstance();
        PlayerModel playerModel = PlayerModel.getInstance();
        Combat combat = new Combat();

        while (playerModel.currentPlayerCanAttack() && !combat.skipCombat(this)) {
            combat.selectAttackingCountry(this);
            combat.selectDefendingCountry(this);

            Country attackingCountry = mapModel.getAttackingCountry();
            Country defendingCountry = mapModel.getDefendingCountry();

            do {
                combat.setAttackingTroops(attackingCountry);
                combat.setDefendingTroops(defendingCountry);
                if (combat.initiateCombat(attackingCountry, defendingCountry)) return true;

                if (attackingCountry.getArmyCount() == 1) break;
            } while (!defendingCountry.getOccupier().equals(this)
                    && !combat.stopCombat(this));

            mapModel.clearCombatants();
        }

        return false;
    }

    @Override
    public boolean cardUsage() {
        CardUsage cardUsage = new CardUsage();
        cardUsage.displayCardsOwned();
        boolean moreThanFourCards = getCards().size() >= 5;

        if (moreThanFourCards) {
            cardUsage.selectCards();
        } else {
            return cardUsage.chooseToSpendCards();
        }

        return true;
    }

    @Override
    public void fortify() {
        Fortification fortification = new Fortification();
        boolean currentPlayerWantsToFortify = fortification.prompt();

        if (currentPlayerWantsToFortify) {
            Optional<Country> originCountry = fortification.selectOriginCountry();

            if (originCountry.isPresent()) {
                int troopsCount = fortification.selectNumberOfTroops(originCountry.get());
                Optional<Country> destinationCountry = fortification.selectDestinationCountry(originCountry.get());
                destinationCountry.ifPresent(country -> fortification.moveTroops(originCountry.get(), country, troopsCount));
            }
        }

    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof HumanPlayer && ((HumanPlayer) obj).getId().equals(id);
    }
}
