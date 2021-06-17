package water.of.cup.boardgames.config;

import org.bukkit.ChatColor;
import water.of.cup.boardgames.BoardGames;

public enum ConfigUtil {

    // PLUGIN SETTINGS
    PERMISSIONS_ENABLED("settings.permissions", "true"),
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
    CHAT_DB_ERROR("settings.messages.chat.dberror", "Error calling to database.");

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
}
