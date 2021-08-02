package water.of.cup.boardgames.game.inventories.trade;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import water.of.cup.boardgames.game.GamePlayer;

import java.util.ArrayList;
import java.util.Collections;

public class GameTradePlayer {

    private final Player player;
    private final ArrayList<ItemStack> items;
    private boolean ready;

    public GameTradePlayer(Player player) {
        this.player = player;
        this.items = new ArrayList<>();
        this.ready = false;
    }

    public Player getPlayer() {
        return player;
    }

    public ArrayList<ItemStack> getItems() {
        return new ArrayList<>(items);
    }

    public void updateItems(ArrayList<ItemStack> items) {
        this.items.clear();
        this.items.addAll(items);
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public boolean isReady() {
        return ready;
    }
}
