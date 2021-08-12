package water.of.cup.boardgames.game.inventories.trade;

import org.bukkit.entity.Player;

public interface GameTradeCallback {

    void onAccept(GameTrade gameTrade);

    void onLeave(GameTrade gameTrade);

}
