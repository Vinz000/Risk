package game.module;

import cavalry.model.GoldCavalryModel;
import common.validation.Validators;
import deck.Card;
import player.Player;
import player.model.PlayerModel;
import shell.model.ShellModel;

import java.util.List;

public class CardUsage extends Module {
    String response;
    ShellModel shellModel = ShellModel.getInstance();
    Player currentPlayer = PlayerModel.getInstance().getCurrentPlayer();
    List<Card> cardsOwned = currentPlayer.getCards();

    public CardUsage() {
    }

    public void displayCardsOwned() {

        boolean moreThanFourCards = cardsOwned.size() >= 5;
        String initialMessageStart = currentPlayer.getName() + " you have " + cardsOwned.size() + " cards";
        String initialMessageEnd = moreThanFourCards ?
                ", so you must spend 3." :
                " left.";
        shellModel.notify(initialMessageStart + initialMessageEnd);
        currentPlayer.displayCards();
    }

    public void chooseToSpendCards() {
        shellModel.notify("Would you like to spend cards? Y/N");
        response = shellModel.prompt(Validators.yesNo);
        if (response.toLowerCase().contains("y")) {
            selectCards();
        }
    }

    public void selectCards() {
        shellModel.notify("Please type in the cards you want to spend:");
        response = shellModel.prompt(Validators.cardChoiceCheck);

        List<Card> cardsToRemove = Validators.parseInsignias(response);
        currentPlayer.removeAllCards(cardsToRemove);
        addTroops(currentPlayer);
    }

    public void addTroops(Player currentPlayer) {
        GoldCavalryModel goldCavalryModel = GoldCavalryModel.getInstance();
        int goldenCalvaryReinforcements = goldCavalryModel.getAndIncrementBonus();

        int currentReinforcements = currentPlayer.getReinforcements();

        currentPlayer.setReinforcements(currentReinforcements + goldenCalvaryReinforcements);
    }
}
