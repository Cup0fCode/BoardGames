package water.of.cup.boardgames.game.wagers;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.game.GamePlayer;

public class RequestWager {
	private BoardGames instance = BoardGames.getInstance();

	Player owner;

	GamePlayer ownerBet;

	double amount;

	public RequestWager(Player owner, GamePlayer playerBettingOn, double amount) {
		this.owner = owner;
		this.ownerBet = playerBettingOn;
		this.amount = amount;
		
//		instance.getEconomy().withdrawPlayer(owner, amount);
	}

	public Wager createWager(Player otherPlayer) {
//		if (instance.getEconomy().getBalance(otherPlayer) < amount) {
//			otherPlayer.sendMessage(ChatColor.RED + "You do not have enough money to accept this wager.");
//			return null;
//		}
		
		return new Wager(owner, otherPlayer, ownerBet, amount);
	}

	public Player getOwner() {
		return owner;
	}

	public GamePlayer getOwnerBet() {
		return ownerBet;
	}

//	public String getOponentColor() {
//		if (ownerSide.equals("WHITE"))
//			return "BLACK";
//		return "WHITE";
//	}

	public double getAmount() {
		return amount;
	}

	public void cancel() {
		instance.getEconomy().depositPlayer(owner, amount);
	}
}

