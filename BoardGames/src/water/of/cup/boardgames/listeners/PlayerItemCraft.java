package water.of.cup.boardgames.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import water.of.cup.boardgames.config.ConfigUtil;
import water.of.cup.boardgames.game.BoardItem;

public class PlayerItemCraft implements Listener {

    @EventHandler
    public void onItemCraft(PrepareItemCraftEvent event) {
        if(event.getInventory().getResult() != null) {
            ItemStack result = event.getInventory().getResult();
            if(event.getViewers().size() > 0) {
                Player player = (Player) event.getViewers().get(0);
                if (BoardItem.isBoardItem(result)) {
                    BoardItem boardItem = new BoardItem(result);
                    if (ConfigUtil.PERMISSIONS_ENABLED.toBoolean()
                            && !player.hasPermission("chessboard.recipe." + boardItem.getName())) {
                        event.getInventory().setResult(null);
                    }
                }
            }
        }
    }
}
