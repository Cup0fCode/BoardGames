package water.of.cup.boardgames.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import water.of.cup.boardgames.BoardGames;
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
        if(strings.length == 1) {
            args.add("games");
            args.add("board");
            args.add("stats");
            args.add("leaderboard");
        } else if(strings.length == 2) {
            if (strings[0].equalsIgnoreCase("leaderboard")
                    || strings[0].equalsIgnoreCase("stats")
                    || strings[0].equalsIgnoreCase("board")) {
                Collections.addAll(args, gameManager.getGameNames());
            }
        } else if(strings.length == 3) {
            if (strings[0].equalsIgnoreCase("leaderboard")) {
                String gameName = strings[1];
                Game tempGame = gameManager.newGame(gameName, 0);

                if(tempGame != null) {
                    GameStorage gameStorage = tempGame.getGameStore();
                    for(StorageType storageType : gameStorage.getStorageTypes()) {
                        args.add(storageType.getKey());
                    }
                }
            } else if (strings[0].equalsIgnoreCase("stats")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    args.add(player.getName());
                }
            }
        }
        return args;
    }
}
