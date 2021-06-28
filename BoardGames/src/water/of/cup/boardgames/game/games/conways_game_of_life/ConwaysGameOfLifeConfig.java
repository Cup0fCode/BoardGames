package water.of.cup.boardgames.game.games.conways_game_of_life;

import org.bukkit.Material;
import water.of.cup.boardgames.config.GameRecipe;
import water.of.cup.boardgames.config.GameSound;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.GameConfig;

import java.util.ArrayList;
import java.util.HashMap;

public class ConwaysGameOfLifeConfig extends GameConfig {


    public ConwaysGameOfLifeConfig(Game game) {
        super(game);
    }

    @Override
    protected GameRecipe getGameRecipe() {
        HashMap<String, String> recipe = new HashMap<>();
       
        recipe.put("Q", Material.QUARTZ.toString());
        recipe.put("H", Material.WITHER_SKELETON_SKULL.toString());

        ArrayList<String> shape = new ArrayList<String>() {
            {
                add("QQQ");
                add("QHQ");
                add("QQQ");
            }
        };

        return new GameRecipe(game.getName(), recipe, shape);
    }

    @Override
    protected ArrayList<GameSound> getGameSounds() {
        return null;
    }

    @Override
    protected HashMap<String, Object> getCustomValues() {
        return null;
    }

    @Override
    protected int getWinAmount() {
        return 0;
    }
}
