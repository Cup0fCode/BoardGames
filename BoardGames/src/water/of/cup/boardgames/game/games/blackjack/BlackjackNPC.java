package water.of.cup.boardgames.game.games.blackjack;

import water.of.cup.boardgames.game.npcs.GameNPC;
import water.of.cup.boardgames.game.npcs.NPCSkin;
import water.of.cup.boardgames.config.ConfigUtil;

public class BlackjackNPC extends GameNPC {

    private static final String skinSig = ConfigUtil.NPC_BLACKJACK_SKIN_SIG.toRawString();
    private static final String skinData = ConfigUtil.NPC_BLACKJACK_SKIN_DATA.toRawString();

    public BlackjackNPC(double[] loc) {
        super(loc);
    }

    @Override
    protected String getName() {
        return ConfigUtil.NPC_BLACKJACK_NAME.toString();
    }

    @Override
    protected NPCSkin getSkin() {
        return new NPCSkin(skinSig, skinData);
    }
}
