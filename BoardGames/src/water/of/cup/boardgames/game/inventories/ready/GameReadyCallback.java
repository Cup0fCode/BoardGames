package water.of.cup.boardgames.game.inventories.ready;

import org.bukkit.entity.Player;

public interface GameReadyCallback {

    void onReady(Player player);

    void onLeave(Player player);

}
