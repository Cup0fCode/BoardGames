package water.of.cup.boardgames.game.inventories.wager;

import water.of.cup.boardgames.game.GamePlayer;
import water.of.cup.boardgames.game.wagers.RequestWager;

public class WagerOption {

    private double wagerAmount;
    private GamePlayer gamePlayer;
    private boolean opened;
    private RequestWager selectedWager;

    public WagerOption(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
        this.wagerAmount = 0.0;
        this.opened = false;
        this.selectedWager = null;
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

    public boolean isOpened() {
        return opened;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public RequestWager getSelectedWager() {
        return selectedWager;
    }

    public void setSelectedWager(RequestWager selectedWager) {
        this.selectedWager = selectedWager;
    }
}
