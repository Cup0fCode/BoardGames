package water.of.cup.boardgames.game.inventories.create;

import org.bukkit.entity.Player;

public interface WaitPlayersCallback {

    void onAccept(Player player);

    void onDecline(Player player);

}
