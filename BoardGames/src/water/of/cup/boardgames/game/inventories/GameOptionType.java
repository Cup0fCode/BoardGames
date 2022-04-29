package water.of.cup.boardgames.game.inventories;

public enum GameOptionType {
    TOGGLE, // Accepts 2 strings to alternate from
    COUNT,  // Accepts an array of strings/nums to alter between
    CUSTOM  // Must be implemented in own way
}
