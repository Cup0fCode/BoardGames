package water.of.cup.boardgames.game.games.tictactoe;

import org.bukkit.Material;
import org.bukkit.Sound;
import water.of.cup.boardgames.config.GameRecipe;
import water.of.cup.boardgames.config.GameSound;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.GameConfig;

import java.util.ArrayList;
import java.util.HashMap;

public class TicTacToeConfig extends GameConfig {

    public TicTacToeConfig(Game game) {
        super(game);
    }

    @Override
    protected GameRecipe getGameRecipe() {
        HashMap<String, String> recipe = new HashMap<>();
        recipe.put("I", Material.IRON_INGOT.toString());
        recipe.put("G", Material.GOLD_INGOT.toString());
        recipe.put("R", Material.REDSTONE.toString());

        ArrayList<String> shape = new ArrayList<String>() {
            {
                add("III");
                add("RGR");
                add("RRR");
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
//        HashMap<String, Object> customValues = new HashMap<>();
//        customValues.put("testoption", 123);
//        customValues.put("anotheroption", "test");
//        return customValues;
        return null;
    }
}
