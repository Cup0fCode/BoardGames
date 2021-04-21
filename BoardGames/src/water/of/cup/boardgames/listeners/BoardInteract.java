package water.of.cup.boardgames.listeners;

import java.util.Collection;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.GameManager;
import water.of.cup.boardgames.game.maps.GameMap;

public class BoardInteract implements Listener {

	private BoardGames instance = BoardGames.getInstance();
	private GameManager gameManager = instance.getGameManager();

	@EventHandler
	public void clickBoard(PlayerInteractEvent e) {
		Player player = e.getPlayer();

		// Only run if interacting with barrier
		Block block = player.getTargetBlock(null, 5);
		if (!block.getType().equals(Material.BARRIER))
			return;

		// check hand to prevent double click
		if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if (e.getHand().equals(EquipmentSlot.HAND)) {
				return;
			}
		}

		// attempt to find a game

		Vector direction = player.getEyeLocation().getDirection();

		// get nearby boards
		Collection<Entity> nearbyEntities = player.getWorld().getNearbyEntities(player.getLocation(), 4, 4, 4);

		RayTraceResult result = null;
		ItemFrame gameFrame = null;
		Game game = null;
		GameMap map = null;

		for (Entity entity : nearbyEntities) {

			if (!(entity instanceof ItemFrame))
				continue;
			ItemFrame frame = (ItemFrame) entity;
			ItemStack item = frame.getItem();
			if (GameMap.isGameMap(item)) {
				// Game found

				GameMap gameMap = new GameMap(item);
				// Game testGame = gameMap.getGame();

				Vector pos = frame.getLocation().toVector();
				double x = pos.getX();
				double y = pos.getY();
				double z = pos.getZ();

				// check if player clicked box
				BoundingBox box = new BoundingBox(x - 0.5, y - .0313, z - 0.5, x + 0.5, y + 0.0313, z + 0.5);
				RayTraceResult tempResult = box.rayTrace(player.getEyeLocation().toVector(), direction, 5);
				if (tempResult != null) {
					result = tempResult;
					gameFrame = frame;
					map = gameMap;
					game = gameMap.getGame();
					break;
				}

			} else {
				player.sendMessage("not a game map");
			}
		}

		if (game == null) // check if a game is found
			return;

		if (gameFrame.getAttachedFace().getOppositeFace() != result.getHitBlockFace()) // check that the top of the
																						// board was hit
			return;

		// TODO: check for permissions

		// player.sendMessage("Game found! Status: " + game.getGameState().toString());

		if (e.getAction().equals(Action.LEFT_CLICK_BLOCK) || e.getAction().equals(Action.LEFT_CLICK_AIR)) {
			e.setCancelled(true);

			// TODO: check if game is running

			// TODO: check if player has permission to delete game

			game.delete();
			Location frameLoc = gameFrame.getLocation();
			gameFrame.remove();

			frameLoc.getBlock().setType(Material.AIR);

			player.getWorld().dropItem(frameLoc, game.getBoardItem());
			return;

		}

		if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))

		{
			if (gameManager.getGameByPlayer(player) != null
					&& gameManager.getGameByPlayer(player) != game) {
				player.sendMessage("You must finish your game before joining another.");
				return;
			}

//			if (game.getGameState().equals(ChessGameState.IDLE)
//					|| game.getGameState().equals(ChessGameState.WAITING_PLAYER)) {
//				if (game.getGameState().equals(ChessGameState.IDLE)) {
//					ChessCreateGameInventory chessCreateGameInventory = new ChessCreateGameInventory(game);
//					chessCreateGameInventory.displayCreateGame(player, true);
//					instance.addCreateGamePlayer(player, chessCreateGameInventory);
//				} else {
//					if (game.getPlayerQueue().size() < 3) {
//						game.addPlayerToDecisionQueue(player);
//					} else {
//						player.sendMessage(ChatColor.RED + "Too many players queuing!");
//					}
//				}
//				return;
//			}

//			double hitx = result.getHitPosition().getX();
//			double hity = result.getHitPosition().getZ();
//
//			int loc[] = ChessUtils.getChessBoardClickLocation(hitx, hity, gameFrame.getRotation(), direction);

			Vector pos = result.getHitPosition();
			double[] loc = new double[] {pos.getX(), pos.getZ()};
			
			game.click(player, loc, map);

			e.setCancelled(true);
		}

	}

}
