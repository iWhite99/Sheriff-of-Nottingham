package com.tema1.main;

import com.tema1.common.Bonus;
import com.tema1.common.Constants;
import com.tema1.players.BasicPlayer;
import com.tema1.players.BribedPlayer;
import com.tema1.players.GreedyPlayer;
import com.tema1.players.Player;
import com.tema1.rounds.Round;

import java.util.ArrayList;
import java.util.Collections;

public final class Main {
  private Main() {

  }

  public static void main(final String[] args) {
    GameInputLoader gameInputLoader = new GameInputLoader(args[0], args[1]);
    GameInput gameInput = gameInputLoader.load();
    ArrayList<Player> players = new ArrayList<>();
    for (int player = 0; player < gameInput.getPlayerNames().size(); player++) {
      Player playerType;
      String type = gameInput.getPlayerNames().get(player);
      switch (type) {
        case Constants.BASIC_PLAYER:
          playerType = new BasicPlayer();
          break;
        case Constants.GREEDY_PLAYER:
          playerType = new GreedyPlayer();
          break;
        case Constants.BRIBED_PLAYER:
          playerType = new BribedPlayer();
          break;
        default:
          // Currently the player type is not defined
          System.out.println("Error: Player type not known");
          return;
      }
      // Initialize each player
      players.add(playerType);
      players.get(player).setSum(Constants.STARTING_SUM);
      players.get(player).setRole(Constants.TRADER);
      players.get(player).setType(type);
      players.get(player).setOrder(player);
    }
    int rounds = gameInput.getRounds();
    if (rounds > Constants.MAXIMUM_ROUNDS) {
      // Up to five rounds can be played
      rounds = Constants.MAXIMUM_ROUNDS;
    }
    for (int roundNumber = 0; roundNumber < rounds; roundNumber++) {
      Round round = new Round(gameInput, players, roundNumber);
      round.start();
    }
    // Add the bonuses to the players
    Bonus.illegalBonus(players);
    Bonus.kingQueenBonus(players);
    Bonus.getProfit(players);
    Collections.sort(players);  // Sort the players by score in descending order
    // Print the final ranking
    for (Player player : players) {
      // Print for each player the index he had in the original array, the strategy and the score
      System.out.println(player.toString());
    }
  }
}
