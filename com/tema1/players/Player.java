package com.tema1.players;

import com.tema1.common.Constants;
import com.tema1.goods.Goods;
import com.tema1.goods.GoodsFactory;
import com.tema1.goods.GoodsType;
import com.tema1.goods.AllGoods;
import com.tema1.main.GameInput;

import java.util.ArrayList;

public abstract class Player implements Comparable<Player> {
  private String role;
  private String type;
  private Integer order;
  private Integer sum;
  private Integer bribe;
  private AllGoods allGoods;

  public final String getRole() {
    return role;
  }

  public final void setRole(final String role) {
    this.role = role;
  }

  private String getType() {
    return type;
  }

  public final void setType(final String type) {
    this.type = type;
  }

  private Integer getOrder() {
    return order;
  }

  public final void setOrder(final Integer order) {
    this.order = order;
  }

  public final Integer getSum() {
    return sum;
  }

  public final void setSum(final Integer sum) {
    this.sum = sum;
  }

  final Integer getBribe() {
    return bribe;
  }

  final void setBribe(final Integer bribe) {
    this.bribe = bribe;
  }

  public final AllGoods getAllGoods() {
    return allGoods;
  }

  Player() {
    this.role = null;
    this.type = null;
    this.order = 0;
    this.sum = 0;
    this.bribe = 0;
    this.allGoods = new AllGoods();
  }

  // Check if a player has legal goods
  final boolean hasLegalGoods() {
    for (int good : this.allGoods.getGoods()) {
      if (GoodsFactory.getInstance().getGoodsById(good).getType().equals(GoodsType.Legal)) {
        return true;
      }
    }
    return false;
  }

  // Check if a player has illegal goods
  final boolean hasIllegalGoods() {
    for (int good : this.allGoods.getGoods()) {
      if (GoodsFactory.getInstance().getGoodsById(good).getType().equals(GoodsType.Illegal)) {
        return true;
      }
    }
    return false;
  }

  // Get the frequency of a good
  private int getFrequency(final int good) {
    int frequency = 0;
    for (int currentGood : this.allGoods.getGoods()) {
      if (good == currentGood) {
        frequency++;
      }
    }
    return frequency;
  }

  // Get the best good available - legal preferred
  private int getBestGood() {
    int maxFrequency = 0;
    int maxGood = 0;
    int maxProfit = 0;
    for (int good : this.getAllGoods().getGoods()) {
      int currentFrequency = getFrequency(good);
      if (this.hasLegalGoods()) {
        int currentProfit = GoodsFactory.getInstance().getGoodsById(good).getProfit();
        GoodsType goodsType = GoodsFactory.getInstance().getGoodsById(good).getType();
        boolean legal = goodsType.equals(GoodsType.Legal);
        // Update only if the current good is legal
        if (legal) {
          // The best legal good should be the one with the highest frequency
          // If the frequency is equal, the good with the most profit will be returned
          // If the profit is also equal, the good with the highest id will be returned
          if (currentFrequency > maxFrequency) {
            maxFrequency = currentFrequency;
            maxGood = good;
            maxProfit = currentProfit;
          } else if ((currentFrequency == maxFrequency)
                  && (currentProfit > maxProfit)) {
            maxGood = good;
            maxProfit = currentProfit;
          } else if ((currentFrequency == maxFrequency)
                  && (currentProfit == maxProfit) && (good > maxGood)) {
            maxGood = good;
            maxProfit = currentProfit;
          }
        }
      } else {
        // If the player has no legal goods, it will take only the most profitable illegal good
        int currentProfit = GoodsFactory.getInstance().getGoodsById(good).getProfit();
        if (currentProfit > maxProfit) {
          maxGood = good;
          maxProfit = currentProfit;
        }
      }
    }
    return maxGood;
  }

  /**
   * @param round is used as some players might act different depending on the round parity
   * Set best goods for a generic player.
   */
  public void setBestGoods(final int round) {
    int good = this.getBestGood();
    ArrayList<Integer> goodsArray = new ArrayList<>();
    if (GoodsFactory.getInstance().getGoodsById(good).getType() == GoodsType.Legal) {
      int goodsNumber = this.getFrequency(good);
      if (goodsNumber > Constants.MAXIMUM_GOODS) {
        goodsNumber = Constants.MAXIMUM_GOODS;
      }
      for (int iterator = 0; iterator < goodsNumber; iterator++) {
        goodsArray.add(good);
      }
    } else {
      if (this.getSum() >= Constants.ILLEGAL_PENALTY) {
        goodsArray.add(good);
        this.getAllGoods().getGoods().remove(this.getIndex(this.getBestGoodIllegal()));
      }
    }
    this.allGoods.setBagGoods(goodsArray);
  }

  /**
   * @param player represents the player that will be checked
   * @param gameInput is used to add to the end of the list the goods
   *                  that are confiscated or illegal
   * Generic check player method for a player.
   */
  public void checkPlayer(final Player player, final GameInput gameInput) {
    Integer declaredGoods = player.getAllGoods().getDeclaredGoods();
    int penalty = 0;
    int legalPenalty = 0;
    boolean honest = true;
    for (Integer good : player.getAllGoods().getBagGoods()) {
      if (declaredGoods.equals(good)) {
        penalty += GoodsFactory.getInstance().getGoodsById(good).getPenalty();
        legalPenalty += GoodsFactory.getInstance().getGoodsById(good).getPenalty();
        player.getAllGoods().getStall().add(good);
      } else {
        penalty += GoodsFactory.getInstance().getGoodsById(good).getPenalty();
        gameInput.getAssetIds().add(good);
        honest = false;
      }
    }
    if (honest) {
      this.setSum(this.getSum() - legalPenalty);
      player.setSum(player.getSum() + legalPenalty);
    } else {
      penalty -= legalPenalty;
      this.setSum(this.getSum() + penalty);
      player.setSum(player.getSum() - penalty);
    }
  }

  /**
   * @param players represents the list containing all players
   * @param gameInput is used to add to the end of the list the goods
   *                  that are confiscated or illegal
   * Method to check all the other players.
   */
  public void checkPlayers(final ArrayList<Player> players, final GameInput gameInput) {
    for (Player player : players) {
      if (player.getRole().equals(Constants.TRADER)) {
        this.checkPlayer(player, gameInput);
      }
    }
  }

  public final void setDeclaredGoods() {
    if (this.getAllGoods().getBagGoods().isEmpty()) {
      this.getAllGoods().setDeclaredGoods(-1);
      return;
    }
    // Get the first good in the bag and check if it is legal or illegal
    int firstId = this.getAllGoods().getBagGoods().get(Constants.FIRST);
    Goods firstGood = GoodsFactory.getInstance().getGoodsById(firstId);
    if (firstGood.getType().equals(GoodsType.Legal)) {
      this.getAllGoods().setDeclaredGoods(firstId);
    } else {
      this.getAllGoods().setDeclaredGoods(Constants.APPLE);
    }
  }

  // Check if a player has illegal goods in bag
  private boolean hasIllegalBagGoods() {
    for (Integer good : this.getAllGoods().getBagGoods()) {
      if (GoodsFactory.getInstance().getGoodsById(good).getType() == GoodsType.Illegal) {
        return true;
      }
    }
    return false;
  }

  @Override
  public final int compareTo(final Player player) {
    // Compare method for the Collections.sort method
    int sumDifference = player.getSum() - this.getSum();
    if (sumDifference == 0) {
      return this.getOrder() - player.getOrder();
    }
    return sumDifference;
  }

  final int getBestGoodIllegal() {
    int maxProfit = 0;
    int maxGood = 0;
    for (Integer good : this.getAllGoods().getGoods()) {
      int currentProfit = GoodsFactory.getInstance().getGoodsById(good).getProfit();
      // The best illegal good should be the most profitable one
      if (currentProfit > maxProfit
              && GoodsFactory.getInstance().getGoodsById(good).getType() == GoodsType.Illegal) {
        maxProfit = currentProfit;
        maxGood = good;
      }
    }
    return maxGood;
  }

  final int getBestGoodLegal() {
    int maxProfit = 0;
    int maxGood = 0;
    for (Integer good : this.getAllGoods().getGoods()) {
      int currentProfit = GoodsFactory.getInstance().getGoodsById(good).getProfit();
      // The best legal good should be the most profitable one
      // If the profit is equal, the good with higher id will be returned
      if (currentProfit > maxProfit
              && GoodsFactory.getInstance().getGoodsById(good).getType() == GoodsType.Legal) {
        maxProfit = currentProfit;
        maxGood = good;
      } else if (currentProfit == maxProfit
              && GoodsFactory.getInstance().getGoodsById(good).getType() == GoodsType.Legal
              && good > maxGood) {
        maxGood = good;
      }
    }
    return maxGood;
  }

  // Return the first index of a good a player has
  final int getIndex(final int good) {
    int index = 0;
    for (Integer i : this.getAllGoods().getGoods()) {
      if (i.equals(good)) {
        return index;
      }
      index++;
    }
    return -1;
  }

  // Checks if a player will play the basic player strategy
  public final boolean isBasic() {
    return this.getSum() <= Constants.ILLEGAL_PENALTY + 1 || !(this.hasIllegalBagGoods());
  }

  // Returns a format to print the ranking
  @Override
  public final String toString() {
    return this.getOrder() + " " + this.getType().toUpperCase() + " " + this.getSum();
  }
}
