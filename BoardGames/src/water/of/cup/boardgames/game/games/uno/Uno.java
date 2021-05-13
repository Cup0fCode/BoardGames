package water.of.cup.boardgames.game.games.uno;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import water.of.cup.boardgames.game.BoardItem;
import water.of.cup.boardgames.game.Button;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.GameImage;
import water.of.cup.boardgames.game.GamePlayer;
import water.of.cup.boardgames.game.inventories.GameInventory;

import java.util.ArrayList;

public class Uno extends Game{
	private UnoDeck deck;

	public Uno(int rotation) {
		super(rotation);
	}

	@Override
	protected void setMapInformation(int rotation) {
		this.mapStructure = new int[][] { {3, 4},{ 1,2 } };
		this.placedMapVal = 1;
	}

	@Override
	protected void startGame() {
		deck = new UnoDeck();
		
	}

	@Override
	protected void setGameName() {
		this.gameName = "Uno";
	}

	@Override
	protected void setBoardImage() {
		// TODO Auto-generated method stub
		this.gameImage = new GameImage("UNO_BOARD");
		
	}

	@Override
	protected void startClock() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected GameInventory getGameInventory() {
		return null;
	}

	@Override
	public ArrayList<String> getTeamNames() {
		return null;
	}

	@Override
	public void click(Player player, double[] loc, ItemStack map) {
		int[] clickLoc = mapManager.getClickLocation(loc, map);
		UnoCard card = deck.drawCards(1).get(0);
		Button cardButton = new Button(this, card.getGameImage(), clickLoc, 0, card.getType());
		buttons.add(cardButton);
		mapManager.renderBoard();
	}

	@Override
	protected void gamePlayerOutOfTime(GamePlayer turn) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public BoardItem getBoardItem() {
		return new BoardItem(gameName, new ItemStack(Material.OAK_TRAPDOOR, 1));
	}

}
