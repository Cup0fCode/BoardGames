package water.of.cup.boardgames.game.games.mines;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.game.*;
import water.of.cup.boardgames.game.inventories.GameInventory;
import water.of.cup.boardgames.game.storage.GameStorage;
import water.of.cup.boardgames.config.ConfigUtil;

import java.util.ArrayList;

public class Mines extends Game {

    private final BoardGames instance = BoardGames.getInstance();
    private int[][] selectedTiles;
    private int[][] bombLocations;
    private Button[][] tileButtons;
    private int bombCount;
    private int betAmount;

    public Mines(int rotation) {
        super(rotation);
    }

    @Override
    protected void setMapInformation(int i) {
        this.mapStructure = new int[][] { { 1 } };
        this.placedMapVal = 1;
    }

    @Override
    protected void startGame() {
        if(hasGameData("betAmount")) {
            this.betAmount = (int) getGameData("betAmount");
            if (instance.getEconomy().getBalance(teamManager.getTurnPlayer().getPlayer()) < this.betAmount) {
                teamManager.getTurnPlayer().getPlayer().sendMessage(water.of.cup.boardgames.config.ConfigUtil.CHAT_GUI_GAME_NO_MONEY_CREATE.toString());
                endGame();
                return;
            }

            instance.getEconomy().withdrawPlayer(teamManager.getTurnPlayer().getPlayer(), this.betAmount);
        } else {
            this.betAmount = 0;
        }

        super.startGame();
        buttons.clear();

        this.bombCount = (int) getGameData("bombAmount");
        if(this.bombCount > 24)
            this.bombCount = 24;

        setInGame();
        createBoard();
        mapManager.renderBoard();
    }

    @Override
    public void renderInitial() {
        super.renderInitial();
        createBoard();
        mapManager.renderBoard();
    }

    private void createBoard()   {
        this.selectedTiles = new int[5][5];
        this.bombLocations = new int[5][5];
        this.tileButtons = new Button[5][5];

        for(int y = 0; y < this.tileButtons.length; y++) {
            for(int x = 0; x < this.tileButtons[y].length; x++) {
                Button tileButton = new Button(this, "MINE_UNSELECTED", new int[] { x * 25 + 2, y * 25 + 2}, 0, "UNSELECTED");
                tileButton.setClickable(true);
                this.tileButtons[y][x] = tileButton;
                buttons.add(tileButton);
            }
        }

        for(int i = 0; i < this.bombCount; i++) {
            int randX = (int) (Math.random() * this.bombLocations.length);
            int randY = (int) (Math.random() * this.bombLocations.length);
            if(this.bombLocations[randY][randX] != 0) {
                i--;
                continue;
            }

            this.bombLocations[randY][randX] = 1;
        }
    }

    @Override
    protected void setGameName() {
        this.gameName = "Mines";
    }

    @Override
    protected void setBoardImage() {
        this.gameImage = new GameImage("MINES_BOARD");
    }

    @Override
    protected void clockOutOfTime() {

    }

    @Override
    protected Clock getClock() {
        return null;
    }

    @Override
    protected GameInventory getGameInventory() {
        return new MinesInventory(this);
    }

    @Override
    protected GameStorage getGameStorage() {
        return null;
    }

    @Override
    public ArrayList<String> getTeamNames() {
        return null;
    }

    @Override
    protected GameConfig getGameConfig() {
        return null;
    }

    @Override
    public void click(Player player, double[] loc, ItemStack map) {
        GamePlayer gamePlayer = getGamePlayer(player);
        if(!teamManager.getTurnPlayer().equals(gamePlayer)) return;

        int[] clickLoc = mapManager.getClickLocation(loc, map);

        Button b = getClickedButton(gamePlayer, clickLoc);
        if(b == null) return;

        int[] buttonLoc = getButtonLocation(b);
        if(buttonLoc == null) return;

        if(!b.getName().equals("UNSELECTED")) return;

        boolean didSelect = selectMine(b, buttonLoc);
        double multiplier = getWinMultiplier(bombCount, getTilesOpened());
        if(didSelect) {
            player.sendMessage(ConfigUtil.CHAT_MINES_CURRENT_MULT.buildString(multiplier, Math.round((this.betAmount * multiplier) * 100.0) / 100.0));
        } else {
            player.sendMessage(ConfigUtil.CHAT_MINES_LOSE.buildString(multiplier + ""));
            endGame();
        }

        mapManager.renderBoard();
    }

    private void endGame() {
        clearGamePlayers();
        super.endGame(null);
    }

    private boolean selectMine(Button b, int[] buttonLoc) {
        if(this.bombLocations[buttonLoc[1]][buttonLoc[0]] == 1) {
            // Show all the bombs
            for (int y = 0; y < bombLocations.length; y++) {
                for(int x = 0; x < bombLocations[y].length; x++) {
                    if(bombLocations[y][x] == 1) {
                        this.tileButtons[y][x].getImage().setImage("MINE_BOMB");
                        this.tileButtons[y][x].setName("BOMB");
                    }
                }
            }
            return false;
        }

        this.selectedTiles[buttonLoc[1]][buttonLoc[0]] = 1;

        b.getImage().setImage("MINE_SELECTED");
        b.setName("SELECTED");
        return true;
    }

    private double getWinMultiplier(int bombCount, int tilesOpened) {
        int tileTotal = 25;
        int availTiles = 25 - bombCount;

        long first = water.of.cup.boardgames.game.games.gameutils.MathUtils.binomial(tileTotal, tilesOpened);
        long second = water.of.cup.boardgames.game.games.gameutils.MathUtils.binomial(availTiles, tilesOpened);
        double result = 0.99 * ((double) first / second);
        return Math.round(result * 100) / 100.0;
    }

    private int[] getButtonLocation(Button b) {
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                if (b == tileButtons[y][x])
                    return new int[] { x, y };
            }
        }
        return null;
    }

    private int getTilesOpened() {
        int numOpened = 0;
        for (int[] selectedTile : this.selectedTiles) {
            for (int i : selectedTile) {
                if (i == 1) {
                    numOpened++;
                }
            }
        }
        return numOpened;
    }

    @Override
    protected void gamePlayerOutOfTime(GamePlayer gamePlayer) {

    }

    protected void cashOut() {
        double multiplier = getTilesOpened() == 0 ? 1.0 : getWinMultiplier(bombCount, getTilesOpened());
        double payout = Math.round((this.betAmount * multiplier) * 100.0) / 100.0;
        instance.getEconomy().depositPlayer(teamManager.getTurnPlayer().getPlayer(), payout);
        teamManager.getTurnPlayer().getPlayer().sendMessage(ConfigUtil.CHAT_MINES_WIN.buildString(multiplier, payout));
        endGame();
    }

    @Override
    public ItemStack getBoardItem() {
        return new BoardItem(gameName, new ItemStack(Material.ACACIA_TRAPDOOR, 1));
    }
}
