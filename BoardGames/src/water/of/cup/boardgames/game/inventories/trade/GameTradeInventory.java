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
    private final GameTradeCallback gameTradeCallback;

    public GameTradeInventory(GameTrade gameTrade, GameTradeCallback gameTradeCallback) {
        this.gameTrade = gameTrade;
        this.gameTradeCallback = gameTradeCallback;
    }

    public void build(Player player) {
        build(player, -1);
    }

    public void build(Player player, int timeLeft) {
        String[] guiSetup = getGuiSetup();

        String title = ConfigUtil.GUI_GAME_TRADE_TITLE.buildString(gameTrade.getGame().getAltName());
        if(timeLeft != -1)
            title += " (" + timeLeft + ")";

        InventoryGui gui = new InventoryGui(BoardGames.getInstance(), player, title, guiSetup);

        gui.setFiller(new ItemStack(Material.BLACK_STAINED_GLASS_PANE));

        GameTradePlayer gameTradePlayer = gameTrade.getGameTradePlayer(player);
        GameTradePlayer otherPlayer = gameTrade.getOtherPlayer(gameTradePlayer);
        GuiElementGroup group = new GuiElementGroup('j');

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
                        ConfigUtil.GUI_READY_TEXT.toString()
                ),
                new GuiStateElement.State(
                        change -> gameTrade.setReady(player, false),
                        "unready",
                        new ItemStack(Material.RED_STAINED_GLASS_PANE),
                        ConfigUtil.GUI_UNREADY_TEXT.toString()
                )
        );

        confirmButton.setState(gameTradePlayer.isReady() ? "ready" : "unready");
        gui.addElement(confirmButton);

        gui.addElement(new DynamicGuiElement('t', (viewer) -> {
            return new StaticGuiElement('t', otherPlayer.isReady() ? new ItemStack(Material.LIME_STAINED_GLASS_PANE) : new ItemStack(Material.RED_STAINED_GLASS_PANE),
                    click -> true,
                    otherPlayer.isReady() ? ConfigUtil.GUI_READY_TEXT.toString() : ConfigUtil.GUI_NOT_READY_TEXT.toString());
        }));


        Inventory inv = Bukkit.createInventory(null, InventoryType.CHEST);
        // If the inventory gets refreshed, refill their items
        if(arrayToList(inv.getStorageContents()).size() == 0 && gameTradePlayer.getItems().size() > 0) {
            for (ItemStack itemStack : gameTradePlayer.getItems()) {
                inv.addItem(itemStack);
            }
        }

        // If not ready
        if(timeLeft == -1) {
            gui.addElement(new GuiStorageActionElement('i', inv, gameTradePlayer, click -> {
                List<ItemStack> items = arrayToList(inv.getStorageContents());

                this.gameTrade.updateInventory(player, new ArrayList<>(items));
                return false;
            }));
        } else {
            GuiElementGroup itemGroup = new GuiElementGroup('i');
            for (ItemStack itemStack : gameTradePlayer.getItems()) {
                itemGroup.addElement((new StaticGuiElement('i', itemStack, itemStack.getAmount(), click -> true)));
            }

            gui.addElement(itemGroup);
        }


        gui.setCloseAction(close -> {
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
