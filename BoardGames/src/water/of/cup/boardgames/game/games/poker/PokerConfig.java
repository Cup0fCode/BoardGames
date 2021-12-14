package water.of.cup.boardgames.game.games.poker;

import water.of.cup.boardgames.config.GameRecipe;
import water.of.cup.boardgames.config.GameSound;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.GameConfig;

import java.util.ArrayList;
import java.util.HashMap;

public class PokerConfig extends GameConfig {
    public PokerConfig(Game game) {
        super(game);
    }

    @Override
    protected GameRecipe getGameRecipe() {
        return null;
    }

    @Override
    protected ArrayList<GameSound> getGameSounds() {
        return null;
    }

    @Override
    protected HashMap<String, Object> getCustomValues() {
        HashMap<String, Object> configOptions = new HashMap<>();
        configOptions.put("turn_timer", 15);
        configOptions.put("game_timer", 15);
        return configOptions;
    }

    @Override
    protected int getWinAmount() {
        return 0;
    }
}
