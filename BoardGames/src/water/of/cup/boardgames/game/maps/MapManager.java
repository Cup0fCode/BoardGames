package water.of.cup.boardgames.game.maps;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.Utils;

public class MapManager {
	// handles map click maths & map images
	private static NamespacedKey mapValsKey;

	private int[][] mapStructure; // the structure of mapVals (0 will be used to mark missing maps)
	private int[][] rotatedMapStructure; // the structure of mapVals with rotation added
	private int[] dimensions; // dimensions of the board
	private int rotation; // the rotation of the board
	private Game game;

	public MapManager(int[][] mapStructure, int rotation, Game game) {
		this.mapStructure = mapStructure;
		this.rotation = rotation;
		this.game = game;
		dimensions = new int[] { mapStructure.length, mapStructure[0].length };

		// set rotatedMapStructure
		rotatedMapStructure = Utils.cloneIntMatrix(mapStructure);
		int i = 0;
		while (i < rotation) {
			rotatedMapStructure = Utils.rotateMatrix(rotatedMapStructure);
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
		int i = 0;
		while (i < rotation) {
			loc = Utils.rotatePointAroundPoint90Degrees(new int[] { 64, 64 }, loc);
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
			y++;
		}
		return new int[] { x, y };
	}

	public int[] getMapValsLocationOnRotatedBoard(int mapVal) {
		int x = 0;
		int y = 0;

		loop: while (y < rotatedMapStructure.length) {
			while (x < rotatedMapStructure[y].length) {
				if (rotatedMapStructure[y][x] == mapVal)
					break loop;
				x++;
			}
			y++;
		}
		return new int[] { x, y };
	}
	
	public int getMapValAtLocationOnRotatedBoard(int x, int y) {
		return rotatedMapStructure[y][x];
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
		if (rotation % 2 == 1)
			return new int[] { dimensions[1], dimensions[0] };
		return dimensions;
	}
}
