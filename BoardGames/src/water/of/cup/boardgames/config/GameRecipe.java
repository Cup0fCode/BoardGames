package water.of.cup.boardgames.config;

import org.bukkit.configuration.file.FileConfiguration;
import water.of.cup.boardgames.BoardGames;

import java.util.ArrayList;
import java.util.HashMap;

public class GameRecipe {

    private final BoardGames instance = BoardGames.getInstance();
    private final HashMap<String, String> recipe;
    private final ArrayList<String> shape;
    private final String name;

    public GameRecipe(String gameName, HashMap<String, String> recipe, ArrayList<String> shape) {
        this.name = gameName;
        this.recipe = recipe;
        this.shape = shape;
    }

    public void addToConfig() {
        HashMap<String, Object> defaultConfig = new HashMap<>();
        FileConfiguration config = instance.getConfig();

        String configLoc = "settings.games." + this.name + ".recipe";

        defaultConfig.put(configLoc + ".shape", shape);

        if (!config.contains(configLoc + ".ingredients")) {
            for (String key : recipe.keySet()) {
                defaultConfig.put(configLoc + ".ingredients." + key, recipe.get(key));
            }
        }

        for (String key : defaultConfig.keySet()) {
            if (!config.contains(key)) {
                config.set(key, defaultConfig.get(key));
            }
        }

        instance.saveConfig();
    }

}
