package water.of.cup.boardgames.game.inventories.wager;

import org.bukkit.entity.Player;
import water.of.cup.boardgames.game.wagers.RequestWager;

public interface GameWagerCallback {

    void onCreate(RequestWager requestWager);

    void onCancel(RequestWager requestWager);

    void onAccept(RequestWager requestWager);

    void onLeave(Player player);

}
