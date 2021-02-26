package shell.prompt;

import common.Constants;
import common.Dice;
import common.validation.Validators;
import deck.Card;
import deck.Deck;
import map.country.Country;
import map.model.MapModel;
import player.HumanPlayer;
import player.NeutralPlayer;
import player.Player;
import player.model.PlayerModel;
import shell.model.ShellModel;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static common.Constants.INIT_COUNTRIES_NEUTRAL;
import static common.Constants.INIT_COUNTRIES_PLAYER;

public class ShellPromptFactory {
    private static final ShellModel shellModel = ShellModel.getInstance();
    private static final MapModel mapModel = MapModel.getInstance();
    private static final PlayerModel playerModel = PlayerModel.getInstance();

    public ShellPromptFactory() {
    }

    public ShellPrompt getPrompt(ShellPromptType type) {
        switch (type) {
            case GET_PLAYER_ONE:
                return new ShellPrompt(input -> {
                    HumanPlayer humanPlayerOne = new HumanPlayer(input, Constants.Colors.PLAYER_1_COLOR);
                    playerModel.addPlayer(humanPlayerOne);

                    shellModel.notify("Welcome " + humanPlayerOne.getName());
                    shellModel.notify(Constants.Notifications.NAME + "(P2)");

                    // Create the neutralPlayers
                    playerModel.createNeutralPlayers();
                }, Validators.nonEmpty);
            case GET_PLAYER_TWO:
                return new ShellPrompt(input -> {
                    HumanPlayer humanPlayerTwo = new HumanPlayer(input, Constants.Colors.PLAYER_2_COLOR);
                    playerModel.addPlayer(humanPlayerTwo);

                    shellModel.notify("Welcome " + humanPlayerTwo.getName() + "\n");

                    shellModel.notify(Constants.Notifications.TERRITORY);
                    shellModel.notify(Constants.Notifications.TERRITORY_OPTION);
                }, Validators.nonEmpty);
            case DRAW_TERRITORIES:
                return new ShellPrompt(input -> {
                    Deck deck = Deck.getInstance();

                    final AtomicInteger counter = new AtomicInteger(0);

                    final boolean showDraw = input.toLowerCase().contains("y");

                    for (int i = 0; i < INIT_COUNTRIES_PLAYER; i++) {
                        playerModel.forEachPlayer(player -> {
                            if (counter.getAndIncrement() < INIT_COUNTRIES_NEUTRAL * 6 || player instanceof HumanPlayer) {
                                Optional<Card> nullableCard = deck.drawCard();

                                nullableCard.ifPresent((!showDraw ? player::addCard : drawnCard -> {
                                    player.addCard(drawnCard);
                                    String drawnCountryName = drawnCard.getCountryName();
                                    String playerName = player.getName();
                                    shellModel.notify(playerName + Constants.Notifications.DRAWN + drawnCountryName);
                                }));
                            }
                        });
                    }

                    playerModel.assignInitialCountries();

                    deck.addWildcards();
                    deck.shuffle();

                    mapModel.showCountryComponents();

                    shellModel.notify("\n" + Constants.Notifications.DICE_ROLL);
                }, Validators.yesNo);
            case SELECT_FIRST_PLAYER:
                return new ShellPrompt(input -> {
                    Dice dice = new Dice();
                    int playerOneDiceSum;
                    int playerTwoDiceSum;

                    List<Player> players = playerModel.getPlayers();

                    do {
                        playerOneDiceSum = dice.getRollSum(2);
                        String playerOneDiceNotification = players.get(0).getName() + " "
                                + Constants.Notifications.ROLLED + dice.printRoll();

                        shellModel.notify(playerOneDiceNotification);

                        playerTwoDiceSum = dice.getRollSum(2);
                        String playerTwoDiceNotification = players.get(players.size() - 1).getName() + " "
                                + Constants.Notifications.ROLLED + dice.printRoll();

                        shellModel.notify(playerTwoDiceNotification);

                        if (playerOneDiceSum == playerTwoDiceSum) {
                            shellModel.notify("\nRolled the same number\nRolling again!");
                        } else if (playerOneDiceSum < playerTwoDiceSum) {
                            // player One goes first by default but if player One rolls a lower sum,
                            // then we change the turn so that the current player is now player Two.

                            Collections.swap(players, 0, players.size() - 1);
                        }

                        playerModel.updatePlayerIndicator();
                        playerModel.showPlayerIndicator();

                    } while (playerOneDiceSum == playerTwoDiceSum);

                    String currentPlayerName = players.get(0).getName();
                    shellModel.notify(String.format("%s rolled higher, so is going first", currentPlayerName));
                    shellModel.notify("Please press Enter to continue");
                }, Validators.alwaysValid);
            case BEFORE_REINFORCING_COUNTRY:
                return  new ShellPrompt(input -> {
                    Player player = playerModel.getCurrentPlayer();
                    boolean isNeutral = (player instanceof NeutralPlayer);

                    String message = ((isNeutral ? "Choose country Owned by " : "\nYour Turn ") + player.getName());
                    shellModel.notify(message);
                    shellModel.notify("Place down reinforcements.");

                    player.getOwnedCountries().forEach(mapModel::highlightCountry);
                }, Validators.alwaysValid);
            case REINFORCING_COUNTRY:
                // Choosing neutral countries to reinforce
                return new ShellPrompt(input -> {
                    Player player = playerModel.getCurrentPlayer();
                    Optional<Country> country = mapModel.getCountryByName(input);
                    int reinforcement = player instanceof NeutralPlayer ? 1 : 3;

                    country.ifPresent(validCountry -> mapModel.updateCountryArmyCount(validCountry, reinforcement));

                    shellModel.notify("Successfully placed reinforcements.");

                    // Undo highlight
                    player.getOwnedCountries().forEach(mapModel::highlightCountry);

                    // Change turn if current neutralPlayer is neutralPlayer4
                    playerModel.changeTurn();
                }, Validators.currentPlayerOccupies);

            default:
                // TODO: Implement what to when player wins
                return new ShellPrompt(input -> System.out.println("Winner winner chicken dinner."), Validators.alwaysValid);
        }
    }
}
