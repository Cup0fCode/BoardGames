package water.of.cup.boardgames.game.maps;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.MathUtils;

public class MapManager {
	// handles map click maths & map images
	private static NamespacedKey mapValsKey;

	private int[][] mapStructure; // the structure of mapVals (0 will be used to mark missing maps)
	private int[][] rotatedMapStructure; // the structure of mapVals with rotation added
	private int[] dimensions; // dimensions of the board
	private int rotation; // the rotation of the board
	private Game game;

	public MapManager(int[][] mapStructure, int rotation, Game game) {
		this.mapStructure = MathUtils.cloneIntMatrix(mapStructure);
		this.rotation = rotation;
		this.game = game;
		dimensions = new int[] { mapStructure[0].length, mapStructure.length };

		// set rotatedMapStructure
		rotatedMapStructure = MathUtils.cloneIntMatrix(mapStructure);
		int i = 0;
		while (i < rotation) {
			rotatedMapStructure = MathUtils.rotateMatrix(rotatedMapStructure);
			i++;
		}
	}

	public int[] getClickLocation(double[] locDouble, ItemStack map) {
		if (!GameMap.isGameMap(map)) // this should never happen
			return new int[] { 0, 0 };

		GameMap gameMap = new GameMap(map);
		if (!gameMap.getGame().equals(game)) // neither should this
			return new int[] { 0, 0 };

		// calculate location on map clicked
		int x = (int) ((locDouble[0] - Math.floor(locDouble[0])) * 128);
		int y = (int) ((locDouble[1] - Math.floor(locDouble[1])) * 128);

		int[] loc = new int[] { x, y };

		// return screen loc if a screen was clicked
		Screen clickedScreen = getClickedScreen(map);
		if (getClickedScreen(map) != null)
			return clickedScreen.getClickLocation(loc, gameMap.getMapVal());

		// rotate the clicked position to account for rotated games
		int i = rotation;
		while (i < 4) {
			loc = MathUtils.rotatePointAroundPoint90Degrees(new double[] { 63.5, 63.5 }, loc);
			i++;
		}

		// add the map's position
		int[] mapPos = getMapValsLocationOnBoard(gameMap.getMapVal());
		loc = new int[] { loc[0] + mapPos[0] * 128, loc[1] + mapPos[1] * 128 };

		return loc;
	}

	public static NamespacedKey getMapValsKey() {
		return mapValsKey;
	}

	public static void setMapValsKey(NamespacedKey mapValsKey) {
		MapManager.mapValsKey = mapValsKey;
	}

	public int[] getMapValsLocationOnBoard(int mapVal) {
		int x = 0;
		int y = 0;

		loop: while (y < mapStructure.length) {
			while (x < mapStructure[y].length) {
				if (mapStructure[y][x] == mapVal)
					break loop;
				x++;
			}
			x = 0;
			y++;
		}

		return new int[] { x, y };
	}

	public int[] getMapValsLocationOnRotatedBoard(int mapVal) {
		int[] loc = getMapValsLocationOnBoard(mapVal);

		// check if screen contains mapval
		for (Screen screen : game.getScreens()) {
			if (screen.containsMapVal(mapVal)) {
				// calculate position on board:
				loc = screen.getPosition();
				loc[screen.getDirection() % 2] = loc[screen.getDirection() % 2]
						+ screen.getMapValsLocationOnScreen(mapVal)[0];
				break;
			}
		}

		int i = 0;
		while (i < rotation) {
			i++;
			loc = MathUtils.rotatePointAroundPoint90Degrees(new double[] { 0, 0 }, loc);
		}

		return loc;
	}

	public int getMapValAtLocationOnRotatedBoard(int x, int y) {
		int r = (4 - rotation) % 4;
		int[] loc = new int[] { x, y };
		int i = 0;
		while (i < r) {
			i++;
			loc = MathUtils.rotatePointAroundPoint90Degrees(new double[] { 0, 0 }, loc);
		}
		return mapStructure[loc[1]][loc[0]];
	}

	public ArrayList<MapData> getMapDataAtLocationOnRotatedBoard(int x, int z, int y) {
		ArrayList<MapData> mapDatas = new ArrayList<MapData>();
		int r = (4 - rotation) % 4;
		int[] loc = new int[] { x, z };
		int i = 0;
		while (i < r) {
			i++;
			loc = MathUtils.rotatePointAroundPoint90Degrees(new double[] { 0, 0 }, loc);
		}
		mapDatas.add(new MapData(mapStructure[loc[1]][loc[0]], BlockFace.UP)); // add base board's mapData

		for (Screen screen : game.getScreens()) {
			int mapVal = screen.getMapValAtLocation(loc[0], loc[1], y);
			if (mapVal > 0)
				mapDatas.add(new MapData(mapVal, screen.getBlockFace())); // add screen mapData
		}

		return mapDatas;
	}

	public int[] getDimensions() {
		return dimensions.clone();
	}

	public int[] getMapVals() {
		ArrayList<Integer> mapValsList = new ArrayList<Integer>();
		for (int[] mapValsArray : mapStructure)
			for (int mapVal : mapValsArray)
				if (mapVal > 0)
					mapValsList.add(mapVal);

		int[] mapVals = new int[mapValsList.size()];
		for (int i = 0; i < mapVals.length; i++) {
			mapVals[i] = mapValsList.get(i).intValue();
		}

		return mapVals;
	}

	public int[] getRotatedDimensions() {
		int[] rd = dimensions.clone();
		rd = new int[] { rd[0] - 1, rd[1] - 1 };
		int i = 0;
		while (i < rotation) {
			rd = MathUtils.rotatePointAroundPoint90Degrees(new double[] { 0, 0 }, rd);
			i++;
		}
		return rd;
	}

	public void renderBoard() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		System.out.println(dtf.format(now));
		for (int mapVal : getMapVals()) {
			GameMap map = game.getGameMapByMapVal(mapVal);
			MapMeta mapMeta = map.getMapMeta();
			MapView view = mapMeta.getMapView();
			for (MapRenderer renderer : view.getRenderers())
				view.removeRenderer(renderer);
			view.setLocked(false);

			GameRenderer renderer = new GameRenderer(game, getMapValsLocationOnBoard(mapVal));

			view.addRenderer(renderer);
			mapMeta.setMapView(view);
			map.setItemMeta(mapMeta);
			
			Bukkit.getServer().getScheduler().runTaskAsynchronously(BoardGames.getInstance(), new Runnable() {
				@Override
				public void run() {
					view.getWorld().getPlayers().forEach(player -> player.sendMap(view));
				}
			});
			//view.getWorld().getPlayers().forEach(player -> player.sendMap(view));
		}
		// render screens
		for (Screen screen : game.getScreens()) {
			screen.renderScreen();
		}

	}

	public int getRotation() {
		return rotation;
	}

	public Screen getClickedScreen(ItemStack map) {
		GameMap gameMap = new GameMap(map);
		int mapVal = gameMap.getMapVal();
		for (Screen screen : game.getScreens()) {
			if (screen.containsMapVal(mapVal))
				return screen;
		}
		return null;
	}
}
