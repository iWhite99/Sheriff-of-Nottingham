package com.tema1.players;

import com.tema1.common.Constants;
import com.tema1.main.GameInput;

public class GreedyPlayer extends BasicPlayer {
  public GreedyPlayer() {
    super();
  }

  @Override
  public final void setBestGoods(final int round) {
    super.setBestGoods(round);
    // In even rounds the greedy player adds one more illegal good to his bag
    if (round % 2 == Constants.EVEN && this.hasIllegalGoods()
            && this.getAllGoods().getBagGoods().size() < Constants.MAXIMUM_GOODS) {
      this.getAllGoods().getBagGoods().add(this.getBestGoodIllegal());
      this.getAllGoods().getGoods().remove(this.getIndex(this.getBestGoodIllegal()));
    }
  }

  @Override
  public final void checkPlayer(final Player player, final GameInput gameInput) {
    if (player.getBribe() == 0) {
      // Checks the player only if the player did not bribe the sheriff
      super.checkPlayer(player, gameInput);
    } else {
      int bribe = player.getBribe();
      player.setBribe(0);
      player.setSum(player.getSum() - bribe);
      this.setSum(this.getSum() + bribe);
      player.getAllGoods().getStall().addAll(player.getAllGoods().getBagGoods());
    }
  }
}
