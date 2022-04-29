package water.of.cup.boardgames.game.npcs;

public class NPCSkin {

    private final String skinSig;
    private final String skinData;

    public NPCSkin(String skinSig, String skinData) {
        this.skinData = skinData;
        this.skinSig = skinSig;
    }

    public String getSkinSig() {
        return skinSig;
    }

    public String getSkinData() {
        return skinData;
    }

}
