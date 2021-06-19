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
    private static final GameManager gameManager = instance.getGameManager();
    private static final ArrayList<GameRecipe> GAME_RECIPES = new ArrayList<>();

    static {
        for (String name : gameManager.getGameNames()) {
            Game temp = gameManager.newGame(name, 0);

            if(temp != null) {
                GameRecipe gameRecipe = temp.getGameRecipe();
                if(gameRecipe != null)
                    GAME_RECIPES.add(gameRecipe);
            }
        }
    }

    public static void loadRecipes() {
        for(GameRecipe gameRecipe : GAME_RECIPES) {
            gameRecipe.addToConfig();
        }

        if(ConfigUtil.RECIPE_ENABLED.toBoolean())
            addBukkitRecipes();
    }

    public static void loadGameSounds() {
        HashMap<String, Object> defaultConfig = new HashMap<>();
        FileConfiguration config = instance.getConfig();

        for (String name : gameManager.getGameNames()) {
            Game temp = gameManager.newGame(name, 0);

            if (temp != null) {
                ArrayList<GameSound> gameSounds = temp.getGameSounds();
                if(gameSounds != null) {
                    String configLoc = "settings.sounds." + temp.getName();
                    for(GameSound gameSound : gameSounds) {
                        defaultConfig.put(configLoc + "." + gameSound.getName(), gameSound.getSound().toString());
                    }

                    defaultConfig.put(configLoc + ".enabled", "true");
                }
            }
        }

        for (String key : defaultConfig.keySet()) {
            if(!config.contains(key)) {
                config.set(key, defaultConfig.get(key));
            }
        }

        instance.saveConfig();
    }

    private static void addBukkitRecipes() {
        FileConfiguration config = instance.getConfig();

        for (String recipeKey : config.getConfigurationSection("settings.recipe").getKeys(false)) {
            if (recipeKey.equals("enabled"))
                continue;

            if(!gameManager.isValidGame(recipeKey)) continue;

            Game temp = gameManager.newGame(recipeKey, 0);

            ItemStack boardItemStack = temp.getBoardItem();
            NamespacedKey key = new NamespacedKey(instance, recipeKey);
            ShapedRecipe recipe = new ShapedRecipe(key, boardItemStack);

            String configPath = "settings.recipe." + recipeKey;

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
