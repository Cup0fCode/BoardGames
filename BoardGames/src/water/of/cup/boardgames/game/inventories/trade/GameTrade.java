package water.of.cup.boardgames.game.inventories.trade;

import de.themoep.inventorygui.InventoryGui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.GamePlayer;

import java.util.ArrayList;
import java.util.HashMap;

public class GameTrade {

    private final GameTradePlayer player1;
    private final GameTradePlayer player2;
    private final ArrayList<GameTradePlayer> players;
    private final GameTradeCallback gameTradeCallback;
    private GameTradeTimer gameTradeTimer;
    private final Game game;

    public GameTrade(Player player1, Player player2, Game game, GameTradeCallback gameTradeCallback) {
        this.player1 = new GameTradePlayer(player1);
        this.player2 = new GameTradePlayer(player2);;
        this.players = new ArrayList<>();
        this.players.add(this.player1);
        this.players.add(this.player2);
        this.gameTradeCallback = gameTradeCallback;
        this.game = game;
    }

    public void sendBackItems() {
        for(GameTradePlayer gameTradePlayer : players) {
            sendItems(gameTradePlayer.getPlayer(), gameTradePlayer.getItems());
        }
    }

    public void sendWinnerItems(Player player) {
        ArrayList<ItemStack> items = player1.getItems();
        items.addAll(player2.getItems());
        sendItems(player, items);
    }

    private void sendItems(Player player, ArrayList<ItemStack> items) {
        for(ItemStack itemStack : items) {
            HashMap<Integer, ItemStack> leftOverItems = player.getInventory().addItem(itemStack);
            // Drops items it could not give back
            for(ItemStack leftOverItem : leftOverItems.values()) {
                player.getWorld().dropItem(player.getLocation(), leftOverItem);
            }
        }
    }

    public void updateInventory(Player player, ArrayList<ItemStack> items) {
        GameTradePlayer gameTradePlayer = getGameTradePlayer(player);
        gameTradePlayer.updateItems(items);

        InventoryGui gui = InventoryGui.get(getOtherPlayer(gameTradePlayer).getPlayer());
        if(gui != null) {
            gui.draw();
        }
    }

    public void updateInventory(int timeLeft) {
        for(GameTradePlayer gameTradePlayer : players) {
            new GameTradeInventory(this, gameTradeCallback).build(gameTradePlayer.getPlayer(), timeLeft);
        }
    }

    private int getAmountItems(ArrayList<ItemStack> items) {
        int count = 0;
        for(ItemStack itemStack : items) {
            if(itemStack != null) count += itemStack.getAmount();
        }

        return count;
    }

    public GameTradePlayer getGameTradePlayer(Player player) {
        return player.equals(player1.getPlayer()) ? player1 : player2;
    }

    public GameTradePlayer getOtherPlayer(GameTradePlayer gameTradePlayer) {
        return gameTradePlayer.equals(player1) ? player2 : player1;
    }

    public void setReady(Player player, boolean ready) {
        GameTradePlayer gameTradePlayer = getGameTradePlayer(player);
        gameTradePlayer.setReady(ready);

        GameTradePlayer otherPlayer = getOtherPlayer(gameTradePlayer);
        InventoryGui gui = InventoryGui.get(otherPlayer.getPlayer());
        if(gui != null) {
            gui.draw();
        }

        if(allReady()) {
            if(gameTradeTimer == null) {
                gameTradeTimer = new GameTradeTimer(this);
                gameTradeTimer.runTaskTimer(BoardGames.getInstance(), 5, 5);
            }
        } else {
            if(gameTradeTimer != null) {
                gameTradeTimer.cancel();
                gameTradeTimer = null;

                updateInventory(-1);
            }
        }
    }

    public void onAccept() {
        if(allReady())
            gameTradeCallback.onAccept(this);
    }

    private boolean allReady() {
        for(GameTradePlayer gameTradePlayer : players) {
            if(!gameTradePlayer.isReady()) return false;
        }
        return true;
    }

    public Game getGame() {
        return game;
    }
}
