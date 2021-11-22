package water.of.cup.boardgames.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.inventory.ItemStack;

import water.of.cup.boardgames.game.maps.GameMap;

public class HangingBreakByEntity implements Listener {
	@EventHandler
	
	public void HangingBreakEvent(org.bukkit.event.hanging.HangingBreakEvent event) {
		if (event.getCause() == RemoveCause.ENTITY) {
			if (event.getEntity() instanceof ItemFrame) {
				Entity entity = event.getEntity();
				if (!(entity instanceof ItemFrame))
					return;
				ItemFrame frame = (ItemFrame) entity;
				ItemStack item = frame.getItem();
				if (GameMap.isGameMap(item))
					event.setCancelled(true);
			}
		}
	}
}
