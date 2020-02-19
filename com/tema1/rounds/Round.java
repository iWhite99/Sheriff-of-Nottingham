package com.tema1.rounds;

import com.tema1.main.GameInput;
import com.tema1.players.Player;

import java.util.ArrayList;

public final class Round {
  private GameInput gameInput;
  private ArrayList<Player> players;
  private int roundNumber;

  public Round(final GameInput gameInput, final ArrayList<Player> players, final int roundNumber) {
    this.gameInput = gameInput;
    this.players = players;
    this.roundNumber = roundNumber;
  }

  public void start() {
    for (int player = 0; player < this.players.size(); player++) {
      SubRound subRound = new SubRound(gameInput, players, player, roundNumber);
      subRound.start();
    }
  }
}
