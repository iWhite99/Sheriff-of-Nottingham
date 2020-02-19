package com.tema1.players;

import com.tema1.common.Constants;
import com.tema1.main.GameInput;

public class BasicPlayer extends Player {
  public BasicPlayer() {
    super();
  }

  /**
   * @param player represents the player that will be checked
   * @param gameInput is used to add to the end of the list the goods
   *                  that are confiscated or illegal
   * Check player method for the basic player.
   */
  @Override
  public void checkPlayer(final Player player, final GameInput gameInput) {
    if (this.getSum() >= Constants.LIMIT) {  // Check only if the sum allows
      super.checkPlayer(player, gameInput);
    } else {
      player.getAllGoods().getStall().addAll(player.getAllGoods().getBagGoods());
    }
  }
}
