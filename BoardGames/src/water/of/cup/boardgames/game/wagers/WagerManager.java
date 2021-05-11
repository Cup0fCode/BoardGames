package water.of.cup.boardgames.game.wagers;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.GamePlayer;

public class WagerManager {

	private final BoardGames instance = BoardGames.getInstance();
	private final ArrayList<Wager> wagers;
	private final ArrayList<RequestWager> requestWagers;

	public WagerManager() {
		wagers = new ArrayList<>();
		requestWagers = new ArrayList<>();
	}
	
	public void addRequestWager(RequestWager requestWager) {
		requestWager.withdrawInitial();
		requestWagers.add(requestWager);
	}

	public void initGameWager(GamePlayer gameCreator, double amount) {
		Wager gameWager = new Wager(gameCreator.getPlayer(), null, gameCreator, amount);
		instance.getEconomy().withdrawPlayer(gameCreator.getPlayer(), amount);
		wagers.add(gameWager);
	}

	public void addGameWagerPlayer(Player player, Player gameCreator) {
		Wager gameWager = getWager(gameCreator);
		instance.getEconomy().withdrawPlayer(player, gameWager.getAmount());
		gameWager.setPlayer2(player);
	}

	public void endAllWagers() {
		for (Wager wager : wagers) {
			wager.cancel();
		}
		wagers.clear();
	}

	public void endAllRequestWagers() {
		for (RequestWager requestWager : requestWagers) {
			requestWager.cancel();
		}
		requestWagers.clear();
	}

	public void endAll() {
		endAllWagers();
		endAllRequestWagers();
	}

	public void completeWagers(GamePlayer winner) {
		if (winner == null) {
			endAll();
			return;
		}

		endAllRequestWagers();

		for (Wager wager : wagers) {
			wager.complete(winner);
		}
		wagers.clear();
	}

	public void acceptRequestWager(Player player, RequestWager requestWager) {
		Wager wager = requestWager.createWager(player);

		wagers.add(wager);
		requestWagers.remove(requestWager);
	}

	public Wager getWager(Player player) {
		for(Wager wager : wagers) {
			if(wager.getPlayer1() != null && wager.getPlayer1().equals(player))
				return wager;
			if(wager.getPlayer2() != null && wager.getPlayer2().equals(player))
				return wager;
		}

		return null;
	}

	public ArrayList<RequestWager> getRequestWagers() {
		return requestWagers;
	}

	public boolean hasRequestWager(Player player) {
		for(RequestWager requestWager : requestWagers) {
			if(requestWager.getOwner().equals(player)) return true;
		}

		return false;
	}

	public RequestWager getRequestWager(Player player) {
		for(RequestWager requestWager : requestWagers) {
			if(requestWager.getOwner().equals(player)) return requestWager;
		}

		return null;
	}

	public void cancelRequestWager(RequestWager requestWager) {
		requestWager.cancel();
		requestWagers.remove(requestWager);
	}
}
