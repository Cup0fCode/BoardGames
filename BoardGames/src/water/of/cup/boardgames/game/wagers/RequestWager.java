package water.of.cup.boardgames.game.wagers;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.game.GamePlayer;

public class RequestWager {

	private final BoardGames instance = BoardGames.getInstance();
	private final Player owner;
	private final GamePlayer ownerBet;
	private final double amount;

	public RequestWager(Player owner, GamePlayer playerBettingOn, double amount) {
		this.owner = owner;
		this.ownerBet = playerBettingOn;
		this.amount = amount;
	}

	public Wager createWager(Player otherPlayer) {
		instance.getEconomy().withdrawPlayer(otherPlayer, amount);

		return new Wager(owner, otherPlayer, ownerBet, amount);
	}

	public Player getOwner() {
		return owner;
	}

	public GamePlayer getOwnerBet() {
		return ownerBet;
	}

	public double getAmount() {
		return amount;
	}

	public void cancel() {
		instance.getEconomy().depositPlayer(owner, amount);
	}

	public boolean canCreate() {
		return (instance.getEconomy().getBalance(owner) >= amount);
	}

	public boolean canAccept(Player accepter) {
		return (instance.getEconomy().getBalance(accepter) >= amount);
	}

	public void withdrawInitial() {
		instance.getEconomy().withdrawPlayer(owner, amount);
	}
}

