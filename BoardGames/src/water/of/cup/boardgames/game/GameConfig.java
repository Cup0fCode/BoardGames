package water.of.cup.boardgames.game;

import org.bukkit.Sound;
import water.of.cup.boardgames.config.GameRecipe;
import water.of.cup.boardgames.config.GameSound;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class GameConfig {

    protected abstract GameRecipe getGameRecipe();
    protected abstract ArrayList<GameSound> getGameSounds();
    protected final Game game;

    public GameConfig(Game game) {
        this.game = game;
    }

}
