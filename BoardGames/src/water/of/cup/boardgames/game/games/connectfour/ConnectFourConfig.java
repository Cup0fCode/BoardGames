package water.of.cup.boardgames.game.games.connectfour;

import org.bukkit.Material;
import org.bukkit.Sound;
import water.of.cup.boardgames.config.GameRecipe;
import water.of.cup.boardgames.config.GameSound;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.GameConfig;

import java.util.ArrayList;
import java.util.HashMap;

public class ConnectFourConfig extends GameConfig {
    public ConnectFourConfig(Game game) {
        super(game);
    }

    @Override
    protected GameRecipe getGameRecipe() {
        HashMap<String, String> recipe = new HashMap<>();
        recipe.put("R", Material.RED_DYE.toString());
        recipe.put("B", Material.BLUE_DYE.toString());
        recipe.put("P", Material.PAPER.toString());
        recipe.put("L", Material.LEATHER.toString());

        ArrayList<String> shape = new ArrayList<String>() {
            {
                add("RPB");
                add("RPB");
                add("LLL");
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

    @Override
    protected int getWinAmount() {
        return 0;
    }
}
