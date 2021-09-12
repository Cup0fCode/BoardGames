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

public class MoneyHoney extends SlotsGame {
	// fruit machine

	public MoneyHoney(int rotation) {
		super(rotation);
	}

	@Override
	protected int[] getDimensions() {
		return new int[] { 3, 3 };
	}

	@Override
	protected void setGameName() {
		this.gameName = "MoneyHoneySlots";

	}

	@Override
	protected void setBoardImage() {
		this.gameImage = new GameImage("MONEYHONEY_BOARD");

	}

	@Override
	protected GameConfig getGameConfig() {
		// TODO Auto-generated method stub
		return new MoneyHoneyConfig(this);
	}

	@Override
	public ItemStack getBoardItem() {
		// TODO Auto-generated method stub
		return new BoardItem(gameName, new ItemStack(Material.ACACIA_TRAPDOOR, 1));
	}

	@Override
	protected Button[][] getSlotsButtons() {
		Button[][] buttons = new Button[3][3];
		int[] xloc = new int[] {7, 52 , 97};
		for (int x = 0 ; x < 3; x++)
			for (int y = 0; y < 3; y++) {
				Button b = new Button(this, "MONEYHONEY_GIRLS", new int[] { xloc[x] + 4, 26 + y * 25 + x }, 0, "slot");
				buttons[y][x] = b;
			}
		return buttons;
	}

	@Override
	protected ArrayList<SlotsSymbol> getSlotsSymbols() {
		ArrayList<SlotsSymbol> symbols = new ArrayList<SlotsSymbol>();

//			The symbols on its reels are:
//
//			(%) Cherries     2   5   -
//			(O) Oranges      4   1  10
//			(@) Plums        5   1   5
//			(A) Bells        1   8   1
//			(=) Bars         1   1   1
//			(*) Stars        2   1   1
//			(C) Melons       4   2   1
//			(&) Girls        1   1   1
//			The machine's payouts were:
//
//			%..         2
//			%%.         5
//			OOO, OO=   10
//			@@@, @@=   14
//			AAA, AA=   18
//			===       150
//			***       100
//			CCC        50
//			&&&       200

		// TODO: add images
		SlotsSymbol cherries = new SlotsSymbol("MONEYHONEY_CHERRIES", SymbolType.STANDARD);
		cherries.addPayout(1, .2);
		cherries.addPayout(2, .5);
		cherries.addPayout(3, 1);
		symbols.add(cherries);

		SlotsSymbol oranges = new SlotsSymbol("MONEYHONEY_ORANGES", SymbolType.STANDARD);
		oranges.addPayout(3, 1);
		symbols.add(oranges);
		
		SlotsSymbol plums = new SlotsSymbol("MONEYHONEY_PLUMS", SymbolType.STANDARD);
		plums.addPayout(3, 1.4);
		symbols.add(plums);
		
		SlotsSymbol bells = new SlotsSymbol("LIBERTYBELL_BELL", SymbolType.STANDARD);
		bells.addPayout(3, 1.8);
		symbols.add(bells);

		SlotsSymbol bars = new SlotsSymbol("MONEYHONEY_BARS", SymbolType.STANDARD);
		bars.addPayout(3, 15);
		
		HashMap<SlotsSymbol, Integer> barAndOranges = new HashMap<SlotsSymbol, Integer>();
		barAndOranges.put(oranges, 2);
		bars.addSpecialPayout(1, barAndOranges, 1);
		
		HashMap<SlotsSymbol, Integer> barAndPlums = new HashMap<SlotsSymbol, Integer>();
		barAndPlums.put(plums, 2);
		bars.addSpecialPayout(1, barAndPlums, 1.4);
		
		HashMap<SlotsSymbol, Integer> barAndBells = new HashMap<SlotsSymbol, Integer>();
		barAndBells.put(bells, 2);
		bars.addSpecialPayout(1, barAndBells, 1.8);
		
		symbols.add(bars);

		SlotsSymbol stars = new SlotsSymbol("LIBERTYBELL_STAR", SymbolType.STANDARD);
		stars.addPayout(3, 10);
		symbols.add(stars);

		SlotsSymbol melons = new SlotsSymbol("MONEYHONEY_MELONS", SymbolType.STANDARD);
		melons.addPayout(3, 5);
		symbols.add(melons);
		
		SlotsSymbol girls = new SlotsSymbol("MONEYHONEY_GIRLS", SymbolType.STANDARD);
		girls.addPayout(3, 20);
		symbols.add(girls);

		return symbols;
	}
}