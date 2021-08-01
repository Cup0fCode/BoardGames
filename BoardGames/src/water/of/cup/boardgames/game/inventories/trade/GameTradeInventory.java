package water.of.cup.boardgames.game.inventories.trade;

import de.themoep.inventorygui.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.config.ConfigUtil;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.inventories.GameInventory;
import water.of.cup.boardgames.game.inventories.GameOption;
import water.of.cup.boardgames.game.inventories.InventoryScreen;
import water.of.cup.boardgames.game.inventories.InventoryUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GameTradeInventory {

    private final GameTrade gameTrade;

    public GameTradeInventory(GameTrade gameTrade) {
        this.gameTrade = gameTrade;
    }

    public void build(Player player, GameTradeCallback gameTradeCallback) {
        String[] guiSetup = getGuiSetup();
        InventoryGui gui = new InventoryGui(BoardGames.getInstance(), player, ConfigUtil.GUI_GAME_TRADE_TITLE.toString(), guiSetup);

        gui.setFiller(new ItemStack(Material.BLACK_STAINED_GLASS_PANE));

        Inventory inv = Bukkit.createInventory(null, InventoryType.CHEST);

        gui.addElement(new GuiStorageActionElement('i', inv, click -> {
            List<ItemStack> items = arrayToList(inv.getStorageContents());

            for(ItemStack itemStack :items) {
                player.sendMessage(itemStack.getType().toString());
            }

            player.sendMessage("Inv content size: " + items.size());
            this.gameTrade.updateInventory(player, new ArrayList<>(items));
            return false;
        }));

        GuiElementGroup group = new GuiElementGroup('j');
        GameTradePlayer gameTradePlayer = gameTrade.getGameTradePlayer(player);
        GameTradePlayer otherPlayer = gameTrade.getOtherPlayer(gameTradePlayer);
        for(int i = 0; i < 12; i++) {
            int finalI = i;
            group.addElement(new DynamicGuiElement('j', (viewer) ->
                    new StaticGuiElement('j', finalI >= otherPlayer.getItems().size()
                            ? new ItemStack(Material.AIR)
                            : otherPlayer.getItems().get(finalI),
                    finalI >= otherPlayer.getItems().size()
                            ? 0
                            : otherPlayer.getItems().get(finalI).getAmount(),
                    click -> true)));
        }

        gui.addElement(group);

        // Player skulls
        gui.addElement(new StaticGuiElement('h', InventoryUtils.getPlayerHead(player), player.getDisplayName()));
        gui.addElement(new StaticGuiElement('g', InventoryUtils.getPlayerHead(otherPlayer.getPlayer()), otherPlayer.getPlayer().getDisplayName()));

        // Ready / unready
        GuiStateElement confirmButton = new GuiStateElement('r',
                new GuiStateElement.State(
                        change -> gameTrade.setReady(player, true),
                        "ready",
                        new ItemStack(Material.LIME_STAINED_GLASS_PANE),
                        ChatColor.GREEN + "READY",
                        ChatColor.GRAY + "Click to unready"
                ),
                new GuiStateElement.State(
                        change -> gameTrade.setReady(player, false),
                        "unready",
                        new ItemStack(Material.RED_STAINED_GLASS_PANE),
                        ChatColor.RED + "NOT READY",
                        ChatColor.GRAY + "Click to ready"
                )
        );

        confirmButton.setState("unready");
        gui.addElement(confirmButton);

        gui.addElement(new DynamicGuiElement('t', (viewer) -> {
            return new StaticGuiElement('t', otherPlayer.isReady() ? new ItemStack(Material.LIME_STAINED_GLASS_PANE) : new ItemStack(Material.RED_STAINED_GLASS_PANE),
                    click -> true,
                    otherPlayer.isReady() ? ChatColor.GREEN + "READY" : ChatColor.RED + "NOT READY");
        }));

        // TODO: on both ready, start countdown, when ready, dont let them move items

        gui.setCloseAction(close -> {
            List<ItemStack> items = Arrays.stream(inv.getContents()).filter(Objects::nonNull).collect(Collectors.toList());
            player.sendMessage("Inv content size: " + items.size());
            gameTradeCallback.onLeave(gameTrade);
            return false;
        });

        gui.show(player);
    }

    private List<ItemStack> arrayToList(ItemStack[] array) {
        List<ItemStack> list = new ArrayList<>();
        for (ItemStack itemStack : array) {
            if (itemStack != null && itemStack.getType() != Material.AIR) {
                list.add(itemStack);
            }
        }
        return list;
    }

    private String[] getGuiSetup() {
        return new String[] {
                "  h   g  ",
                " iii jjj ",
                " iii jjj ",
                " iii jjj ",
                " iii jjj ",
                "  r   t  ",
        };
    }
}
