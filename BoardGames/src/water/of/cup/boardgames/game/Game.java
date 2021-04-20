package water.of.cup.boardgames.game;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;

import water.of.cup.boardgames.game.wagers.WagerManager;

public abstract class Game {
	private int gameId;
	private String gameName;
	private MapManager mapManager;
	private HashMap<Player, GamePlayer> players;
	private int turn;
	private BufferedImage boardImage;
	private ArrayList<Button> buttons;
	private WagerManager wagerManager;
	private Clock clock;
	
	abstract public void click(Player player, int[] location);
	
	public void cancelGame() {
		wagerManager.endAll();
	}
	
	private Button getClickedButton(GamePlayer gamePlayer, int[] loc) {
		for (Button button : buttons) {
		
		}
		
		return null;
	}
	
	public void renderMaps() {
		// TODO: render maps using mapManager
	}
	
	public boolean hasPlayer(Player player) {
		return players.containsKey(player);
	}

	public ArrayList<Game> getPlayerQueue() {
		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<Game> getPlayerDecideQueue() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getGameId() {
		return gameId;
	}

	public GamePlayer getTurn() {
		return ((List<GamePlayer>) players.values()).get(turn);
	}

	private void setTurn(int turn) {
		this.turn = turn;
	}
	
	private void setTurn(GamePlayer gamePlayer) {
		turn = ((List<GamePlayer>) players.values()).indexOf(gamePlayer);
	}
	
	private void setTurn(Player player) {
		setTurn(players.get(player));
	}
	
	private Player getPlayer(GamePlayer player) {
		for (Player p : players.keySet()) {
			if (players.get(p).equals(player))
				return p;
		}
		return null;
	}

	public ArrayList<GamePlayer> getGamePlayers() {
		return (ArrayList<GamePlayer>) players.values();
	}

	protected abstract void gamePlayerOutOfTime(GamePlayer turn);

}
