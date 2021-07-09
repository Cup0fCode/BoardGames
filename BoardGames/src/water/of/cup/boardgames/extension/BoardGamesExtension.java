package water.of.cup.boardgames.extension;

import java.util.ArrayList;

import water.of.cup.boardgames.config.ConfigInterface;
import water.of.cup.boardgames.game.Game;

public abstract class BoardGamesExtension {
	public abstract ArrayList<Class<? extends Game>> getGames();
	public abstract String getExtensionName();
	public abstract ArrayList<BoardGamesConfigOption> getExtensionConfig();
}
