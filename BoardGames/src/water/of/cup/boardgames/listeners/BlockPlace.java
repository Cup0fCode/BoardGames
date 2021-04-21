package water.of.cup.boardgames.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.game.GameManager;
import water.of.cup.boardgames.game.games.tictactoe.TicTacToe;

public class BlockPlace implements Listener {

	private BoardGames instance = BoardGames.getInstance();
	private GameManager gameManager = instance.getGameManager();

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		ItemStack itemStack = player.getInventory().getItemInMainHand();

		if (itemStack.getType() != Material.OAK_SAPLING)
			return;

		event.setCancelled(true);

		int rotation = 0;
		Location loc = event.getBlock().getLocation();
		TicTacToe ttt = new TicTacToe(rotation);
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(instance, new Runnable() {
			@Override
			public void run() {
				if (ttt.canPlaceBoard(loc, rotation)) {
					ttt.placeBoard(loc, rotation);
					gameManager.addGame(ttt);
					player.sendMessage("placed");
				} else {
					player.sendMessage("no room to place");
				}
			}
		});
	}

}