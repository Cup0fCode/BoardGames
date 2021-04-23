package water.of.cup.boardgames.game.games.battleship;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.GamePlayer;
import water.of.cup.boardgames.game.maps.GameMap;

public class Battleship extends Game {

	public Battleship(int rotation) {
		super(rotation);
	}

	@Override
	protected void setMapInformation() {
		this.mapStructure = new int[][] {
			{2},
			{1}
			};
		this.placedMapVal = 1;
		
	}

	@Override
	protected void setGameName() {
		this.gameName = "Battle Ship";
		
	}

	@Override
	protected void setBoardImage() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void startClock() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void click(Player player, double[] loc, ItemStack map) {
		int[] clickLoc = mapManager.getClickLocation(loc, map);
		player.sendMessage("you clicked: " + clickLoc[0] + "," + clickLoc[1]);
		player.sendMessage("board#: " + (new GameMap(map).getMapVal()));
		
	}

	@Override
	protected void gamePlayerOutOfTime(GamePlayer turn) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ItemStack getBoardItem() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
