package water.of.cup.boardgames.listeners;

import java.util.Collection;
import java.util.HashMap;

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
import water.of.cup.boardgames.config.ConfigUtil;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.GameManager;
import water.of.cup.boardgames.game.games.chess.ChessBoardsUtil;
import water.of.cup.boardgames.game.maps.GameMap;

public class BoardInteract implements Listener {

	private BoardGames instance = BoardGames.getInstance();
	private GameManager gameManager = instance.getGameManager();
	private final HashMap<Player, Long> clickDelays = new HashMap<>();

	@EventHandler
	public void clickBoard(PlayerInteractEvent e) {
		Player player = e.getPlayer();

		// Only run if interacting with barrier
		Block block = player.getTargetBlock(null, 5);
		if (!block.getType().equals(Material.BARRIER))
			return;

		if(ConfigUtil.PERMISSIONS_ENABLED.toBoolean()
				&& !player.hasPermission("boardgames.interact"))
			return;

		// check hand to prevent double click
		if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if (e.getHand().equals(EquipmentSlot.HAND)) {
				return;
			}
		}

		// Click delay
		if(clickDelays.containsKey(player)) {
			long currentTime = System.currentTimeMillis();
			if(currentTime - clickDelays.get(player) >= ConfigUtil.BOARD_CLICK_DELAY.toInteger()) {
				clickDelays.remove(player);
			} else {
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
		boolean isChessBoardsMap = false;

		double minDistance = 100; //used to find the closest map clicked
		for (Entity entity : nearbyEntities) {

			if (!(entity instanceof ItemFrame))
				continue;
			ItemFrame frame = (ItemFrame) entity;
			ItemStack item = frame.getItem();
			boolean isGameMap = GameMap.isGameMap(item);
			if (isGameMap || ChessBoardsUtil.isChessBoardsMap(item)) {
				// Game found

				GameMap gameMap = null;

				if(isGameMap) {
					gameMap = new GameMap(item);
				}
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

					if(!isGameMap) {
						isChessBoardsMap = true;
					} else {
						map = gameMap;
						game = gameMap.getGame();
					}
				}
			}
		}

		// Loads in old ChessBoards as new BoardGames
		if(isChessBoardsMap) {
			Game newGame = gameManager.newGame("Chess", 0);

			if(newGame == null)
				return;

			ChessBoardsUtil.removeChessBoard(gameFrame);

			Location loc = gameFrame.getLocation().getBlock().getLocation();
			newGame.replace(loc, newGame.getRotation(), 1);
			gameManager.addGame(newGame);
			return;
		}

		if(game == null && gameFrame != null) {
			Game newGame = gameManager.newGame(map.getGameName(), map.getRotation());

			if(newGame == null)
				return;

			Location loc = gameFrame.getLocation().getBlock().getLocation();
			newGame.replace(loc, newGame.getRotation(), map.getMapVal());
			gameManager.addGame(newGame);
			return;
		}

		if (game == null) // check if a game is found
			return;

		if (gameFrame.getAttachedFace().getOppositeFace() != result.getHitBlockFace()) // check that the top of the																				// board was hit
			return;


		if (e.getAction().equals(Action.LEFT_CLICK_BLOCK) || e.getAction().equals(Action.LEFT_CLICK_AIR)) {
			e.setCancelled(true);

			// If they punch mid game, show ff option
			if(game.isIngame() && game.hasPlayer(player)) {
				game.displayGameInventory(player);
				return;
			}

			if(ConfigUtil.PERMISSIONS_ENABLED.toBoolean()
					&& !player.hasPermission("boardgames.destroy"))
				return;

			if (!game.isIngame() && game.destroy(gameFrame)) {

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
				player.sendMessage(ConfigUtil.CHAT_PLAYER_INGAME.toString());
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
			// Might want to only allow outside clicks if the game is ingame
			if(game.hasPlayer(player) || !game.hasGameInventory() || (game.allowOutsideClicks() && game.isIngame())) {
				Vector pos = result.getHitPosition();
				double[] loc = new double[] { pos.getX(), pos.getZ() };
				if (gameFrame.getAttachedFace() == BlockFace.WEST || gameFrame.getAttachedFace() == BlockFace.EAST) {
					loc = new double[] { pos.getZ(), pos.getY() };
				}
				if (gameFrame.getAttachedFace() == BlockFace.NORTH || gameFrame.getAttachedFace() == BlockFace.SOUTH) {
					loc = new double[] { pos.getX(), pos.getY() };
				}

				clickDelays.put(player, System.currentTimeMillis());
				game.click(player, loc, map);
			} else {
				game.displayGameInventory(player);
				//player.sendMessage("You are not part of this game");
			}

			e.setCancelled(true);
		}

	}

}