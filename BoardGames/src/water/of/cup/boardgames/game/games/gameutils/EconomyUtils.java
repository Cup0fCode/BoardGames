package water.of.cup.boardgames.game.games.gameutils;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import water.of.cup.boardgames.BoardGames;

public class EconomyUtils {
	private static BoardGames instance = BoardGames.getInstance();
	private static Economy economy = instance.getEconomy();
	
	public static boolean playerHasFunds(Player player, double amount) {
		return (economy != null && economy.getBalance(player) > amount);
	}
	
	public static boolean playerTakeMoney(Player player, double amount) {
		if (!playerHasFunds(player, amount))
			return false;
		economy.withdrawPlayer(player, amount);
		return true;
	}
	
	public static boolean playerGiveMoney(Player player, double amount) {
		economy.depositPlayer(player, amount);
		return true;
	}
}
