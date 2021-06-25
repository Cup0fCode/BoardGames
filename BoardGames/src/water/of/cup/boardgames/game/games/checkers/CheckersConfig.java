package water.of.cup.boardgames.game.games.checkers;

import org.bukkit.Material;
import org.bukkit.Sound;
import water.of.cup.boardgames.config.GameRecipe;
import water.of.cup.boardgames.config.GameSound;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.GameConfig;

import java.util.ArrayList;
import java.util.HashMap;

public class CheckersConfig extends GameConfig {
    public CheckersConfig(Game game) {
        super(game);
    }

    @Override
    protected GameRecipe getGameRecipe() {
        HashMap<String, String> recipe = new HashMap<>();
        recipe.put("I", Material.IRON_INGOT.toString());
        recipe.put("W", Material.OAK_WOOD.toString());

        ArrayList<String> shape = new ArrayList<String>() {
            {
                add("WWW");
                add("WIW");
                add("WWW");
            }
        };

        return new GameRecipe(game.getName(), recipe, shape);
    }

    @Override
    protected ArrayList<GameSound> getGameSounds() {
        ArrayList<GameSound> gameSounds = new ArrayList<>();
        gameSounds.add(new GameSound("click", Sound.BLOCK_WOOD_PLACE));
        return gameSounds;
    }

    @Override
    protected HashMap<String, Object> getCustomValues() {
        return null;
    }
}
