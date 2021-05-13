package water.of.cup.boardgames.listeners;

import java.util.Collection;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
import org.w3c.dom.events.Event;

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

		double minDistance = 100; //used to find the closest map clicked
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

				// create bounding box
				double[] bounds = new double[] {0.5, .0313, 0.5};
				if (frame.getAttachedFace() == BlockFace.NORTH || frame.getAttachedFace() == BlockFace.SOUTH)
					bounds = new double[] {0.5, 0.5, 0.0313};
				if (frame.getAttachedFace() == BlockFace.EAST || frame.getAttachedFace() == BlockFace.WEST)
					bounds = new double[] {0.0313, 0.5, 0.5};


				BoundingBox box = new BoundingBox(x - bounds[0], y - bounds[1], z - bounds[2], x + bounds[0], y +  bounds[1], z + bounds[2]);
				// check if player clicked box
				RayTraceResult tempResult = box.rayTrace(player.getEyeLocation().toVector(), direction, 5);
				if (tempResult != null) {
					double distance = tempResult.getHitPosition().distance(player.getEyeLocation().toVector());
					if ( distance > minDistance)
						continue; // if this map is further away than the last map, keep searching
					minDistance = distance;
					result = tempResult;
					gameFrame = frame;
					map = gameMap;
					game = gameMap.getGame();
					continue;
				}
			}
		}

		if (game == null) // check if a game is found
			return;

		if (gameFrame.getAttachedFace().getOppositeFace() != result.getHitBlockFace()) // check that the top of the																				// board was hit
			return;

		// TODO: check for permissions

		// player.sendMessage("Game found! Status: " + game.getGameState().toString());

		if (e.getAction().equals(Action.LEFT_CLICK_BLOCK) || e.getAction().equals(Action.LEFT_CLICK_AIR)) {
			e.setCancelled(true);
			// TODO: check if game is running

			// TODO: check if player has permission to delete game

			if (game.destroy(gameFrame)) {

				// drop the board item TODO: check if board item should be dropped (player not in creative, game settings)
				ItemStack boardItem = game.getBoardItem();
				if (boardItem != null)
					player.getWorld().dropItem(e.getClickedBlock().getLocation(), boardItem);
			}


			return;

		}

		if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))

		{
			if (gameManager.getGameByPlayer(player) != null && gameManager.getGameByPlayer(player) != game) {
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

			// Temp dbeug
			if((gameManager.getGameByPlayer(player) != null && gameManager.getGameByPlayer(player).equals(game)) || !game.hasGameInventory()) {
				Vector pos = result.getHitPosition();
				double[] loc = new double[] { pos.getX(), pos.getZ() };
				if (gameFrame.getAttachedFace() == BlockFace.WEST || gameFrame.getAttachedFace() == BlockFace.EAST) {
					loc = new double[] { pos.getZ(), pos.getY() };
				}
				if (gameFrame.getAttachedFace() == BlockFace.NORTH || gameFrame.getAttachedFace() == BlockFace.SOUTH) {
					loc = new double[] { pos.getX(), pos.getY() };
				}

				game.click(player, loc, map);
			} else {
				game.displayGameInventory(player);
			}

			e.setCancelled(true);
		}

	}

}
