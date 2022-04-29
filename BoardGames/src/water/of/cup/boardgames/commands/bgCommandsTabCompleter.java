package water.of.cup.boardgames.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.config.ConfigUtil;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.GameManager;
import water.of.cup.boardgames.game.storage.GameStorage;
import water.of.cup.boardgames.game.storage.StorageType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class bgCommandsTabCompleter implements TabCompleter {

	private final BoardGames instance = BoardGames.getInstance();
	private final GameManager gameManager = instance.getGameManager();

	@Override
	public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
		ArrayList<String> args = new ArrayList<>();
		boolean permissions = ConfigUtil.PERMISSIONS_ENABLED.toBoolean();
		if (strings.length == 1) {
			if(permissions) {
				for(String arg : bgCommands.ARG_PERMS.keySet()) {
					if(commandSender.hasPermission(bgCommands.ARG_PERMS.get(arg)))
						args.add(arg);
				}
			} else {
				Collections.addAll(bgCommands.ARG_PERMS.keySet());
			}
		} else if (strings.length == 2) {
			if (strings[0].equalsIgnoreCase("board")) {
				Collections.addAll(args, gameManager.getAltGameNames().toArray(new String[0]));
			} else if(strings[0].equalsIgnoreCase("leaderboard") || strings[0].equalsIgnoreCase("stats")) {
				// Only show games with database
				for (String name : gameManager.getGameNames()) {
					Game temp = instance.getGameManager().newGame(name, 0);

					if (temp != null && temp.hasGameStorage()) {
						args.add(temp.getAltName());
					}
				}
			}
		} else if (strings.length == 3) {
			if (strings[0].equalsIgnoreCase("leaderboard")) {
				String gameName = strings[1];
				Game tempGame = gameManager.newGame(gameName, 0);

				if (tempGame != null) {
					GameStorage gameStorage = tempGame.getGameStore();
					for (StorageType storageType : gameStorage.getStorageTypes()) {
						args.add(storageType.getKey());
					}
				}
			} else if (strings[0].equalsIgnoreCase("stats")) {
				for (Player player : Bukkit.getOnlinePlayers()) {
					args.add(player.getName());
				}
			}
		} else if (strings.length == 4) {
			if (strings[0].equalsIgnoreCase("leaderboard")) {
				String gameName = strings[1];
				Game tempGame = gameManager.newGame(gameName, 0);

				if (tempGame != null) {
					GameStorage gameStorage = tempGame.getGameStore();
					int extraPages = (instance.getStorageManager().getGamePlayerTotal(gameStorage) / 10);
					for (int i = 0; i < extraPages; i++) {
						args.add((i + 2) + "");
					}
				}
			}
		}

		ArrayList<String> finalArgs = new ArrayList<>();
		String start = strings[strings.length - 1];
		for (String arg : args)
			if (arg.startsWith(start))
				finalArgs.add(arg);

		return finalArgs;
	}
}
