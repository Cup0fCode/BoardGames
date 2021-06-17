package water.of.cup.boardgames.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.config.ConfigUtil;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.GameManager;
import water.of.cup.boardgames.game.storage.GameStorage;
import water.of.cup.boardgames.game.storage.StorageType;

import java.util.LinkedHashMap;

public class bgCommands implements CommandExecutor {

	private final BoardGames instance = BoardGames.getInstance();
	private final GameManager gameManager = instance.getGameManager();
	
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
				sendHelpMessage(p);
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

			} else if (args[0].equalsIgnoreCase("stats")) {
				if(args.length != 3) {
					sendHelpMessage(p);
					return false;
				}

				if(!instance.hasStorage()) {
					p.sendMessage(ConfigUtil.CHAT_NO_DB.toString());
					return false;
				}

				String gameName = args[1];
				String playerName = args[2];

				Game tempGame = gameManager.newGame(gameName, 0);
				Player player = Bukkit.getPlayer(playerName);

				if(player == null) {
					p.sendMessage(ConfigUtil.CHAT_NO_PLAYER.toString());
					return false;
				}

				if(tempGame == null) {
					p.sendMessage(ConfigUtil.CHAT_NO_GAME.toString());
					return false;
				}

				if(!tempGame.hasGameStorage()) {
					p.sendMessage(ConfigUtil.CHAT_NO_DB.toString());
					return false;
				}

				LinkedHashMap<StorageType, Object> playerStats = instance.getStorageManager().fetchPlayerStats(player, tempGame.getGameStore());
				if(playerStats == null) {
					p.sendMessage(ConfigUtil.CHAT_DB_ERROR.toString());
					return false;
				}

				p.sendMessage(player.getName() + "'s " + tempGame.getName() + " stats");
				for(StorageType storageType : playerStats.keySet()) {
					p.sendMessage(storageType.getKey() + " : " + playerStats.get(storageType));
				}

				return true;
			} else if (args[0].equalsIgnoreCase("leaderboard")) {
				if(args.length < 2) {
					sendHelpMessage(p);
					return false;
				}

				if(!instance.hasStorage()) {
					p.sendMessage(ConfigUtil.CHAT_NO_DB.toString());
					return false;
				}

				String gameName = args[1];
				Game tempGame = gameManager.newGame(gameName, 0);

				if(tempGame == null) {
					p.sendMessage(ConfigUtil.CHAT_NO_GAME.toString());
					return false;
				}

				if(!tempGame.hasGameStorage()) {
					p.sendMessage(ConfigUtil.CHAT_NO_DB.toString());
					return false;
				}

				GameStorage gameStorage = tempGame.getGameStore();
				StorageType orderBy = gameStorage.getStorageTypes().get(0);

				if(args.length > 2) {
					String orderByType = args[2];
					for(StorageType storageType : StorageType.values()) {
						if(storageType.getKey().equalsIgnoreCase(orderByType)) {
							orderBy = storageType;
							break;
						}
					}
				}

				LinkedHashMap<Player, LinkedHashMap<StorageType, Object>> topPlayers = instance.getStorageManager().fetchTopPlayers(gameStorage, orderBy, 0);
				int count = 1;

				p.sendMessage(tempGame.getName() + " leaderboard sorting by " + orderBy.getKey());
				for(Player player : topPlayers.keySet()) {
					p.sendMessage("#" + count + ". " + player.getName() + " - " + topPlayers.get(player).get(orderBy));
					count++;
				}
			}
			return true;
		}

		return false;
	}

	private void sendHelpMessage(Player p) {
		p.sendMessage("Available commands");
		p.sendMessage("/bg games - lists games");
		p.sendMessage("/bg board [game name] - gives you the game's item");
		p.sendMessage("/bg stats [game name] [player name]");
		p.sendMessage("/bg leaderboard [game name] [order by]");
	}

}
