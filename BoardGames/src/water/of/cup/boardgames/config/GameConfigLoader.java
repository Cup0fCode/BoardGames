package water.of.cup.boardgames.config;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.GameManager;

import java.util.ArrayList;
import java.util.HashMap;

public class GameConfigLoader {

    private static final BoardGames instance = BoardGames.getInstance();
    private static final HashMap<String, Game> GAMES = new HashMap<>();

    public static void loadGameConfig() {
        for (String name : instance.getGameManager().getGameNames()) {
            Game temp = instance.getGameManager().newGame(name, 0);

            if(temp != null) {
                Bukkit.getLogger().info("[BoardGames] Loading game " + temp.getName());
                GAMES.put(temp.getName(), temp);
            }
        }

        loadRecipes();
        loadGameSounds();
        loadCustomConfigValues();
        loadGameWinAmounts();
        loadDefaults();
    }

    public static void unloadConfig() {
        GAMES.clear();
    }

    private static void loadRecipes() {
        for(String gameName : GAMES.keySet()) {
            Game game = GAMES.get(gameName);

            GameRecipe gameRecipe = game.getGameRecipe();
            if(gameRecipe != null)
                gameRecipe.addToConfig();
        }

        if(ConfigUtil.RECIPE_ENABLED.toBoolean())
            addBukkitRecipes();
    }

    private static void loadGameSounds() {
        HashMap<String, Object> defaultConfig = new HashMap<>();

        for (String gameName : GAMES.keySet()) {
            Game temp = GAMES.get(gameName);

            if (temp != null) {
                ArrayList<GameSound> gameSounds = temp.getGameSounds();
                if(gameSounds != null) { ;
                    String configLoc = "settings.games." + temp.getName() + ".sounds";
                    for(GameSound gameSound : gameSounds) {
                        defaultConfig.put(configLoc + "." + gameSound.getName(), gameSound.getSound().toString());
                    }

                    defaultConfig.put(configLoc + ".enabled", "true");
                }
            }
        }

        instance.addToConfig(defaultConfig);
    }

    private static void loadGameWinAmounts() {
        HashMap<String, Object> defaultConfig = new HashMap<>();

        for (String gameName : GAMES.keySet()) {
            Game temp = GAMES.get(gameName);

            if (temp != null) {
                int gameWinAmount = temp.getGameWinAmount();
                String configLoc = "settings.games." + temp.getName() + ".winAmount";
                defaultConfig.put(configLoc, gameWinAmount);
            }
        }

        instance.addToConfig(defaultConfig);
    }

    private static void loadCustomConfigValues() {
        HashMap<String, Object> defaultConfig = new HashMap<>();

        for (String gameName : GAMES.keySet()) {
            Game temp = GAMES.get(gameName);

            if (temp != null) {
                HashMap<String, Object> customValues = temp.getCustomValues();
                if(customValues != null) {
                    String configLoc = "settings.games." + temp.getName() + ".misc";
                    for(String key : customValues.keySet()) {
                        defaultConfig.put(configLoc + "." + key, customValues.get(key));
                    }
                }
            }
        }

        instance.addToConfig(defaultConfig);
    }

    private static void loadDefaults() {
        HashMap<String, Object> defaultConfig = new HashMap<>();

        for (String gameName : GAMES.keySet()) {
            Game temp = GAMES.get(gameName);

            if (temp != null) {
                String configLoc = "settings.games." + temp.getName();
                defaultConfig.put(configLoc + ".enabled", "true");
                defaultConfig.put(configLoc + ".altName", temp.getName());
                defaultConfig.put(configLoc + ".maxWager", "-1");
            }
        }

        instance.addToConfig(defaultConfig);
    }

    private static void addBukkitRecipes() {
        FileConfiguration config = instance.getConfig();

        for (String recipeKey : config.getConfigurationSection("settings.games").getKeys(false)) {
            if(!instance.getGameManager().isValidGame(recipeKey)) continue;

            Game temp = instance.getGameManager().newGame(recipeKey, 0);

            if(temp.getGameRecipe() == null) continue;

            ItemStack boardItemStack = temp.getBoardItem();
            NamespacedKey key = new NamespacedKey(instance, recipeKey);
            ShapedRecipe recipe = new ShapedRecipe(key, boardItemStack);

            String configPath = "settings.games." + recipeKey + ".recipe";

            ArrayList<String> shapeArr = (ArrayList<String>) config.get(configPath + ".shape");
            recipe.shape(shapeArr.toArray(new String[shapeArr.size()]));

            String ingredientsPath = configPath + ".ingredients";

            for (String ingredientKey : config.getConfigurationSection(ingredientsPath).getKeys(false)) {
                recipe.setIngredient(ingredientKey.charAt(0),
                        Material.valueOf((String) config.get(ingredientsPath + "." + ingredientKey)));
            }

            Bukkit.addRecipe(recipe);
        }
    }

}
