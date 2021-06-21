package water.of.cup.boardgames.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import water.of.cup.boardgames.game.maps.GameMap;

import java.util.Collection;

public class BlockBreak implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if(block.getType().equals(Material.BARRIER)) {
            Collection<Entity> nearbyEntities = event.getPlayer().getWorld().getNearbyEntities(block.getBoundingBox());
            for (Entity entity : nearbyEntities) {
                if (!(entity instanceof ItemFrame))
                    continue;
                ItemFrame frame = (ItemFrame) entity;
                ItemStack item = frame.getItem();

                // TODO: check if game is in manager
                if (GameMap.isGameMap(item)) {
                    event.setCancelled(true);
                }
            }
        }
    }

}
