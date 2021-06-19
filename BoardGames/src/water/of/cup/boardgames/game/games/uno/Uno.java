package water.of.cup.boardgames.game.games.uno;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import water.of.cup.boardgames.config.GameRecipe;
import water.of.cup.boardgames.game.*;
import water.of.cup.boardgames.game.inventories.GameInventory;
import water.of.cup.boardgames.game.storage.GameStorage;

public class Uno extends Game {
	private UnoDeck deck;

	private HashMap<GamePlayer, Integer> playerBoardPosition; // [0,7]
	private HashMap<GamePlayer, UnoHand> playerHands;
	private HashMap<GamePlayer, ArrayList<Button>> playerCardButtons;
	
	private UnoCard currentCard;

	public Uno(int rotation) {
		super(rotation);
	}

	@Override
	protected void setMapInformation(int rotation) {
		this.mapStructure = new int[][] { { 3, 4 }, { 1, 2 } };
		this.placedMapVal = 1;
	}

	@Override
	protected void startGame() {
		deck = new UnoDeck();
		playerBoardPosition = new HashMap<GamePlayer, Integer>();
		playerCardButtons = new HashMap<GamePlayer, ArrayList<Button>>();
		createPlayerHands();
		
		currentCard = deck.drawCards(1).get(0);
		mapManager.renderBoard();
	}

	private void createPlayerHands() {
		int posCounter = 0;
		for (GamePlayer player : super.getGamePlayers()) {
			playerBoardPosition.put(player, posCounter);
			playerHands.put(player, new UnoHand());
			playerDrawCards(player, 7); // draw 7 cards at game start
			playerCardButtons.put(player, new ArrayList<Button>());
			setCardButtons(player);
			
			
			// pos maths
			int rotation = posCounter / 2;
			int[] loc = new int[] { 32, 95 };
			for (int i = 0; i < rotation; i++) { // rotate initial cords
				loc = MathUtils.rotatePointAroundPoint90Degrees(new double[] {63.5, 63,5}, loc);
			}
			loc[0] += (1 - (posCounter + 1) / 4) * 128; // set X cords
			loc[1] += (1 - (posCounter - 3) / 4) * 128; // set Y cords
			
			// create position image
			Button b =  new Button(this, "UNO_DECK", loc, rotation, "deck");
			buttons.add(b);
			b.setClickable(false);
					
			posCounter++; // TODO: do maths to make player positions evenly spaced
			
			setCardButtons(player);
		}

	}

	private void playerDrawCards(GamePlayer player, int amount) {
		UnoHand hand = playerHands.get(player);
		hand.draw(deck, amount);
	}

	private void setCardButtons(GamePlayer player) { // TODO: include page
		ArrayList<Button> cardButtons = playerCardButtons.get(player);
		buttons.removeAll(cardButtons);
		playerCardButtons.clear();

		UnoHand hand = playerHands.get(player);
		int boardPos = playerBoardPosition.get(player);

		// TODO : add loc rotation maths
		int rotation = boardPos / 2;
		
		int[] iLoc = new int[] { 33, 96 };
		
		for (int i = 0; i < rotation; i++) { // rotate initial cords
			iLoc = MathUtils.rotatePointAroundPoint90Degrees(new double[] {63.5, 63,5}, iLoc);
		}
		
		iLoc[0] += (1 - (boardPos + 1) / 4) * 128; // set X cords
		iLoc[1] += (1 - (boardPos - 3) / 4) * 128; // set Y cords

		int handPos = 0;
		for (UnoCard card : hand.getCards()) {
			int[] cLoc = new int[] { (handPos % 7) * 9, (handPos / 7) * 15 };
			
			for (int i = 0; i < rotation; i++) { // rotate change cords
				cLoc = MathUtils.rotatePointAroundPoint90Degrees(new double[] {63.5, 63,5}, cLoc);
			}
			
			int[] loc = new int[] { iLoc[0] + cLoc[0], iLoc[1] + cLoc[1] };

			Button cardButton = new Button(this, card.getGameImage(), loc, rotation, "" + handPos);
			cardButton.setVisibleForAll(false);
			cardButton.addVisiblePlayer(player);
			cardButtons.add(cardButton);
			
			handPos++;
		}

		buttons.addAll(cardButtons);
	}
	
	private UnoCard getSelectedCard(GamePlayer player, Button b) {
		ArrayList<Button> cardButtons = playerCardButtons.get(player);
		int index = cardButtons.indexOf(b);
		UnoHand hand = playerHands.get(player);
		return hand.getCards().get(index);
	}
	
	private boolean playCard(GamePlayer player, UnoCard card) {
		if (card.matches(currentCard)) {
			UnoHand hand = playerHands.get(player);
			hand.removeCard(card);
			if (hand.cardsLeft() == 1) {
				// say uno
			} else if (hand.cardsLeft() == 0){
				// player won
			} 
			
			currentCard = card;
			setCardButtons(player);
			
			
			doCardActions(card); //changes the turn
			
			
			return true;
		} else {
			return false;
		}
	}
	
	private void doCardActions(UnoCard card) {
		// TODO do card actions with messages
		// & next turn
		
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
	protected GameStorage getGameStorage() {
		return null;
	}

	@Override
	public ArrayList<String> getTeamNames() {
		return null;
	}

	@Override
	protected GameConfig getGameConfig() {
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
