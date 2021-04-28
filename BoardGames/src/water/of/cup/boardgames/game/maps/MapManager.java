package water.of.cup.boardgames.game.maps;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.GameImage;
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

	public int[] getDimensions() {
		return dimensions.clone();
	}

	public int[] getMapVals() {
		ArrayList<Integer> mapValsList = new ArrayList<Integer>();
		for (int[] mapValsArray : mapStructure)
			for (int mapVal : mapValsArray)
				if (mapVal != 0)
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
		for (int mapVal : getMapVals()) {
			GameMap map = game.getGameMapByMapVal(mapVal);
			MapMeta mapMeta = map.getMapMeta();
			MapView view = mapMeta.getMapView();
			for (MapRenderer renderer : view.getRenderers())
				view.removeRenderer(renderer);
			view.setLocked(false);
			view.addRenderer(new GameRenderer(game, getMapValsLocationOnBoard(mapVal)));
			mapMeta.setMapView(view);
			map.setItemMeta(mapMeta);
			view.getWorld().getPlayers().forEach(player -> player.sendMap(view));
		}
	}

	public int getRotation() {
		return rotation;
	}
}
