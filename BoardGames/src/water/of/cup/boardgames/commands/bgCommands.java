package water.of.cup.boardgames.commands;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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
		boolean permissions = ConfigUtil.PERMISSIONS_ENABLED.toBoolean();

		if (cmd.getName().equalsIgnoreCase("chessboards")) {
			if(permissions && !p.hasPermission("chessboard.command"))
				return false;

			if (args.length == 0) {
				sendHelpMessage(p);
				return true;
			}

			if (args[0].equalsIgnoreCase("give")) {
				if(permissions && !p.hasPermission("chessboard.command.give"))
					return false;

				Game game = gameManager.newGame("Chess", 0);
				if (game != null) {
					p.getWorld().dropItem(p.getLocation(), game.getBoardItem());
				}
			} else if (args[0].equalsIgnoreCase("stats")) {
				if(permissions && !p.hasPermission("chessboard.command.stats"))
					return false;

				if(args.length != 2) {
					sendHelpMessage(p);
					return false;
				}

				if(!instance.hasStorage()) {
					p.sendMessage(ConfigUtil.CHAT_NO_DB.toString());
					return false;
				}

				String gameName = "Chess";
				String playerName = args[1];

				Game tempGame = gameManager.newGame(gameName, 0);
				OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);

				if(!player.hasPlayedBefore()) {
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

				LinkedHashMap<StorageType, Object> playerStats = instance.getStorageManager().fetchPlayerStats(player, tempGame.getGameStore(), true);
				if(playerStats == null) {
					p.sendMessage(ConfigUtil.CHAT_NO_PLAYER.toString());
					return false;
				}

				p.sendMessage(ConfigUtil.CHAT_STATS_HEADER.buildStringPlayerGame(player.getName(), tempGame.getName()));
				for(StorageType storageType : playerStats.keySet()) {
					p.sendMessage(ConfigUtil.CHAT_STATS_FORMAT.buildStatsFormat(StringUtils.capitalize(storageType.getKey()), playerStats.get(storageType) + ""));
				}

				return true;
			} else if (args[0].equalsIgnoreCase("leaderboard")) {
				if(permissions && !p.hasPermission("chessboard.command.leaderboard"))
					return false;

				if(!instance.hasStorage()) {
					p.sendMessage(ConfigUtil.CHAT_NO_DB.toString());
					return false;
				}

				String gameName = "Chess";
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

				if(args.length == 2) {
					String orderByType = args[1];
					for(StorageType storageType : StorageType.values()) {
						if(storageType.getKey().equalsIgnoreCase(orderByType)) {
							orderBy = storageType;
							break;
						}
					}
				}

				int numGamePlayers = instance.getStorageManager().getGamePlayerTotal(gameStorage);

				int page = 0;
				if(args.length == 3) {
					try {
						page = Integer.parseInt(args[2]) - 1;
					} catch (NumberFormatException e) {
					}
				}

				if (page < 0)
					page = 0;

				int numOfPages = (numGamePlayers / 10) + 1;

				if (page > numOfPages - 1)
					page = numOfPages - 1;

				LinkedHashMap<OfflinePlayer, LinkedHashMap<StorageType, Object>> topPlayers = instance.getStorageManager().fetchTopPlayers(gameStorage, orderBy, page);

				if(topPlayers == null) {
					p.sendMessage(ConfigUtil.CHAT_DB_ERROR.toString());
					return false;
				}

				int count = 1 + (page * 10);;

				p.sendMessage(ConfigUtil.CHAT_LEADERBOARD_HEADER.buildString(tempGame.getName(), orderBy.getKey()) + " (" + (page + 1) + "/"
						+ numOfPages + ")");
				for(OfflinePlayer player : topPlayers.keySet()) {
					p.sendMessage(ConfigUtil.CHAT_LEADERBOARD_FORMAT.buildLeaderBoardFormat(count, player.getName(),  topPlayers.get(player).get(orderBy) + ""));
					count++;
				}
			} else if (args[0].equalsIgnoreCase("reload")) {
				if(permissions && !p.hasPermission("chessboard.command.reload"))
					return false;

				p.sendMessage(ConfigUtil.CHAT_RELOAD.toString());

				instance.loadConfig();
			}
			return true;
		}

		return false;
	}

	private void sendHelpMessage(Player p) {
		p.sendMessage(ConfigUtil.CHAT_AVAIL_COMMANDS.toString());
	}

}
