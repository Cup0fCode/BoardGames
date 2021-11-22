package water.of.cup.boardgames.listeners;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.game.GameManager;

public class ProjectileHit implements Listener {
	private final BoardGames instance = BoardGames.getInstance();
	private final GameManager gameManager = instance.getGameManager();

	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		
		if (event.getHitBlock() != null) { // check if a block was hit
			Block hitBlock = event.getHitBlock();
			Bukkit.getLogger().info("hight:" + hitBlock.getBoundingBox().getHeight());
			if (gameManager.getGamesInRegion(hitBlock.getWorld(), hitBlock.getBoundingBox().getMin(),
					hitBlock.getBoundingBox().getMax()).size() != 0) {
				if (!event.getEntity().isDead())
					event.getEntity().remove();
			}
		}
	}
}