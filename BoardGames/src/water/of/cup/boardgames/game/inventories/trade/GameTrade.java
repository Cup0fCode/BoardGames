package water.of.cup.boardgames.game.inventories.trade;

import de.themoep.inventorygui.InventoryGui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import water.of.cup.boardgames.game.GamePlayer;

import java.util.ArrayList;
import java.util.HashMap;

public class GameTrade {

    private final GameTradePlayer player1;
    private final GameTradePlayer player2;
    private final ArrayList<GameTradePlayer> players;

    public GameTrade(Player player1, Player player2) {
        this.player1 = new GameTradePlayer(player1);
        this.player2 = new GameTradePlayer(player2);;
        this.players = new ArrayList<>();
        this.players.add(this.player1);
        this.players.add(this.player2);
    }

    public void sendBackItems() {
        for(GameTradePlayer gameTradePlayer : players) {
            for(ItemStack itemStack : gameTradePlayer.getItems()) {
                HashMap<Integer, ItemStack> leftOverItems = gameTradePlayer.getPlayer().getInventory().addItem(itemStack);
                // Drops items it could not give back
                for(ItemStack leftOverItem : leftOverItems.values()) {
                    Player player = gameTradePlayer.getPlayer();
                    player.getWorld().dropItem(player.getLocation(), leftOverItem);
                }
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

        InventoryGui gui = InventoryGui.get(getOtherPlayer(gameTradePlayer).getPlayer());
        if(gui != null) {
            gui.draw();
        }
    }
}
