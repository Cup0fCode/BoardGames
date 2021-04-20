package water.of.cup.boardgames.game.wagers;

import org.bukkit.entity.Player;

import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.game.GamePlayer;



public class Wager {
	private BoardGames instance = BoardGames.getInstance();
	
	Player player1;
	Player player2;

	GamePlayer player1Bet;

	double amount;
	
	public Wager(Player player1, Player player2, GamePlayer ownerBet, double amount) {
		
		instance.getEconomy().withdrawPlayer(player2, amount);
		instance.getEconomy().withdrawPlayer(player1, amount);
		
		this.player1 = player1;
		this.player2 = player2;
		
		this.player1Bet = ownerBet;
		
		this.amount = amount;
	}
	
	public Wager(RequestWager requestWager, Player accepter) {
		player1 = requestWager.getOwner();
		player2 = accepter;
		
		player1Bet = requestWager.getOwnerBet();
		
		amount = requestWager.getAmount();
		
		instance.getEconomy().withdrawPlayer(player2, amount);
	}
	
//	public Wager(String wagerString) {
//		for (String arg : wagerString.split("&")) {
//
//			String key = arg.substring(0, arg.indexOf(":"));
//			String result = arg.substring(arg.indexOf(":") + 1);
//
//			if (key.equals("Player1")) {
//				player1 = Bukkit.getPlayer(UUID.fromString(result));
//				continue;
//			}
//			if (key.equals("Player2")) {
//				player2 = Bukkit.getPlayer(UUID.fromString(result));
//				continue;
//			}
//			if (key.equals("Player1Side")) {
//				player1Bet = result;
//				continue;
//			}
//			if (key.equals("Amount")) {
//				amount = Double.valueOf(result);
//				continue;
//			}
//		}
//	}

	public void complete(GamePlayer winner) {
		if(instance.getEconomy() == null) return;
		if (winner == null)
			cancel();

//		if (!(winningColor.equals("WHITE") || winningColor.equals("BLACK"))) {
//			//give players their money back
//			instance.getEconomy().depositPlayer(player1, amount);
//			instance.getEconomy().depositPlayer(player2, amount);
//			return;
//		}
			

		if (winner.equals(player1Bet)) {
			//player1 won
			instance.getEconomy().depositPlayer(player1, amount * 2);
		} else {
			//player2 won
			instance.getEconomy().depositPlayer(player2, amount * 2);
		}
	}

	public void cancel() {
		instance.getEconomy().depositPlayer(player1, amount);
		instance.getEconomy().depositPlayer(player2, amount);		
	}
	
//	public String toString() {
//		String wagerString = "";
//		wagerString += "Player1:" + player1.getUniqueId().toString() + "&";
//		wagerString += "Player2:" + player2.getUniqueId().toString() + "&";
//		
//		wagerString += "Player1Side:" + player1Bet.getPlayer().getName() + "&";
//		
//		wagerString += "Amount:" + amount + "&";
//		
//		return wagerString;
//	}
}
