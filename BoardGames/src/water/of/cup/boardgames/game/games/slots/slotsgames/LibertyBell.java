package water.of.cup.boardgames.game.games.slots.slotsgames;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import water.of.cup.boardgames.game.BoardItem;
import water.of.cup.boardgames.game.Button;
import water.of.cup.boardgames.game.GameConfig;
import water.of.cup.boardgames.game.GameImage;
import water.of.cup.boardgames.game.games.slots.SlotsGame;
import water.of.cup.boardgames.game.games.slots.SlotsSymbol;
import water.of.cup.boardgames.game.games.slots.SymbolType;

import java.util.ArrayList;
import java.util.HashMap;

public class LibertyBell extends SlotsGame {
	// fruit machine

	public LibertyBell(int rotation) {
		super(rotation);
	}

	@Override
	protected int[] getDimensions() {
		return new int[] { 3, 1 };
	}

	@Override
	protected void setGameName() {
		this.gameName = "LibertyBellSlots";

	}

	@Override
	protected void setBoardImage() {
		this.gameImage = new GameImage("LIBERTYBELL_BOARD");

	}

	@Override
	protected GameConfig getGameConfig() {
		// TODO Auto-generated method stub
		return new LibertyBellConfig(this);
	}

	@Override
	public ItemStack getBoardItem() {
		// TODO Auto-generated method stub
		return new BoardItem(gameName, new ItemStack(Material.ACACIA_TRAPDOOR, 1));
	}

	@Override
	protected Button[][] getSlotsButtons() {
		Button[][] buttons = new Button[1][3];
//		for (int x = 0; x < 3; x ++) {
//			Button b = new Button(this, "LIBERTYBELL_BELL", new int[] {40 + x * 16 , 56 }, 0, "slot");
//			buttons[0][x] = b;
//		}
		buttons[0][0] = new Button(this, "LIBERTYBELL_BELL", new int[] {18 , 16 }, 0, "slot");
		buttons[0][1] = new Button(this, "LIBERTYBELL_BELL", new int[] {41 , 16 }, 0, "slot");
		buttons[0][2] = new Button(this, "LIBERTYBELL_BELL", new int[] {63 , 16 }, 0, "slot");
		return buttons;
	}

	@Override
	protected ArrayList<SlotsSymbol> getSlotsSymbols() {
		ArrayList<SlotsSymbol> symbols = new ArrayList<SlotsSymbol>();
		
		// cracked Liberty Bell, diamond, spade, heart, and a horseshoe, star. 
		// https://en.wikipedia.org/wiki/Liberty_Bell_(game)
		// pay-outs per 5 cents:
//		2 horseshoes = 5 cents
//		2 horseshoe + 1 star = 10 cents
//		3 spades = 20 cents
//		3 diamonds = 30 cents
//		3 hearts = 40 cents
//		3 Liberty Bells = 50 cents
		
		//TODO: add images
		SlotsSymbol bell = new SlotsSymbol("LIBERTYBELL_BELL", SymbolType.STANDARD);
		bell.addPayout(3, 10);
		symbols.add(bell);
		
		SlotsSymbol diamond = new SlotsSymbol("LIBERTYBELL_DIAMOND", SymbolType.STANDARD);
		diamond.addPayout(3, 6);
		symbols.add(diamond);
		
		SlotsSymbol spade = new SlotsSymbol("LIBERTYBELL_SPADE", SymbolType.STANDARD);
		spade.addPayout(3, 4);
		symbols.add(spade);
		
		SlotsSymbol heart = new SlotsSymbol("LIBERTYBELL_HEART", SymbolType.STANDARD);
		heart.addPayout(3, 8);
		symbols.add(heart);
		
		SlotsSymbol star = new SlotsSymbol("LIBERTYBELL_STAR", SymbolType.STANDARD);
		symbols.add(star);
		
		SlotsSymbol horseshoe = new SlotsSymbol("LIBERTYBELL_HORSESHOE", SymbolType.STANDARD);
		horseshoe.addPayout(2, 1);
		HashMap<SlotsSymbol, Integer> horseshoeAndStar = new HashMap<SlotsSymbol, Integer>();
		horseshoeAndStar.put(star, 1);
		horseshoe.addSpecialPayout(2, horseshoeAndStar, 2);
		symbols.add(horseshoe);
		
		return symbols;
	}
}
