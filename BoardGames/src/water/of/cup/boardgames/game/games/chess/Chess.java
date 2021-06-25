package water.of.cup.boardgames.game.games.chess;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import water.of.cup.boardgames.game.*;
import water.of.cup.boardgames.game.inventories.GameInventory;
import water.of.cup.boardgames.game.storage.GameStorage;
import water.of.cup.boardgames.game.storage.StorageType;

public class Chess extends Game {
	private ChessBoard board;
	private Button[][] boardButtons;
	private int[] selected;
	private String promotion;
	private Button[] promotionButtons;

	private String[] promotionNames = new String[] { "ROOK", "KNIGHT", "BISHOP", "QUEEN" };

	public Chess(int rotation) {
		super(rotation);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void setMapInformation(int rotation) {
		this.mapStructure = new int[][] { { 1 } };
		this.placedMapVal = 1;
	}

	@Override
	protected void startGame() {
		selected = null;
		setInGame();
		promotion = "NONE";
		buttons.clear();
		board = new ChessBoard();
		boardButtons = new Button[8][8];
		for (int x = 0; x < 8; x++)
			for (int y = 0; y < 8; y++) {
				Button b = new Button(this, "CHESS_EMPTY", new int[] { x * 16, y * 16 }, 0, "PIECE");
				b.setClickable(true);
				boardButtons[y][x] = b;
				buttons.add(b);
			}

		promotionButtons = new Button[4];
		for (int i = 0; i < 4; i++) {
			Button b = new Button(this, "CHESS_EMPTY", new int[] { 24 + i * 16, 56 }, 0, "PROMOTION");
			b.setClickable(false);
			promotionButtons[i] = b;
			buttons.add(b);
		}
		paintBoard();

	}

	private void togglePromotionButtons() {
		for (int i = 0; i < 4; i++)
			if (promotion == "NONE") {
				promotionButtons[i].setClickable(false);
				promotionButtons[i].setVisibleForAll(false);
			} else {
				ChessPiece piece = ChessPiece.valueOf(promotion + "_" + promotionNames[i]);
				promotionButtons[i].setImage(piece.getImage());
				promotionButtons[i].setClickable(true);
				promotionButtons[i].setVisibleForAll(true);
			}
	}

	private int[] getButtonLocation(Button b) {
		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 8; x++) {
				if (b == boardButtons[y][x])
					return new int[] { x, y };
			}
		}
		return null;
	}

	private int getPromotionButtonLocation(Button b) {
		for (int i = 0; i < 4; i++) {
			if (b == promotionButtons[i])
				return i;

		}
		return -1;
	}

	@Override
	protected void setGameName() {
		this.gameName = "Chess";

	}

	@Override
	protected void setBoardImage() {
		this.gameImage = new GameImage("CHESS_BOARD");
	}

	private void paintBoard() {
		boolean[][] moves = new boolean[8][8];
		if (selected != null && board.getStructure()[selected[1]][selected[0]] != null) {
			moves = board.getMoves(selected);
		}

		ChessPiece[][] structure = board.getStructure();

		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 8; x++) {
				Button b = boardButtons[y][x];
				b.setImage("CHESS_EMPTY");
				if (selected != null && selected[0] == x && selected[1] == y)
					b.setImage("CHESS_SELECTED");
				if (moves[y][x])
					b.setImage("CHESS_MOVE");
				ChessPiece piece = structure[y][x];
				if (piece != null)
					b.getImage().addGameImage(new GameImage(piece.getImage(), 0), new int[] { 0, 0 });
			}
		}

		togglePromotionButtons();
		mapManager.renderBoard();
	}

	@Override
	protected void startClock() {
		// TODO Auto-generated method stub

	}

	@Override
	protected GameInventory getGameInventory() {
		// TODO Auto-generated method stub
		return new ChessInventory(this);
	}

	@Override
	public ArrayList<String> getTeamNames() {
		ArrayList<String> names = new ArrayList<String>();
		names.add("WHITE");
		names.add("BLACK");
		return names;
	}

	@Override
	public void click(Player player, double[] loc, ItemStack map) {
		GamePlayer gamePlayer = getGamePlayer(player);
		if (!teamManager.getTurnPlayer().equals(gamePlayer))
			return;

		int[] clickLoc = mapManager.getClickLocation(loc, map);
		Button b = getClickedButton(gamePlayer, clickLoc);

		String teamTurn = teamManager.getTurnTeam();

		if (!board.getPawnPromotion().equals("NONE")) {
			int p = getPromotionButtonLocation(b);
			if (p == -1)
				return; // none clicked
			board.promotePawn(teamTurn, ChessPiece.valueOf(promotion + "_" + promotionNames[p]));
			promotion = "NONE";
			teamManager.nextTurn();
			paintBoard();
			return;
		}

		int[] position = getButtonLocation(b);

		if (position == null)
			return;

		if (selected == null) {
			ChessPiece piece = board.getStructure()[position[1]][position[0]];
			if (piece != null && piece.getColor().equals(teamTurn))
				selected = position;
			paintBoard();
			return;
		}

		if (board.move(selected, position, teamTurn)) {
			// move works
			selected = null;

			this.playGameSound("click");

			if (!board.checkGameOver().equals("")) {
				// game over
				GamePlayer playerWinner = teamManager.getGamePlayerByTeam(board.checkGameOver());
				paintBoard();
				endGame(playerWinner);
				Bukkit.getLogger().info(board.checkGameOver());
				return;
			}

			if (board.getPawnPromotion().equals("NONE")) {
				// no pawn promotion
				teamManager.nextTurn();
				paintBoard();
				return;
			} else {
				// pawn promotion
				promotion = board.getPawnPromotion();
				paintBoard();
				return;
			}

		} else {
			ChessPiece piece = board.getStructure()[position[1]][position[0]];
			if (piece != null && piece.getColor().equals(teamTurn)) {
				selected = position;
				paintBoard();
				return;
			}
		}

	}

	@Override
	protected void gamePlayerOutOfTime(GamePlayer turn) {
		// TODO Auto-generated method stub

	}

	@Override
	public ItemStack getBoardItem() {
		return new BoardItem(gameName, new ItemStack(Material.OAK_TRAPDOOR, 1));
	}

	@Override
	protected GameConfig getGameConfig() {
		return new ChessConfig(this);
	}

	@Override
	protected GameStorage getGameStorage() {
		return new ChessStorage(this);
	}

	public void endGame(GamePlayer gamePlayerWinner) {
		updateGameStorage(gamePlayerWinner);

		String message;
		if(gamePlayerWinner != null) {
			message = gamePlayerWinner.getPlayer().getDisplayName() + " has won the game!";
		} else {
			message = ChatColor.GREEN + "Tie game!";
		}

		for(GamePlayer player : teamManager.getGamePlayers()) {
			player.getPlayer().sendMessage(message);
		}

		super.endGame(gamePlayerWinner);
	}

	private void updateGameStorage(GamePlayer gamePlayerWinner) {
		if(!hasGameStorage()) return;

		if(gamePlayerWinner == null) {
			for(GamePlayer player : teamManager.getGamePlayers()) {
				gameStorage.updateData(player.getPlayer(), StorageType.TIES, 1);
			}
		} else {
			GamePlayer gamePlayerLoser = teamManager.getGamePlayers().get(0).equals(gamePlayerWinner)
					? teamManager.getGamePlayers().get(1)
					: teamManager.getGamePlayers().get(0);

			gameStorage.updateData(gamePlayerWinner.getPlayer(), StorageType.WINS, 1);
			gameStorage.updateData(gamePlayerLoser.getPlayer(), StorageType.LOSSES, 1);
		}
	}
}
