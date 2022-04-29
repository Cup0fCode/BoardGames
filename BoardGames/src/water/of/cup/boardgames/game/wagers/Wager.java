package water.of.cup.boardgames.game.wagers;

import water.of.cup.boardgames.game.GamePlayer;

public interface Wager {

    void complete(GamePlayer winner);

    void cancel();

}
