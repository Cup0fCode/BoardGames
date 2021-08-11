package water.of.cup.boardgames.game.games.uno;

import org.bukkit.Material;
import org.bukkit.Sound;
import water.of.cup.boardgames.config.GameRecipe;
import water.of.cup.boardgames.config.GameSound;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.GameConfig;

import java.util.ArrayList;
import java.util.HashMap;

public class UnoConfig extends GameConfig {
    public UnoConfig(Game game) {
        super(game);
    }

    @Override
    protected GameRecipe getGameRecipe() {
        HashMap<String, String> recipe = new HashMap<>();
        recipe.put("B", Material.BLUE_DYE.toString());
        recipe.put("G", Material.GREEN_DYE.toString());
        recipe.put("R", Material.RED_DYE.toString());
        recipe.put("Y", Material.YELLOW_DYE.toString());
        recipe.put("L", Material.LEATHER.toString());
        recipe.put("I", Material.INK_SAC.toString());
        recipe.put("P", Material.PAPER.toString());

        ArrayList<String> shape = new ArrayList<String>() {
            {
                add("BIR");
                add("GPY");
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
        HashMap<String, Object> configOptions = new HashMap<>();
        configOptions.put("middle_card_size", 2);
        return configOptions;
    }

    @Override
    protected int getWinAmount() {
        return 0;
    }
}
