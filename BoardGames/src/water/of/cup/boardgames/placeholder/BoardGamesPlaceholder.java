package water.of.cup.boardgames.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.config.ConfigUtil;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.storage.StorageType;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class BoardGamesPlaceholder extends PlaceholderExpansion {

    private BoardGames plugin;

    public BoardGamesPlaceholder(BoardGames plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean persist(){
        return true;
    }

    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    public String getAuthor(){
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getIdentifier(){
        return "boards";
    }

    @Override
    public String getVersion(){
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier){
        if(player == null || !plugin.hasStorage()){
            return "";
        }

        String[] args = identifier.split("_");
        if(args.length != 3) return null;

        // %boards_stats_GAME_wins%
        if(args[0].equals("stats")) {
            String gameName = args[1];
            String storeTypeString = args[2];

            Game tempGame = plugin.getGameManager().newGame(gameName, 0);

            if(tempGame == null) {
                return null;
            }

            if(!tempGame.hasGameStorage()) {
                return null;
            }

            if(!tempGame.getGameStore().hasStorageType(storeTypeString)) {
                return null;
            }

            StorageType storeType = BoardGames.getInstance().getStorageManager().getStorageTypeByKey(storeTypeString);
            if(storeType == null) {
                return null;
            }

            LinkedHashMap<StorageType, Object> playerStats = plugin.getStorageManager().fetchPlayerStats(player, tempGame.getGameStore(), true);
            if(playerStats == null) return null;
            if(playerStats.get(storeType) == null) return null;

            return playerStats.get(storeType) + "";
        }

        // We return null if an invalid placeholder (f.e. %someplugin_placeholder3%)
        // was provided
        return null;
    }
}
