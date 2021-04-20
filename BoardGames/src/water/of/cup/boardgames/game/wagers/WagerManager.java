package water.of.cup.boardgames.game.wagers;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import water.of.cup.boardgames.game.GamePlayer;

public class WagerManager {
	private ArrayList<Wager> wagers;
	private ArrayList<RequestWager> requestWagers;

	public WagerManager() {
		wagers = new ArrayList<Wager>();
		requestWagers = new ArrayList<RequestWager>();
	}
	
	public void addWager(Wager wager) {
		wagers.add(wager);
	}
	
	public void addRequestWager(RequestWager requestWager) {
		requestWagers.add(requestWager);
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

	public boolean acceptRequestWager(Player player, RequestWager requestWager) {
		Wager wager = requestWager.createWager(player);
		if (wager == null)
			return false;

		wagers.add(wager);
		requestWagers.remove(requestWager);

		return true;
	}

}
