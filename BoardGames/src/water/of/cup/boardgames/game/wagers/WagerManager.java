package water.of.cup.boardgames.game.wagers;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.game.GamePlayer;

public class WagerManager {

	private final BoardGames instance = BoardGames.getInstance();
	private final ArrayList<EconomyWager> economyWagers;
	private final ArrayList<RequestWager> requestWagers;
	private final ArrayList<Wager> wagers;

	public WagerManager() {
		economyWagers = new ArrayList<>();
		requestWagers = new ArrayList<>();
		wagers = new ArrayList<>();
	}
	
	public void addRequestWager(RequestWager requestWager) {
		requestWager.withdrawInitial();
		requestWagers.add(requestWager);
	}

	public void initGameWager(GamePlayer gameCreator, double amount) {
		EconomyWager gameWager = new EconomyWager(gameCreator.getPlayer(), null, gameCreator, amount);
		instance.getEconomy().withdrawPlayer(gameCreator.getPlayer(), amount);
		economyWagers.add(gameWager);
		wagers.add(gameWager);
	}

	public void addGameWagerPlayer(Player player, Player gameCreator) {
		EconomyWager gameWager = getWager(gameCreator);
		instance.getEconomy().withdrawPlayer(player, gameWager.getAmount());
		gameWager.setPlayer2(player);
	}

	public void addWager(Wager wager) {
		wagers.add(wager);
	}

	public void endAllWagers() {
		for (Wager wager : wagers) {
			wager.cancel();
		}

		wagers.clear();
		economyWagers.clear();
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
		economyWagers.clear();
	}

	public void acceptRequestWager(Player player, RequestWager requestWager) {
		EconomyWager wager = requestWager.createWager(player);

		wagers.add(wager);
		economyWagers.add(wager);
		requestWagers.remove(requestWager);
	}

	public EconomyWager getWager(Player player) {
		for(EconomyWager wager : economyWagers) {
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
