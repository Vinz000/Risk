package player;

import deck.Card;
import deck.CardType;
import javafx.scene.paint.Color;
import map.country.Country;
import shell.model.ShellModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class Player {
    private final String id;
    private final String name;
    private final Color color;
    private final List<Country> ownedCountries = new ArrayList<>();
    private final List<Card> cards = new ArrayList<>();
    private int reinforcements = 0;

    public Player(String name, Color color) throws IllegalArgumentException {
        assert !name.trim().isEmpty() : "Name cannot be empty";

        this.id = UUID.randomUUID().toString();
        this.name = Objects.requireNonNull(name);
        this.color = Objects.requireNonNull(color);
    }

    public void removeCountry(Country country) {
        ownedCountries.remove(country);
    }

    public int getReinforcements() {
        return reinforcements;
    }

    public void setReinforcements(int setReinforcements) {
        assert setReinforcements < 0 : "Reinforcement cannot be less than 0";
        reinforcements = setReinforcements;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public void addCountry(Country country) {
        Objects.requireNonNull(country);

        ownedCountries.add(country);
    }

    public List<Country> getOwnedCountries() {
        return ownedCountries;
    }

    public abstract void startTurn();

    public abstract void startReinforcement();

    public abstract void initReinforce();

    public abstract void reinforce();

    public abstract boolean combat();

    public abstract boolean cardUsage();

    public abstract void fortify();

    public List<Card> getCards() {
        return cards;
    }

    public Card getMostRecentCard() {
        return cards.get(cards.size() - 1);
    }

    public void addCard(Card card) {
        Objects.requireNonNull(card);

        cards.add(card);
    }

    public void removeAllCards(List<Card> cardsToRemove) {
        cardsToRemove.forEach(cards::remove);
    }

    public void removeCard(Card card) throws IllegalArgumentException {
        assert getCards().size() > 0 : "Cannot remove from empty hand";

        cards.remove(card);
    }

    public List<Card> getCardsOfType(CardType cardType) {
        Predicate<Card> isCardType = card -> card.getType() == cardType;
        return cards
                .stream()
                .filter(isCardType)
                .collect(Collectors.toList());
    }

    public void displayCards() {
        ShellModel shellModel = ShellModel.getInstance();
        for (CardType cardType : CardType.values()) {
            List<Card> cardsOfType = getCardsOfType(cardType);
            String cardMessage = String.format("%d: %s cards.", cardsOfType.size(), cardType.toString());
            shellModel.notify(cardMessage);
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Player && ((Player) obj).id.equals(id);
    }
}
