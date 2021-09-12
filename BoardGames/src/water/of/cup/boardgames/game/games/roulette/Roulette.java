package water.of.cup.boardgames.game.games.roulette;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.config.ConfigUtil;
import water.of.cup.boardgames.game.*;
import water.of.cup.boardgames.game.games.gameutils.EconomyUtils;
import water.of.cup.boardgames.game.inventories.GameInventory;
import water.of.cup.boardgames.game.inventories.GameOption;
import water.of.cup.boardgames.game.inventories.GameOptionType;
import water.of.cup.boardgames.game.inventories.number.GameNumberInventory;
import water.of.cup.boardgames.game.npcs.GameNPC;
import water.of.cup.boardgames.game.storage.GameStorage;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Roulette extends Game {
	private RouletteSpinner spinner;
	private Button spinnerButton;
	private boolean spinning;
	private RouletteSpinnerRunnable spinnerRunnable;
	private int spinnerVal;

	private RouletteStateRunnable rouletteStateRunnable;

	private HashMap<GamePlayer, ArrayList<RouletteBet>> playerBets;

	private ArrayList<RouletteBetPosition> betPositions;

	private ArrayList<Button> betButtons;

	public Roulette(int rotation) {
		super(rotation);
		spinner = new RouletteSpinner(this);
		spinnerButton = new Button(this, spinner.getGameImage(), new int[] { 0, 256 }, 0, "spinner");
		buttons.add(spinnerButton);

		Button betsButton = new Button(this, "ROULETTE_BETS", new int[] { 0, 0 }, 2, "bets");
		buttons.add(betsButton);

		setBetPositions();

		spinning = false;
		spinnerVal = 0;
		playerBets = new HashMap<GamePlayer, ArrayList<RouletteBet>>();
		betButtons = new ArrayList<Button>();
	}

	private void setBetPositions() {

		betPositions = new ArrayList<RouletteBetPosition>();

//		private Button[] cornerButtons;
		int[] cornerImage = { 7, 7 };

		for (int i = 0; i < 22; i++) {
			int[] loc = { 113 - (i % 2) * 23, 207 - (i / 2) * 17 - i / 8 };

			betPositions.add(new RouletteBetPosition(cornerImage, loc, i, "corner"));
		}

//		private Button[] straitButtons;
		int[] straitImage = { 18, 12 };

		for (int i = 0; i < 36; i++) {
			int[] loc = { 119 - (i % 3) * 23, 213 - (i / 3) * 17 - i / 12 };

			betPositions.add(new RouletteBetPosition(straitImage, loc, i, "strait"));
		}

//		private Button[] splitButtons;
		int[] splitHImage = { 16, 5 };
		int[] splitVImage = { 5, 10 };

		for (int i = 0; i < 24; i++) {
			int[] loc = { 114 - (i % 2) * 23, 214 - (i / 2) * 17 - i / 8 };

			betPositions.add(new RouletteBetPosition(splitVImage, loc, i, "split"));
		}
		for (int i = 24; i < 57; i++) {
			int[] loc = { 120 - ((i - 24) % 3) * 23, 208 - ((i - 24) / 3) * 17 - (i - 24) / 12 };

			betPositions.add(new RouletteBetPosition(splitHImage, loc, i, "split"));
		}

//		private Button[] streetButtons;

		int[] streetImage = { 6, 10 };

		for (int i = 0; i < 12; i++) {
			int[] loc = { 137, 214 - i * 17 - i / 4 };

			betPositions.add(new RouletteBetPosition(streetImage, loc, i, "street"));
		}

//		private Button[] sixLineButtons;

		for (int i = 0; i < 11; i++) {
			int[] loc = { 136, 207 - i * 17 - i / 4 };

			betPositions.add(new RouletteBetPosition(cornerImage, loc, i, "six line"));
		}

//		private Button[] trioButtons;

		for (int i = 0; i < 2; i++) {
			int[] loc = { 113 - i * 23, 224 };

			betPositions.add(new RouletteBetPosition(cornerImage, loc, i, "trio"));
		}

//		private Button basketButton;

		// use cornerImage
		int[] basketLoc = { 136, 224 };

		betPositions.add(new RouletteBetPosition(cornerImage, basketLoc, 0, "basket"));

//		private Button[] redBlackButtons;

		int[] outsideSmallImage = { 23, 33, BufferedImage.TYPE_INT_RGB };

		for (int i = 0; i < 2; i++) {
			int[] loc = { 162, 125 - i * 34 };

			betPositions.add(new RouletteBetPosition(outsideSmallImage, loc, i, "red or black"));
		}

//		private Button[] oddEvenButtons;

		for (int i = 0; i < 2; i++) {
			int[] loc = { 162, 160 - i * 106 };

			betPositions.add(new RouletteBetPosition(outsideSmallImage, loc, 1 - i, "odd or even"));
		}

//		private Button[] eighteenButtons;

		for (int i = 0; i < 2; i++) {
			int[] loc = { 162, 194 - i * 172 };

			betPositions.add(new RouletteBetPosition(outsideSmallImage, loc, i, "1 to 18 or 19 to 36"));
		}

//		private Button[] dozensButtons;

		int[] dozensImage = { 20, 67 };

		for (int i = 0; i < 3; i++) {
			int[] loc = { 141, 160 - i * 69 };

			betPositions.add(new RouletteBetPosition(dozensImage, loc, i, "dozens"));
		}

//		private Button[] columnsButtons;
		int[] columnsImage = { 22, 15 };

		// columnsButtons = new Button[3];
		for (int i = 0; i < 3; i++) {
			int[] loc = { 117 - i * 23, 6 };

			betPositions.add(new RouletteBetPosition(columnsImage, loc, i, "columns"));
		}

		// private Button[] strait0Buttons;
		int[] strait0Image = { 34, 18 };

		// strait0Buttons = new Button[2];
		for (int i = 0; i < 2; i++) {
			int[] loc = { 105 - i * 35, 229 };

			betPositions.add(new RouletteBetPosition(strait0Image, loc, i, "strait0"));
		}
	}

	@Override
	protected void setMapInformation(int rotation) {
		this.mapStructure = new int[][] { { 7, 8 }, { 5, 6 }, { 3, 4 }, { 1, 2 } };
		this.placedMapVal = 1;

	}

//	protected void startRound() {
//		spin();
//	}

	protected void prepareNextRound() {
		playerBets = new HashMap<GamePlayer, ArrayList<RouletteBet>>();

	}
	
	private void dealerSendMessage(Player player, String message) {
		player.sendMessage(ConfigUtil.CHAT_ROULETTE_DEALER.toString() + message);
	}

	protected void dealerSendAll(String message) {
		for (GamePlayer player : teamManager.getGamePlayers())
			player.getPlayer().sendMessage(ConfigUtil.CHAT_ROULETTE_DEALER.toString() + message);
	}

	@Override
	public void startGame() {
		//clearGamePlayers();
		//super.startGame();
		spawnNPC();
		super.setInGame();
		rouletteStateRunnable = new RouletteStateRunnable(this);
		rouletteStateRunnable.runTaskTimer(BoardGames.getInstance(), 9, 9);
	}

	@Override
	public void exitPlayer(Player player) {
		// In betting stage
		GamePlayer gamePlayer = teamManager.getGamePlayer(player);
		if(rouletteStateRunnable.canBet()) {
			// Give money back
			returnRouletteBets(gamePlayer);

			// Clear chips
			clearRouletteChips(gamePlayer);
			mapManager.renderBoard();
		}

		playerBets.remove(gamePlayer);
		teamManager.removeTeamByPlayer(player);

		if(teamManager.getGamePlayers().size() <= 0)
			endGame(null);
	}

	private void returnRouletteBets(GamePlayer gamePlayer) {
		if(!playerBets.containsKey(gamePlayer)) return;
		for(RouletteBet rouletteBet : playerBets.get(gamePlayer)) {
			EconomyUtils.playerGiveMoney(gamePlayer.getPlayer(), rouletteBet.getAmount());
		}
	}

	private void clearRouletteChips(GamePlayer gamePlayer) {
		if(!playerBets.containsKey(gamePlayer)) return;
		ArrayList<Button> chipButtons = new ArrayList<>();
		for(RouletteBet rouletteBet : playerBets.get(gamePlayer)) {
			chipButtons.add(rouletteBet.getButton());
		}

		buttons.removeAll(chipButtons);
		betButtons.removeAll(chipButtons);
	}

	@Override
	protected void setGameName() {
		this.gameName = "Roulette";

	}

	@Override
	protected void setBoardImage() {
		this.gameImage = new GameImage("ROULETTE_BOARD");

	}

	@Override
	protected void clockOutOfTime() {
		// TODO Auto-generated method stub

	}

	@Override
	protected Clock getClock() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected GameInventory getGameInventory() {
		// TODO Auto-generated method stub
		return new RouletteInventory(this);
	}

	@Override
	protected GameStorage getGameStorage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<String> getTeamNames() {
		// TODO Auto-generated method stub
		ArrayList<String> names = new ArrayList<String>();
		names.add("RED");
		names.add("BLUE");
		names.add("GREEN");
		names.add("YELLOW");
		names.add("PURPLE");
		names.add("AQUA");
		names.add("GREY");

		return names;
	}

	@Override
	protected GameConfig getGameConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void click(Player player, double[] loc, ItemStack map) {
//		if (spinning)
//			return;
		GamePlayer gamePlayer = getGamePlayer(player);
		if (gamePlayer == null) {
			gamePlayer = this.addPlayer(player);
			//teamManager.addTeam(gamePlayer);
			this.dealerSendMessage(player, ConfigUtil.CHAT_ROULETTE_WELCOME.buildString(teamManager.getTeamByPlayer(gamePlayer).toLowerCase()));
			return;
		}
			
		
		// player.sendMessage("clicked");
		
		if (!rouletteStateRunnable.canBet())
			return; 
		
		int[] clickLoc = mapManager.getClickLocation(loc, map);

		RouletteBetPosition b = getClickedBetPosition(clickLoc);
		if (b != null) {
			// bet Button clicked
			GamePlayer finalGamePlayer = gamePlayer;
			GameOption betOption = new GameOption("bet", Material.GOLD_INGOT, GameOptionType.COUNT, ConfigUtil.GUI_BET_AMOUNT_LABEL.toString(),  "0", true);
			new GameNumberInventory(gameInventory).build(player, (s, betAmount) -> {
				if(betAmount > 0 && EconomyUtils.playerTakeMoney(player, betAmount)) {
					String type = b.getType();
					int position = b.getPosition();
					RouletteBet bet = new RouletteBet(type, position, betAmount, clickLoc, teamManager.getTeamByPlayer(finalGamePlayer),
							this);

//					player.sendMessage(ConfigUtil.CHAT_ROULETTE_NUMBERS.buildString(type, Arrays.toString(bet.getWinningNums().toArray())));

					if (!playerBets.containsKey(finalGamePlayer))
						playerBets.put(finalGamePlayer, new ArrayList<RouletteBet>());

					playerBets.get(finalGamePlayer).add(bet);

					betButtons.add(bet.getButton());
					buttons.add(bet.getButton());
					mapManager.renderBoard();
				}
			}, betOption, 0);
		}

		//spin();
	}

	private RouletteBetPosition getClickedBetPosition(int[] clickLoc) {
		for (RouletteBetPosition position : betPositions) {
			if (position.clicked(clickLoc))
				return position;
		}
		return null;
	}

	protected void spin() {
		spinning = true;
		spinnerRunnable = new RouletteSpinnerRunnable(this, spinner);
		spinnerRunnable.runTaskTimer(BoardGames.getInstance(), 3, 3);
	}

	protected void endSpin() {
		spinning = false;
		rouletteStateRunnable.endSpin();
		if (!spinnerRunnable.isCancelled())
			spinnerRunnable.cancel();
		spinnerVal = spinner.getValue();
		dealerSendAll(ConfigUtil.CHAT_ROULETTE_SPINNER.buildString(spinnerVal + ""));
//		teamManager.getGamePlayers().get(0).getPlayer().sendMessage("value: " + spinnerVal); debug?
		giveBets();
	}

	private void giveBets() {
		int spinnerVal = spinner.getValue();
		for (GamePlayer player : playerBets.keySet()) {
			ArrayList<RouletteBet> bets = playerBets.get(player);
			double total = 0;
			for (RouletteBet bet : bets) {
				double win = bet.getWin(spinnerVal);
				total += win;
				if (win == 0) {
					Button b = bet.getButton();
					betButtons.remove(b);
					buttons.remove(b);
				}
			}

			player.getPlayer().sendMessage(ConfigUtil.CHAT_ROULETTE_WIN.buildString(total + ""));
			EconomyUtils.playerGiveMoney(player.getPlayer(), total);
		}
		mapManager.renderBoard();
		playerBets.clear();
	}
	
	protected void clearBetButtons() {
		for (Button b : betButtons)
			buttons.remove(b);
		betButtons.clear();
		mapManager.renderBoard();
		
	}

	protected void updateSpinner() {
		spinnerButton.setImage(spinner.getGameImage());
		((RouletteMapManager) mapManager).renderSpinner();
	}

	@Override
	protected void createMapManager(int rotation) {
		mapManager = new RouletteMapManager(mapStructure, rotation, this);
	}

	@Override
	protected void gamePlayerOutOfTime(GamePlayer turn) {
		// TODO Auto-generated method stub

	}

	@Override
	public ItemStack getBoardItem() {
		return new BoardItem(gameName, new ItemStack(Material.BLACK_CARPET, 1));
	}

	public boolean betMade() {
		for (ArrayList<RouletteBet> bets : playerBets.values())
			if (bets.size() > 0)
				return true;

		return false;
	}
	
	@Override
	public void endGame(GamePlayer winner) {
		rouletteStateRunnable.cancel();
		if (spinnerRunnable != null && !spinnerRunnable.isCancelled())
			spinnerRunnable.cancel();
		this.clearBetButtons();
		
		super.endGame(null);
	}
	
	@Override
	public GameNPC getGameNPC() {
		return new RouletteNPC(new double[] { 0.5, -1, 1.5 });
	}

	@Override
	public boolean allowOutsideClicks() {
		return true;
	}
}
