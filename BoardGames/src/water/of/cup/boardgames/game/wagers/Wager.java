package water.of.cup.boardgames.game.wagers;

import org.bukkit.entity.Player;

import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.game.GamePlayer;



public class Wager {

	private final BoardGames instance = BoardGames.getInstance();
	private final Player player1;
	private Player player2;
	private final GamePlayer player1Bet;
	private final double amount;
	
	public Wager(Player player1, Player player2, GamePlayer ownerBet, double amount) {
		this.player1 = player1;
		this.player2 = player2;
		this.player1Bet = ownerBet;
		this.amount = amount;
	}

	public void complete(GamePlayer winner) {
		if (winner == null) {
			cancel();
			return;
		}

		if (winner.equals(player1Bet)) {
			//player1 won
			instance.getEconomy().depositPlayer(player1, amount * 2);
		} else if(player2 != null) {
			//player2 won
			instance.getEconomy().depositPlayer(player2, amount * 2);
		}
	}

	public void cancel() {
		instance.getEconomy().depositPlayer(player1, amount);

		// Players maybe be null due to game wager
		if(player2 != null)
			instance.getEconomy().depositPlayer(player2, amount);
	}

	public Player getPlayer1() {
		return player1;
	}

	public Player getPlayer2() {
		return player2;
	}

	public double getAmount() {
		return this.amount;
	}

	public void setPlayer2(Player player) {
		this.player2 = player;
	}
}
