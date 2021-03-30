package game.module;

import cavalry.model.GoldCavalryModel;
import common.validation.Validators;
import deck.Card;
import deck.CardSet;
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
                "\nArtillery \nCalvary \nSoldier \nMixed \nWild");
        response = shellModel.prompt(Validators.cardChoiceCheck);
        Optional<CardSet> nullableCardSet = CardSet.fromString(response);
        if (nullableCardSet.isPresent()) {
            CardSet cardSet = nullableCardSet.get();
            removeCardSet(cardSet);
            addTroops(currentPlayer);
        }
    }

    public void removeCardSet(CardSet cardSet) {
        switch (cardSet) {
            case THREE_ARTILLERY:
                removeThreeCards(CardType.ARTILLERY);
                break;
            case THREE_CALVARY:
                removeThreeCards(CardType.CALVARY);
                break;
            case THREE_SOLDIER:
                removeThreeCards(CardType.SOLDIER);
                break;
            case ONE_OF_EACH:
                removeOneOfEach();
                break;
            case ONE_WILDCARD:
                removeOneWildCard();
                break;
            case TWO_WILDCARDS:
                removeTwoWildCards();
                break;
        }
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

    public void removeOneOfEach() {
            removeOneCard(CardType.ARTILLERY);
            removeOneCard(CardType.CALVARY);
            removeOneCard(CardType.SOLDIER);
    }

    public void removeOneWildCard() {
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
    }

    public void removeTwoWildCards() {
        removeTwoCards(CardType.WILDCARD);
        currentPlayer.getCards().remove(0);
    }

    public void addTroops(Player currentPlayer) {
        GoldCavalryModel goldCavalryModel = GoldCavalryModel.getInstance();
        int goldenCalvaryReinforcements = goldCavalryModel.getAndIncrementBonus();
        mapModel.highlightCountries(currentPlayer.getOwnedCountries());

        ///TODO: VINCENT PLEASE MAKE THIS GENERIC
        while (goldenCalvaryReinforcements > 0) {
            shellModel.notify(String.format("You have %d reinforcements to place.", goldenCalvaryReinforcements));
            shellModel.notify("Choose country to reinforce");

            response = shellModel.prompt(Validators.validReinforcingCountry);
            Optional<Country> chosenCountry = mapModel.getCountryByName(response);

            shellModel.notify("How many units would you like to reinforce with?");

            response = shellModel.prompt(Validators.alwaysValid);
            int numReinforce = Integer.parseInt(response);

            chosenCountry.ifPresent(validCountry -> mapModel.updateCountryArmyCount(validCountry, numReinforce));

            shellModel.notify("Successfully placed reinforcements.");

            goldenCalvaryReinforcements -= numReinforce;
        }
        mapModel.highlightCountries(currentPlayer.getOwnedCountries());
    }

}
