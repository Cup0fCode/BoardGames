package water.of.cup.boardgames.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.GameManager;

public class bgCommands implements CommandExecutor {
	BoardGames instance = BoardGames.getInstance();
	GameManager gameManager = instance.getGameManager();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		if (!(sender instanceof Player)) {
			return true;
		}

		Player p = (Player) sender;

		if (cmd.getName().equalsIgnoreCase("bg")) {

//			if (!p.isOp())
//				return false;

			if (args.length == 0) {
				p.sendMessage("Available commands");
				p.sendMessage("/bg games - lists games");
				p.sendMessage("/bg board [game name] - gives you the game's item");
				return true;
			}

			if (args[0].equalsIgnoreCase("games")) {
				p.sendMessage("Game names: ");
				for (String name : gameManager.getGameNames()) 
					p.sendMessage(name);
				
			} else if (args[0].equalsIgnoreCase("board")) {
				if (args.length == 2) {
					Game game = gameManager.newGame(args[1], 0);
					if (game != null) {
						p.getWorld().dropItem(p.getLocation(), game.getBoardItem());
					}
				}
				//instance.getDataStore().getCasinoPlayers().get(p).setBloodAlcoholContent(0);

			} 
			return true;
		}

		return false;
	}

}
