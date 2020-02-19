package com.tema1.players;

import com.tema1.common.Constants;
import com.tema1.goods.GoodsFactory;
import com.tema1.goods.GoodsType;
import com.tema1.main.GameInput;

import java.util.ArrayList;

public class BribedPlayer extends BasicPlayer {
  public BribedPlayer() {
    super();
  }

  @Override
  public final void checkPlayers(final ArrayList<Player> players, final GameInput gameInput) {
    int index = 0;
    int sheriffIndex = 0;
    for (Player player : players) {
      if (player.getRole().equals(Constants.SHERIFF)) {
        sheriffIndex = index;
      }
      index++;
    }
    // Get the index for the players beside the bribed player
    int leftPlayerIndex = sheriffIndex - 1;
    int rightPlayerIndex = sheriffIndex + 1;
    if (sheriffIndex == 0) {
      leftPlayerIndex = players.size() - 1;
    }
    if (sheriffIndex == players.size() - 1) {
      rightPlayerIndex = 0;
    }
    Player leftPlayer = players.get(leftPlayerIndex);
    Player rightPlayer = players.get(rightPlayerIndex);
    // Check the players if possible
    if (leftPlayerIndex != rightPlayerIndex) {
      if (this.getSum() >= Constants.HIGH_LIMIT) {
        this.checkPlayer(leftPlayer, gameInput);
        this.checkPlayer(rightPlayer, gameInput);
      } else {
        leftPlayer.getAllGoods().getStall().addAll(leftPlayer.getAllGoods().getBagGoods());
        rightPlayer.getAllGoods().getStall().addAll(rightPlayer.getAllGoods().getBagGoods());
      }
    } else {
      if (this.getSum() >= Constants.LIMIT) {
        this.checkPlayer(leftPlayer, gameInput);
      } else {
        leftPlayer.getAllGoods().getStall().addAll(leftPlayer.getAllGoods().getBagGoods());
      }
    }
    for (Player player : players) {
      if (!(player.equals(this) || player.equals(leftPlayer) || player.equals(rightPlayer))) {
        this.setSum(this.getSum() + player.getBribe());
        player.setSum(player.getSum() - player.getBribe());
        player.setBribe(0);
        player.getAllGoods().getStall().addAll(player.getAllGoods().getBagGoods());
      }
    }
  }

  @Override
  public final void setBestGoods(final int round) {
    int illegalGoodsNumber = this.maximumIllegal();  // Maximum illegal goods the player can take
    int illegalGoodsBag = this.illegalGoods();       // Number of illegal goods the player has
    if (illegalGoodsBag == 0 || illegalGoodsNumber == 0) {
      this.setBribe(0);
      super.setBestGoods(round);
      return;
    }
    this.getAllGoods().setBagGoods(new ArrayList<>());
    if (illegalGoodsBag < illegalGoodsNumber) {
      illegalGoodsNumber = illegalGoodsBag;
    }
    // Set the correct bribe
    if (illegalGoodsNumber >= Constants.BRIBE_MINIMUM_ITEMS
            && illegalGoodsNumber <= Constants.BRIBE_MAXIMUM_ITEMS) {
      this.setBribe(Constants.MINIMUM_BRIBE);
    } else if (illegalGoodsNumber > Constants.BRIBE_MAXIMUM_ITEMS) {
      this.setBribe(Constants.MAXIMUM_BRIBE);
    }
    // Add the illegal goods to the bag
    for (int i = 0; i < illegalGoodsNumber; i++) {
      int good = this.getBestGoodIllegal();
      this.getAllGoods().getBagGoods().add(good);
      this.getAllGoods().getGoods().remove(this.getIndex(good));
    }
    // Add the legal goods to the bag
    for (int i = illegalGoodsNumber; i < this.maximumLegal(illegalGoodsNumber); i++) {
      if (this.hasLegalGoods()) {
        int good = this.getBestGoodLegal();
        this.getAllGoods().getBagGoods().add(good);
        this.getAllGoods().getGoods().remove(this.getIndex(good));
      }
    }
  }

  // Return the number of maximum illegal goods so that the player does not run out of money
  private int maximumIllegal() {
    if (this.getSum() <= Constants.ILLEGAL_PENALTY + 1) {
      return 0;
    }
    if (this.getSum() / Constants.ILLEGAL_PENALTY > Constants.MAXIMUM_GOODS) {
      return Constants.MAXIMUM_GOODS;
    }
    int value = this.getSum() / Constants.ILLEGAL_PENALTY;
    if (value * Constants.ILLEGAL_PENALTY == this.getSum()) {
      return value - 1;
    }
    return value;
  }

  // Return the number of maximum legal goods so that the player does not run out of money
  private int maximumLegal(final int illegalGoodsNumber) {
    int maximum = (this.getSum() - Constants.ILLEGAL_PENALTY * illegalGoodsNumber)
            / Constants.LEGAL_PENALTY;
    if (maximum + illegalGoodsNumber > Constants.MAXIMUM_GOODS) {
      return Constants.MAXIMUM_GOODS;
    }
    if (maximum * Constants.LEGAL_PENALTY == this.getSum()
            - Constants.ILLEGAL_PENALTY * illegalGoodsNumber) {
      return maximum + illegalGoodsNumber - 1;
    }
    return maximum + illegalGoodsNumber;
  }

  // Counts the number of illegal goods a player has
  private int illegalGoods() {
    int counter = 0;
    for (Integer good : this.getAllGoods().getGoods()) {
      if (GoodsFactory.getInstance().getGoodsById(good).getType() == GoodsType.Illegal) {
        counter++;
      }
    }
    return counter;
  }
}
