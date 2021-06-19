package water.of.cup.boardgames.config;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import water.of.cup.boardgames.BoardGames;

public enum ConfigUtil {

    // PLUGIN SETTINGS
    PERMISSIONS_ENABLED("settings.permissions", "true"),
    WAGERS_ENABLED("settings.wagers", "true"),
    RECIPE_ENABLED("settings.recipe.enabled", "true"),
    DB_HOST("settings.database.host", "localhost"),
    DB_PORT("settings.database.port", "3306"),
    DB_NAME("settings.database.database", "boardgames"),
    DB_USERNAME("settings.database.username", "root"),
    DB_PASS("settings.database.password", " "),
    DB_ENABLED("settings.database.enabled", "false"),

    // CHAT MESSAGES
    CHAT_NO_DB("settings.messages.chat.nodb", "Database must be enabled to view stats."),
    CHAT_NO_GAME("settings.messages.chat.nogame", "No game found with that name."),
    CHAT_NO_PLAYER("settings.messages.chat.noplayer", "No player found with that name."),
    CHAT_DB_ERROR("settings.messages.chat.dberror", "Error calling to database."),
    CHAT_GAME_NAMES("settings.messages.chat.gamenames", "Game Names: "),
    CHAT_STATS_HEADER("settings.messages.chat.statsheader", "%player%'s stats"),
    CHAT_LEADERBOARD_HEADER("settings.messages.chat.leaderboardheader", "%game% leaderboard sorting by %sort%"),
    CHAT_AVAIL_COMMANDS("settings.messages.chat.availcommands", "Available commands\n/bg games - lists games\n/bg board [game name] - gives you the game's item\n/bg stats [game name] [player name]\n/bg leaderboard [game name] [order by]");

    private final String path;
    private final String defaultValue;
    private static final BoardGames instance = BoardGames.getInstance();

    ConfigUtil(String path, String defaultValue) {
        this.path = path;
        this.defaultValue = defaultValue;
    }

    @Override
    public String toString() {
        String configString = instance.getConfig().getString(this.path);

        if(configString == null) return "";

        return ChatColor.translateAlternateColorCodes('&', configString);
    }

    public String toRawString() {
        return ChatColor.stripColor(this.toString());
    }

    public boolean toBoolean() {
        return this.toString().equals("true");
    }

    public String getPath() {
        return this.path;
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }

    public static boolean getBoolean(String path) {
        String configString = instance.getConfig().getString(path);

        if(configString == null) return false;

        return configString.equals("true");
    }

    public String buildString(String playerName) {
        String formatted = this.toString();

        formatted = formatted.replace("%player%", playerName);

        return formatted;
    }

    public String buildString(String game, String sort) {
        String formatted = this.toString();

        formatted = formatted.replace("%game%", game).replace("%sort%", sort);

        return formatted;
    }
}
