package water.of.cup.boardgames.game.inventories.join;

import org.bukkit.entity.Player;

public interface JoinGameCallback {

    void onJoin(Player player);

    void onLeave(Player player);

}
