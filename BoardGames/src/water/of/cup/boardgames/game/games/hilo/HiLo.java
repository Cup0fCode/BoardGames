package water.of.cup.boardgames.game.games.hilo;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.game.*;
import water.of.cup.boardgames.game.games.gameutils.cards.Card;
import water.of.cup.boardgames.game.games.gameutils.cards.Deck;
import water.of.cup.boardgames.game.inventories.GameInventory;
import water.of.cup.boardgames.game.storage.GameStorage;
import water.of.cup.boardgames.config.ConfigUtil;

import java.util.ArrayList;

public class HiLo extends Game {
	private BoardGames instance = BoardGames.getInstance();
	private Card[] cards;
	private Deck deck;
	private Button[] cardButtons;
	private Button[] hiButtons;
	private Button[] lowButtons;
	private Button cashOutButton;
	private double houseEdge = 0.025;
	private double bet;
	private Button lastCardButton;

	public HiLo(int rotation) {
		super(rotation);
		initailDraw();
		createButtons();
	}

	private void createButtons() {
		cardButtons = new Button[3];
		hiButtons = new Button[3];
		lowButtons = new Button[3];
		cashOutButton = new Button(this, "HILO_CASHOUT", new int[] { 90, 50 }, 0, "cashOut");
		cashOutButton.setClickable(true);
		buttons.add(cashOutButton);
		lastCardButton = new Button(this, deck.draw().getGameImage(), new int[] { 10, 60 }, 0, "lastCard");
		buttons.add(lastCardButton);
		for (int i = 0; i < 3; i++) {
			cardButtons[i] = new Button(this, cards[i].getGameImage(), new int[] { 39 + i * 17, 70 }, 0, "card");
			buttons.add(cardButtons[i]);
			hiButtons[i] = new Button(this, "HILO_POINTER", new int[] { 39 + 2 + i * 17, 80 - 22}, 0, "hi");
			hiButtons[i].setClickable(true);
			buttons.add(hiButtons[i]);
			lowButtons[i] = new Button(this, "HILO_POINTER", new int[] { 39 + 2 + i * 17, 80 + 12}, 2, "lo");
			lowButtons[i].setClickable(true);
			buttons.add(lowButtons[i]);
		}
	}

	@Override
	protected void startGame() {
		bet = (int) this.gameInventory.getGameData("betAmount"); // TODO: get initial bet
		if (instance.getEconomy().getBalance(teamManager.getTurnPlayer().getPlayer()) < bet) {
			teamManager.getTurnPlayer().getPlayer().sendMessage(water.of.cup.boardgames.config.ConfigUtil.CHAT_GUI_GAME_NO_MONEY_CREATE.toString());
			clearGamePlayers();
			endGame(null);
			return;
		}

		instance.getEconomy().withdrawPlayer(teamManager.getTurnPlayer().getPlayer(), bet);
		
		initailDraw();
		setButtons();
		mapManager.renderBoard();

	}

	private void setButtons() {
		for (int i = 0; i < 3; i++) {
			cardButtons[i].setImage(cards[i].getGameImage());
			lowButtons[i].setVisibleForAll(cards[i].getValue(false) != 1);
			hiButtons[i].setVisibleForAll(cards[i].getValue(false) != 13);
		}
	}

	private void initailDraw() {
		deck = new Deck(1);
		cards = new Card[3];
		for (int i = 0; i < 3; i++) {
			cards[i] = deck.draw();
		}
	}

	private void hilo(int cardNum, boolean high) {
		// high = true for high, false for low
		Card nextCard = deck.draw();
		int nextCardValue = nextCard.getValue(false);
		int cardValue = cards[cardNum].getValue(false);
		Card lastCard = cards[cardNum];

		if ((high == nextCardValue > cardValue) && nextCardValue != cardValue) {
			// win
			double betIncrease = getBetIncrease(cardValue, high);
			bet *= betIncrease;
			teamManager.getTurnPlayer().getPlayer().sendMessage(ConfigUtil.CHAT_HILO_CURRENT_BET.buildString(bet + ""));
			
		} else {
			// loss
			teamManager.getTurnPlayer().getPlayer().sendMessage(ConfigUtil.CHAT_HILO_LOSE.buildString(bet + ""));
			endGame(null);
		}
		
		cards[cardNum] = nextCard;
		setButtons();
		lastCardButton.setImage(lastCard.getGameImage());
		mapManager.renderBoard();

	}

	private void cashOut() {
		teamManager.getTurnPlayer().getPlayer().sendMessage(ConfigUtil.CHAT_HILO_WIN.buildString(bet + ""));
		instance.getEconomy().depositPlayer(teamManager.getTurnPlayer().getPlayer(), bet);
		endGame(teamManager.getTurnPlayer());
	}

	private double getBetIncrease(int cardValue, boolean high) {
		double increase = cardValue;
		if (!high)
			increase = 14 - cardValue;
		increase /= 13;
		increase *= 1 - houseEdge;
		return 1.0 + increase;
	}

	@Override
	protected void setMapInformation(int rotation) {
		this.mapStructure = new int[][] { { 1 } };
		this.placedMapVal = 1;
	}

	@Override
	protected void setGameName() {
		this.gameName = "Hi-Lo";
	}

	@Override
	protected void setBoardImage() {
		this.gameImage = new GameImage("HILO_BOARD");
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
		return new HiLoInventory(this);
	}

	@Override
	protected GameStorage getGameStorage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<String> getTeamNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected GameConfig getGameConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void click(Player player, double[] loc, ItemStack map) {
		GamePlayer gamePlayer = getGamePlayer(player);
		if (!teamManager.getTurnPlayer().equals(gamePlayer))
			return;

		int[] clickLoc = mapManager.getClickLocation(loc, map);

		Button b = getClickedButton(gamePlayer, clickLoc);
		if (b == null)
			return;

		if (b == cashOutButton) {
			cashOut();
			return;
		}

		for (int i = 0; i < 3; i++) {
			boolean high = false;
			if (b == hiButtons[i])
				high = true;
			else if (b != lowButtons[i])
				continue;
			hilo(i, high);
			return;
		}
	}

	@Override
	protected void gamePlayerOutOfTime(GamePlayer turn) {
		// TODO Auto-generated method stub

	}

	@Override
    public ItemStack getBoardItem() {
        return new BoardItem(gameName, new ItemStack(Material.ACACIA_TRAPDOOR, 1));
    }

}
