package water.of.cup.boardgames.game.maps;

import java.util.ArrayList;

import org.bukkit.block.BlockFace;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import water.of.cup.boardgames.game.Button;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.GameImage;
import water.of.cup.boardgames.game.GamePlayer;

public class Screen {
	private int direction;
	private int initialDirection;
	private int[] dimensions;
	private int[] position;
	private int[][] mapStructure;
	private GameImage gameImage;
	private Game game;

	public Screen(Game game, String imageName, int direction, int[] position, int[][] mapStructure, int rotation) {
		this.game = game;
		this.initialDirection = direction % 4;
		this.direction = (direction + rotation) % 4;
		dimensions = new int[] { mapStructure[0].length, mapStructure.length };
		this.mapStructure = mapStructure;
		this.position = position;
		this.gameImage = new GameImage(imageName);
	}

	public int getHeight() {
		return dimensions[1] - 1;
	}

	public int getMapValAtLocation(int x, int z, int y) {
		int d;
		if (initialDirection % 2 == 0) {
			d = x;
			// check that screen is on z val
			if (position[1] != z)
				return 0;
		} else {
			d = z;
			// check that screen is on x val
			if (position[0] != x)
				return 0;
		}
		if (d >= dimensions[0] || y >= dimensions[1])
			return 0;
		return mapStructure[y][d];
	}

	public int[] getClickLocation(int[] loc, int mapVal) {
		// add the map's position

		// flip x val if necessary
		if (direction < 1)
			loc[0] = 127 - loc[0];

		// flip y val
		loc[1] = 127 - loc[1];

		int[] mapPos = getMapValsLocationOnScreen(mapVal);
		loc = new int[] { loc[0] + mapPos[0] * 128, loc[1] + mapPos[1] * 128 };

		return loc;
	}

	public int[] getMapValsLocationOnScreen(int mapVal) {
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

	public int[] getPosition() {
		return position;
	}

	public boolean containsMapVal(int mapVal) {
		for (int[] line : mapStructure)
			for (int val : line)
				if (mapVal == val)
					return true;
		return false;
	}
	
	public Button getClickedButton(GamePlayer gamePlayer, int[] loc) { // returns null if no button is clicked
		for (Button button : game.getButtons()) {
			if (button.getScreen() == this && button.clicked(gamePlayer, loc))
				return button;
		}
		return null;
	}

	public BlockFace getBlockFace() {
		switch (direction) {
		case 0:
			return BlockFace.NORTH;
		case 1:
			return BlockFace.EAST;
		case 2:
			return BlockFace.SOUTH;
		case 3:
			return BlockFace.WEST;
		}

		return null; // doesn't happen
	}

	public void renderScreen() {
		for (int mapVal : getMapVals()) {
			GameMap map = game.getGameMapByMapVal(mapVal);
			MapMeta mapMeta = map.getMapMeta();
			MapView view = mapMeta.getMapView();
			for (MapRenderer renderer : view.getRenderers())
				view.removeRenderer(renderer);
			view.setLocked(false);
			view.addRenderer(new GameRenderer(game, getMapValsLocationOnScreen(mapVal), this));
			mapMeta.setMapView(view);
			map.setItemMeta(mapMeta);
			view.getWorld().getPlayers().forEach(player -> player.sendMap(view));
		}
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

	public int getDirection() {
		return direction;
	}

	public GameImage getGameImage() {
		return gameImage;
	}
	
	public void setGameImage(GameImage gameImage) {
		this.gameImage = gameImage;
	}
}
