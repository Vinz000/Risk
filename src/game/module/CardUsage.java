package game.module;

import common.validation.Validators;
import deck.Card;
import deck.CardType;
import map.country.Country;
import player.Player;
import player.model.PlayerModel;
import shell.model.ShellModel;

import java.util.*;

public class CardUsage extends Module {
    String response;
    ShellModel shellModel = ShellModel.getInstance();
    Player currentPlayer = PlayerModel.getInstance().getCurrentPlayer();
    List<Card> cardsOwned = currentPlayer.getCards();
    List<Card> artillery = currentPlayer.getArtilleryCards();
    List<Card> calvary = currentPlayer.getCalvaryCards();
    List<Card> soldier = currentPlayer.getSoldierCards();
    List<Card> wildcard = currentPlayer.getWildCards();


    public CardUsage() {
    }

    public void displayCardsOwned() {

        if (cardsOwned.size() >= 5) {
            shellModel.notify("You have " + cardsOwned.size() + " cards so you must spend 3.");
            shellModel.notify(artillery.size() + ": artillery cards.");
            shellModel.notify(calvary.size() + ": calvary cards.");
            shellModel.notify(soldier.size() + ": soldier cards.");
            shellModel.notify(wildcard.size() + ": wild cards.");
            selectCards();
        } else {
            shellModel.notify(currentPlayer.getName() + " you have " + cardsOwned.size() + " cards left.");
            shellModel.notify(artillery.size() + ": artillery cards.");
            shellModel.notify(calvary.size() + ": calvary cards.");
            shellModel.notify(soldier.size() + ": soldier cards.");
            shellModel.notify(wildcard.size() + ": wild cards.");
            shellModel.notify("Would you like to spend cards? Y/N");
            chooseToSpendCards();
        }
    }

    public void chooseToSpendCards() {
        response = shellModel.prompt(Validators.validUseOfCards);

        if (response.toLowerCase().contains("y")) {
            selectCards();
        }
    }

    public void selectCards() {
        shellModel.notify("Please choose what card selection you wish to spend: " +
                "\nArtillery \nCalvary \nSoldier \nMixed");

        response = shellModel.prompt(Validators.cardChoiceCheck);

        CardType cardType = CardType.valueOf(response.toLowerCase());

        switch (cardType) {
            case ARTILLERY:
                removeThreeCards(CardType.ARTILLERY);
                break;
            case CALVARY:
                removeThreeCards(CardType.CALVARY);
                break;
            case SOLDIER:
                removeThreeCards(CardType.SOLDIER);
                break;
            default:
                mixedSpending();
                break;
        }

        addTroops(currentPlayer);
    }

    public void removeOneCard(CardType cardType) {
        currentPlayer.removeCardsOfType(cardType, 1);
    }

    public void removeTwoCards(CardType cardType) {
        currentPlayer.removeCardsOfType(cardType, 2);
    }

    public void removeThreeCards(CardType cardType) {
        currentPlayer.removeCardsOfType(cardType, 3);
    }

    public void mixedSpending() {
        if (currentPlayer.oneWildCard()) {
            removeOneCard(CardType.WILDCARD);

            for (CardType mixedCardType : CardType.values()) {
                List<Card> cards = currentPlayer.getCardsOfType(mixedCardType);
                if (cards.size() > 1) {
                    removeTwoCards(mixedCardType);
                    break;
                } else {
                    currentPlayer.getCards().remove(0);
                    currentPlayer.getCards().remove(1);
                }
            }
        } else if (currentPlayer.twoWildCards()) {
            removeTwoCards(CardType.WILDCARD);
            currentPlayer.getCards().remove(0);
        } else {
            if (currentPlayer.atLeastOneOfEach()) {
                removeOneCard(CardType.ARTILLERY);
                removeOneCard(CardType.CALVARY);
                removeOneCard(CardType.SOLDIER);
            }
        }
    }

    ///TODO:Change to work with golden calvary
    public void addTroops(Player currentPlayer) {
        int goldenCalvaryReinforcements = 2;
        mapModel.highlightCountries(currentPlayer.getOwnedCountries());

        while (goldenCalvaryReinforcements > 0) {
            shellModel.notify(String.format("You have %d reinforcements to place.", goldenCalvaryReinforcements));
            shellModel.notify("Choose country to reinforce");

            response = shellModel.prompt(Validators.validReinforcingCountry);
            Optional<Country> chosenCountry = mapModel.getCountryByName(response);

            shellModel.notify("How many units would you like to reinforce with?");

            ///TODO: Change validator for golden calvary
            response = shellModel.prompt(Validators.alwaysValid);
            int numReinforce = Integer.parseInt(response);

            chosenCountry.ifPresent(validCountry -> mapModel.updateCountryArmyCount(validCountry, numReinforce));

            shellModel.notify("Successfully placed reinforcements.");

            goldenCalvaryReinforcements -= numReinforce;
        }
        mapModel.highlightCountries(currentPlayer.getOwnedCountries());
    }

}
