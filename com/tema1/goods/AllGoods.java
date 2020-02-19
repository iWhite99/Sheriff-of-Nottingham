package com.tema1.goods;

import java.util.ArrayList;

public final class AllGoods {
  private ArrayList<Integer> goods;     // Used to store the goods a player has in hand
  private ArrayList<Integer> bagGoods;  // Used to store the goods a player adds to his bag
  private Integer declaredGoods;        // Used to store the goods a player declares
  private ArrayList<Integer> stall;     // Used to store the goods a player adds to his stall

  public ArrayList<Integer> getGoods() {
    return goods;
  }

  public void setGoods(final ArrayList<Integer> goods) {
    this.goods = goods;
  }

  public ArrayList<Integer> getBagGoods() {
    return bagGoods;
  }

  public void setBagGoods(final ArrayList<Integer> bagGoods) {
    this.bagGoods = bagGoods;
  }

  public Integer getDeclaredGoods() {
    return declaredGoods;
  }

  public void setDeclaredGoods(final Integer declaredGoods) {
    this.declaredGoods = declaredGoods;
  }

  public ArrayList<Integer> getStall() {
    return stall;
  }

  public AllGoods() {
    this.goods = null;
    this.bagGoods = null;
    this.declaredGoods = -1;
    this.stall = new ArrayList<>();
  }
}
