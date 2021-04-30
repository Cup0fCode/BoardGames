package water.of.cup.boardgames.game.inventories.wager;

import water.of.cup.boardgames.game.GamePlayer;

public class WagerOption {

    private double wagerAmount;
    private GamePlayer gamePlayer;

    public WagerOption(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
        this.wagerAmount = 0.0;
    }

    public void setWagerAmount(double amount) {
        this.wagerAmount = amount;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public double getWagerAmount() {
        return wagerAmount;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }
}
