package water.of.cup.boardgames.game.games.poker;

import water.of.cup.boardgames.config.ConfigUtil;
import water.of.cup.boardgames.game.npcs.GameNPC;
import water.of.cup.boardgames.game.npcs.NPCSkin;

public class PokerNPC extends GameNPC {

    private static final String skinSig = ConfigUtil.NPC_POKER_SKIN_SIG.toRawString();
    private static final String skinData = ConfigUtil.NPC_POKER_SKIN_DATA.toRawString();

    public PokerNPC(double[] loc) {
        super(loc);
    }

    @Override
    protected String getName() {
        return ConfigUtil.NPC_POKER_NAME.toString();
    }

    @Override
    protected NPCSkin getSkin() {
        return new NPCSkin(skinSig, skinData);
    }
}
