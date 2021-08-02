package water.of.cup.boardgames.game.wagers;

import org.bukkit.entity.Player;
import water.of.cup.boardgames.game.GamePlayer;
import water.of.cup.boardgames.game.inventories.trade.GameTrade;

public class ItemWager implements Wager {

    private final GameTrade gameTrade;

    public ItemWager(GameTrade gameTrade) {
        this.gameTrade = gameTrade;
    }

    @Override
    public void complete(GamePlayer winner) {
        if (winner == null) {
            cancel();
            return;
        }

        gameTrade.sendWinnerItems(winner.getPlayer());
    }

    @Override
    public void cancel() {
        gameTrade.sendBackItems();
    }
}
