package water.of.cup.boardgames.game.maps;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.game.Game;

public class GameMap extends ItemStack {
	private int gameId;
	private int mapVal;
	private Game game;
	private int rotation;
	private String gameName;

	public GameMap(ItemStack itemStack) {
		super(itemStack);
		MapMeta itemMeta = (MapMeta) itemStack.getItemMeta();
		PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
		gameId = persistentDataContainer.get(Game.getGameIdKey(), PersistentDataType.INTEGER);
		mapVal = persistentDataContainer.get(MapManager.getMapValsKey(), PersistentDataType.INTEGER);
		game = BoardGames.getInstance().getGameManager().getGameByGameId(gameId);
		rotation = persistentDataContainer.get(MapManager.getRotationKey(), PersistentDataType.INTEGER);
		gameName = persistentDataContainer.get(Game.getGameNameKey(), PersistentDataType.STRING);
	}

	public GameMap(Game game, int mapVal, ItemStack itemStack) {
		super(itemStack);
		MapMeta itemMeta = (MapMeta) itemStack.getItemMeta();
		PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
		this.game = game;
		this.mapVal = mapVal;
		this.gameId = game.getGameId();
		this.rotation = game.getRotation();
		gameName = game.getGameName();

		persistentDataContainer.set(Game.getGameIdKey(), PersistentDataType.INTEGER, gameId);
		persistentDataContainer.set(MapManager.getMapValsKey(), PersistentDataType.INTEGER, mapVal);
		persistentDataContainer.set(MapManager.getRotationKey(), PersistentDataType.INTEGER, rotation);
		persistentDataContainer.set(Game.getGameNameKey(), PersistentDataType.STRING, gameName);
		this.setItemMeta(itemMeta);
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
		if (!persistentDataContainer.has(MapManager.getRotationKey(), PersistentDataType.INTEGER))
			return false;
		if (!persistentDataContainer.has(Game.getGameNameKey(), PersistentDataType.STRING))
			return false;

		return true;
	}

	public MapMeta getMapMeta() {
		return (MapMeta) getItemMeta();
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
	
	public int getRotation() {
		return rotation;
	}
	
	public String getGameName() {
		return gameName;
	}
}
