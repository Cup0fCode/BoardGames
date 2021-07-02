package water.of.cup.boardgames;

import java.util.ArrayList;

import water.of.cup.boardgames.game.Game;

public abstract class BoardGamesExtension {
	public abstract ArrayList<Class<? extends Game>> getGames();
}
