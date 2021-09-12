package water.of.cup.boardgames.game.games.poker;

public enum PokerButton {
    BET (4, 96),
    CALL(32, 96),
    CALL_ANY(66, 96),
    CHECK_FOLD(10, 112),
    FOLD(86, 112);

    private final int xDisplacement;
    private final int yDisplacement;

    PokerButton(int xDisplacement, int yDisplacement) {
        this.xDisplacement = xDisplacement;
        this.yDisplacement = yDisplacement;
    }

    public String getImageName(boolean dark) {
        return dark ? "POKER_" + toString() + "_DARK" : "POKER_" + toString();
    }

    public int getxDisplacement() {
        return this.xDisplacement;
    }

    public int getyDisplacement() {
        return this.yDisplacement;
    }
}
