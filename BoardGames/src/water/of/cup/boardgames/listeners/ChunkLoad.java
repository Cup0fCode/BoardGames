package water.of.cup.boardgames.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.GameManager;
import water.of.cup.boardgames.game.maps.GameMap;

public class ChunkLoad implements Listener {
	private BoardGames instance = BoardGames.getInstance();
	private GameManager gameManager = instance.getGameManager();

	@EventHandler
	public void chunkLoad(ChunkLoadEvent e) {
		// create games in chunk if entity loaded
		for (Entity entity : e.getChunk().getEntities()) {
			if (entity.isDead())
				continue;
			if (!(entity instanceof ItemFrame))
				continue;
			ItemFrame frame = (ItemFrame) entity;
			ItemStack item = frame.getItem();
			if (!GameMap.isGameMap(item))
				continue;
			GameMap gameMap = new GameMap(item);

			if (gameManager.getGameByGameId(gameMap.getGameId()) == null) {
				final Game game = gameManager.newGame(gameMap.getGameName(), gameMap.getRotation());
				if (game == null) {
					continue;
				}
				// if (game.getPlacedMapVal() == gameMap.getMapVal()) {
				Location loc = frame.getLocation().getBlock().getLocation();
				game.replace(loc, game.getRotation(), gameMap.getMapVal());
				gameManager.addGame(game);
				// game.placeBoard(frame.getLocation(), game.getRotation());
				// }
			}
		}
	}
}
