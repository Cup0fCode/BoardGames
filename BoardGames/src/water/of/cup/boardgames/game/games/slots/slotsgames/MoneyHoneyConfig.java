package water.of.cup.boardgames.game.games.slots.slotsgames;

import org.bukkit.Material;
import water.of.cup.boardgames.config.GameRecipe;
import water.of.cup.boardgames.config.GameSound;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.GameConfig;

import java.util.ArrayList;
import java.util.HashMap;

public class MoneyHoneyConfig extends GameConfig {
    public MoneyHoneyConfig(Game game) {
        super(game);
    }

    @Override
    protected GameRecipe getGameRecipe() {
        HashMap<String, String> recipe = new HashMap<>();
        recipe.put("B", Material.BLACK_DYE.toString());
        recipe.put("G", Material.GOLD_INGOT.toString());
        recipe.put("R", Material.REDSTONE.toString());
        
        ArrayList<String> shape = new ArrayList<String>() {
            {
                add("BBB");
                add("GGG");
                add("BRB");
            }
        };

        return new GameRecipe(game.getName(), recipe, shape);
    }

    @Override
    protected ArrayList<GameSound> getGameSounds() {
        ArrayList<GameSound> gameSounds = new ArrayList<>();
        //gameSounds.add(new GameSound("click", Sound.BLOCK_WOOD_PLACE));
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
