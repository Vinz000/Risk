package game.module;

import common.validation.Validators;
import deck.Card;
import deck.CardType;
import deck.Deck;
import map.country.Country;
import player.Player;
import shell.model.ShellModel;

import java.util.*;

public class CardUsage extends Module {
    String response;
    ShellModel shellModel = ShellModel.getInstance();
    static Deck deck = Deck.getInstance();
    List<Card> artillery = new ArrayList<>();
    List<Card> calvary = new ArrayList<>();
    List<Card> soldier = new ArrayList<>();
    List<Card> wildcard = new ArrayList<>();


    public CardUsage() {
    }

    public static void addCard(Player player) {
        deck.shuffle();

        Optional<Card> nullableCard = deck.drawCard();
        nullableCard.ifPresent(player::addCard);
    }

    public void seeOwnedCards(Player currentPlayer) {

        List<Card> cardsOwned = currentPlayer.getCards();
        separateCards(cardsOwned);

        if (cardsOwned.size() >= 5) {
            shellModel.notify("You have " + cardsOwned.size() + " cards so you must spend 3.");
            shellModel.notify(artillery.size() + ": artillery cards.");
            shellModel.notify(calvary.size() + ": calvary cards.");
            shellModel.notify(soldier.size() + ": soldier cards.");
            shellModel.notify(wildcard.size() + ": wild cards.");
            selectCards(currentPlayer);
        } else {
            shellModel.notify(currentPlayer.getName() + " you have " + cardsOwned.size() + " cards left.");
            shellModel.notify(artillery.size() + ": artillery cards.");
            shellModel.notify(calvary.size() + ": calvary cards.");
            shellModel.notify(soldier.size() + ": soldier cards.");
            shellModel.notify(wildcard.size() + ": wild cards.");
            shellModel.notify("Would you like to spend cards? Y/N");

            response = shellModel.prompt(Validators.validUseOfCards);

            if (response.toLowerCase().contains("y")) {
                selectCards(currentPlayer);
            }
        }
    }

    public void selectCards(Player currentPlayer) {
        shellModel.notify("Please choose what card selection you wish to spend: " +
                "\nArtillery \nCalvary \nSoldier \nMixed");

        response = shellModel.prompt(Validators.cardChoiceCheck);

        int cardsToRemove = 3;

        if (response.toLowerCase().contains("ar")) {
            for (int i = 0; i < cardsToRemove; i++) {
                currentPlayer.getCards().remove(artillery.get(i));
            }
        } else if (response.toLowerCase().contains("c")) {
            for (int i = 0; i < cardsToRemove; i++) {
                currentPlayer.getCards().remove(calvary.get(i));
            }
        } else if (response.toLowerCase().contains("s")) {
            for (int i = 0; i < cardsToRemove; i++) {
                currentPlayer.getCards().remove(soldier.get(i));
            }
        } else {
            if (wildcard.size() == 1) {
                cardRemoval(currentPlayer.getCards(), wildcard, 0);
                if (artillery.size() > 1) {
                    cardRemoval(currentPlayer.getCards(), artillery, 2);
                } else if (calvary.size() > 1) {
                    cardRemoval(currentPlayer.getCards(), calvary, 2);
                } else if (soldier.size() > 1) {
                    cardRemoval(currentPlayer.getCards(), soldier, 2);
                } else {
                    currentPlayer.getCards().remove(0);
                    currentPlayer.getCards().remove(1);
                }
            } else if (wildcard.size() == 2) {
                cardRemoval(currentPlayer.getCards(), wildcard, 2);
                currentPlayer.getCards().remove(0);
            } else {
                if (artillery.size() <= 1 && calvary.size() <= 1 && soldier.size() <= 1) {
                    cardRemoval(currentPlayer.getCards(), artillery, 0);
                    cardRemoval(currentPlayer.getCards(), calvary, 0);
                    cardRemoval(currentPlayer.getCards(), soldier, 0);
                }
            }
        }

        addTroops(currentPlayer);

        artillery.clear();
        calvary.clear();
        soldier.clear();
        wildcard.clear();
    }

    public void cardRemoval(List<Card> cardsOwned, List<Card> card, int index) {

        if (index == 0) {
            cardsOwned.remove(card.get(index));
        } else {
            for (int i = 0; i < index; i++) {
                cardsOwned.remove(card.get(i));
            }
        }
    }

    public void separateCards(List<Card> cardsOwned) {
        for (Card card : cardsOwned) {
            if (card.getType() == CardType.ARTILLERY) {
                artillery.add(card);
            } else if (card.getType() == CardType.CALVARY) {
                calvary.add(card);
            } else if (card.getType() == CardType.SOLDIER) {
                soldier.add(card);
            } else {
                wildcard.add(card);
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
