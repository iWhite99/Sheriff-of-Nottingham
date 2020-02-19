package com.tema1.common;

import com.tema1.goods.Goods;
import com.tema1.goods.GoodsFactory;
import com.tema1.goods.GoodsType;
import com.tema1.goods.LegalGoods;
import com.tema1.players.Player;

import java.util.ArrayList;

public final class Bonus {
  private Bonus() {

  }

  private static void silkBonus(final ArrayList<Integer> integers) {
    integers.add(Constants.CHEESE);
    integers.add(Constants.CHEESE);
    integers.add(Constants.CHEESE);
  }

  private static void pepperBonus(final ArrayList<Integer> integers) {
    integers.add(Constants.CHICKEN);
    integers.add(Constants.CHICKEN);
  }

  private static void barrelBonus(final ArrayList<Integer> integers) {
    integers.add(Constants.BREAD);
    integers.add(Constants.BREAD);
  }

  private static void beerBonus(final ArrayList<Integer> integers) {
    integers.add(Constants.WINE);
    integers.add(Constants.WINE);
    integers.add(Constants.WINE);
    integers.add(Constants.WINE);
  }

  private static void seafoodBonus(final ArrayList<Integer> integers) {
    integers.add(Constants.TOMATO);
    integers.add(Constants.TOMATO);
    integers.add(Constants.POTATO);
    integers.add(Constants.POTATO);
    integers.add(Constants.POTATO);
    integers.add(Constants.CHICKEN);
  }

  public static void illegalBonus(final ArrayList<Player> players) {
    for (Player player : players) {
      ArrayList<Integer> stall = new ArrayList<>();
      for (Integer good : player.getAllGoods().getStall()) {
        Goods goods =  GoodsFactory.getInstance().getGoodsById(good);
        if (goods.getType().equals(GoodsType.Illegal)) {
          // Add the bonus for each illegal good
          if (good.equals(Constants.SILK)) {
            silkBonus(stall);
          } else if (good.equals(Constants.PEPPER)) {
            pepperBonus(stall);
          } else if (good.equals(Constants.BARREL)) {
            barrelBonus(stall);
          } else if (good.equals(Constants.BEER)) {
            beerBonus(stall);
          } else if (good.equals(Constants.SEAFOOD)) {
            seafoodBonus(stall);
          } else {
            System.out.println("Error: Good type not known");
            return;
          }
        }
      }
      player.getAllGoods().getStall().addAll(stall);
    }
  }

  public static void kingQueenBonus(final ArrayList<Player> players) {
    for (int i = Constants.FROM_INDEX; i < Constants.TO_INDEX; i++) {
      // Find the first two players with the maximum frequency for each good
      int firstMaxFrequency = 0;
      int secondMaxFrequency = 0;
      int firstIndex = 0;
      int secondIndex = 0;
      int currentPlayerIndex = 0;
      for (Player player : players) {
        // Find the current frequency and update the first and second if applicable
        int currentFrequency = 0;
        for (Integer good : player.getAllGoods().getStall()) {
          if (good.equals(i)) {
            currentFrequency++;
          }
        }
        if (currentFrequency > firstMaxFrequency) {
          // Update the first and second maximum frequencies
          secondMaxFrequency = firstMaxFrequency;
          secondIndex = firstIndex;
          firstMaxFrequency = currentFrequency;
          firstIndex = currentPlayerIndex;
        } else if (currentFrequency > secondMaxFrequency) {
          // Update the second maximum frequency
          secondMaxFrequency = currentFrequency;
          secondIndex = currentPlayerIndex;
        }
        currentPlayerIndex++;
      }
      // Add the king bonus if applicable
      if (firstMaxFrequency > 0) {
        int sum = players.get(firstIndex).getSum();
        sum += getKingQueenBonus(Constants.KING, i);
        players.get(firstIndex).setSum(sum);
        // Add the queen bonus if applicable
        if (secondMaxFrequency > 0) {
          int secondSum = players.get(secondIndex).getSum();
          secondSum += getKingQueenBonus(Constants.QUEEN, i);
          players.get(secondIndex).setSum(secondSum);
        }
      }
    }
  }

  private static int getKingQueenBonus(final boolean kingQueen, final int index) {
    LegalGoods legalGoods = (LegalGoods) GoodsFactory.getInstance().getGoodsById(index);
    if (kingQueen) {
      return legalGoods.getKingBonus();
    } else {
      return legalGoods.getQueenBonus();
    }
  }

  public static void getProfit(final ArrayList<Player> players) {
    for (Player player : players) {
      int sum = player.getSum();
      for (Integer good : player.getAllGoods().getStall()) {
        // Adds the profit for each good on the stall
        sum += GoodsFactory.getInstance().getGoodsById(good).getProfit();
      }
      player.setSum(sum);
    }
  }
}
