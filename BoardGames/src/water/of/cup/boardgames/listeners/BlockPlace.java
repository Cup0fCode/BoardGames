package water.of.cup.boardgames.listeners;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.config.ConfigUtil;
import water.of.cup.boardgames.game.BoardItem;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.GameManager;
import water.of.cup.boardgames.game.games.chess.ChessBoardsUtil;

public class BlockPlace implements Listener {

	private BoardGames instance = BoardGames.getInstance();
	private GameManager gameManager = instance.getGameManager();

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		ItemStack itemStack = player.getInventory().getItemInMainHand();

//		calculate board rotation
		double yaw = player.getEyeLocation().getYaw();
		int rotation = ((int) ((yaw - 45) / 90 + 3) % 4);

		// check if itemStack is boardItem
		if (!BoardItem.isBoardItem(itemStack) && !ChessBoardsUtil.isChessBoardsItem(itemStack))
			return;

		Game game;
		if(ChessBoardsUtil.isChessBoardsItem(itemStack)) {
			game = gameManager.newGame("Chess", rotation);
		} else {
			game = gameManager.newGame(new BoardItem(itemStack), rotation);
		}

		
//		if (itemStack.getType() == Material.OAK_SAPLING)
//			game = new TicTacToe(rotation);
//		if (itemStack.getType() == Material.ACACIA_SAPLING)
//			game = new Battleship(rotation);
		
		
		// make sure game exists
		if (game == null)
			return;

		if(ConfigUtil.PERMISSIONS_ENABLED.toBoolean()
				&& !player.hasPermission("boardgames.place")) {
			event.setCancelled(true);
			return;
		}
		
		final Game finalGame = game;
		event.setCancelled(true);

		Location loc = event.getBlock().getLocation();

		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(instance, new Runnable() {
			@Override
			public void run() {
				if (finalGame.canPlaceBoard(loc, rotation)) {
					if(player.getGameMode() != GameMode.CREATIVE) {
						player.getInventory().getItemInMainHand()
								.setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
					}

					finalGame.placeBoard(loc, rotation);
					gameManager.addGame(finalGame);
					player.sendMessage(ConfigUtil.CHAT_PLACED_BOARD.toString());
				} else {
					player.sendMessage(ConfigUtil.CHAT_NO_BOARD_ROOM.toString());
				}
			}
		});
	}

}