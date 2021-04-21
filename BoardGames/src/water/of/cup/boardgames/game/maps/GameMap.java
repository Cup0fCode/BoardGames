package water.of.cup.boardgames.game.maps;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.game.Game;

public class GameMap extends ItemStack {
	private int gameId;
	private int mapVal;
	private Game game;

	public GameMap(ItemStack itemStack) {
		super(itemStack);
		MapMeta itemMeta = (MapMeta) itemStack.getItemMeta();
		PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
		gameId = persistentDataContainer.get(Game.getGameIdKey(), PersistentDataType.INTEGER);
		mapVal = persistentDataContainer.get(MapManager.getMapValsKey(), PersistentDataType.INTEGER);
		game = BoardGames.getInstance().getGameManager().getGameByGameId(gameId);
	}

	public GameMap(Game game, int mapVal, ItemStack itemStack) {
		super(itemStack);
		MapMeta itemMeta = (MapMeta) itemStack.getItemMeta();
		PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
		this.game = game;
		this.mapVal = mapVal;
		this.gameId = game.getGameId();
		
		persistentDataContainer.set(Game.getGameIdKey(), PersistentDataType.INTEGER, gameId);
		persistentDataContainer.set(MapManager.getMapValsKey(), PersistentDataType.INTEGER, mapVal);
	}

	public static boolean isGameMap(ItemStack itemStack) {
		if (itemStack.getType() != Material.FILLED_MAP)
			return false;
		MapMeta itemMeta = (MapMeta) itemStack.getItemMeta();
		PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();

		// check that map has appropriate persistent data
		if (!persistentDataContainer.has(Game.getGameIdKey(), PersistentDataType.INTEGER))
			return false;
		if (!persistentDataContainer.has(MapManager.getMapValsKey(), PersistentDataType.INTEGER))
			return false;

		return false;
	}

	public int getGameId() {
		return gameId;
	}

	public int getMapVal() {
		return mapVal;
	}

	public Game getGame() {
		return game;
	}
}
