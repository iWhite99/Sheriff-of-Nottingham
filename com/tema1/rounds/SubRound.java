package com.tema1.rounds;

import com.tema1.common.Constants;
import com.tema1.main.GameInput;
import com.tema1.players.Player;

import java.util.ArrayList;

final class SubRound {
  private GameInput gameInput;
  private ArrayList<Player> players;
  private int subRoundNumber;
  private int roundNumber;

  SubRound(final GameInput gameInput, final ArrayList<Player> players,
           final int subRoundNumber, final int roundNumber) {
    this.gameInput = gameInput;
    this.players = players;
    this.subRoundNumber = subRoundNumber;
    this.roundNumber = roundNumber;
  }

  void start() {
    Player sheriff = players.get(this.subRoundNumber);
    sheriff.setRole(Constants.SHERIFF);
    for (Player player : this.players) {
      if (player.getRole().equals(Constants.TRADER)) {
        ArrayList<Integer> goods = new ArrayList<>();
        for (int i = Constants.FROM_INDEX; i < Constants.TO_INDEX; i++) {
          goods.add(gameInput.getAssetIds().get(Constants.FIRST));
          gameInput.getAssetIds().remove(Constants.FIRST);
        }
        player.getAllGoods().setGoods(goods);
        player.setBestGoods(this.roundNumber);
        player.setDeclaredGoods();
      }
    }
    sheriff.checkPlayers(players, gameInput);
    sheriff.setRole(Constants.TRADER);
  }
}
