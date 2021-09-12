package water.of.cup.boardgames.game.games.slots.slotsgames;

import org.bukkit.Material;
import water.of.cup.boardgames.config.GameRecipe;
import water.of.cup.boardgames.config.GameSound;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.GameConfig;

import java.util.ArrayList;
import java.util.HashMap;

public class LibertyBellConfig extends GameConfig {
    public LibertyBellConfig(Game game) {
        super(game);
    }

    @Override
    protected GameRecipe getGameRecipe() {
        HashMap<String, String> recipe = new HashMap<>();
        recipe.put("B", Material.BLACK_DYE.toString());

        ArrayList<String> shape = new ArrayList<String>() {
            {
                add("B B");
                add("BBB");
                add("BBB");
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
