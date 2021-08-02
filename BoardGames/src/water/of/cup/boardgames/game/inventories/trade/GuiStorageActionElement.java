package water.of.cup.boardgames.game.inventories.trade;

import de.themoep.inventorygui.GuiElement;
import de.themoep.inventorygui.GuiStorageElement;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

public class GuiStorageActionElement extends GuiElement  {

    private final Inventory storage;
    private final int invSlot;
    private Runnable applyStorage;
    private Function<ValidatorInfo, Boolean> itemValidator;

    public GuiStorageActionElement(char slotChar, Inventory storage, GameTradePlayer gameTradePlayer, Action action) {
        super(slotChar, null);
        this.invSlot = -1;
        this.applyStorage = null;
        this.itemValidator = null;
        setAction(click -> {
            if(gameTradePlayer.isReady()) {
                return true;
            }
            if (getStorageSlot(click.getWhoClicked(), click.getSlot()) < 0) {
                return true;
            }
            ItemStack storageItem = getStorageItem(click.getWhoClicked(), click.getSlot());
            ItemStack slotItem = click.getEvent().getView().getTopInventory().getItem(click.getSlot());
            if (slotItem == null && storageItem != null && storageItem.getType() != Material.AIR
                    || storageItem == null && slotItem != null && slotItem.getType() != Material.AIR
                    || storageItem != null && !storageItem.equals(slotItem)) {
                gui.draw(click.getEvent().getWhoClicked(), false);
                return true;
            }
            ItemStack movedItem = null;
            switch (click.getEvent().getAction()) {
                case NOTHING:
                case CLONE_STACK:
                    return false;
                case MOVE_TO_OTHER_INVENTORY:
                    if (click.getEvent().getRawSlot() < click.getEvent().getView().getTopInventory().getSize()) {
                        movedItem = null;
                    } else {
                        movedItem = click.getEvent().getCurrentItem();
                    }
                    break;
                case HOTBAR_MOVE_AND_READD:
                case HOTBAR_SWAP:
                    int button = click.getEvent().getHotbarButton();
                    if (button < 0) {
                        return true;
                    }
                    ItemStack hotbarItem = click.getEvent().getView().getBottomInventory().getItem(button);
                    if (hotbarItem != null) {
                        movedItem = hotbarItem.clone();
                    }
                    break;
                case PICKUP_ONE:
                case DROP_ONE_SLOT:
                    movedItem = click.getEvent().getCurrentItem().clone();
                    movedItem.setAmount(movedItem.getAmount() - 1);
                    break;
                case DROP_ALL_SLOT:
                    movedItem = null;
                    break;
                case PICKUP_HALF:
                    movedItem = click.getEvent().getCurrentItem().clone();
                    movedItem.setAmount(movedItem.getAmount() / 2);
                    break;
                case PLACE_SOME:
                    if (click.getEvent().getCurrentItem() == null) {
                        movedItem = click.getEvent().getCursor();
                    } else {
                        movedItem = click.getEvent().getCurrentItem().clone();
                        if (movedItem.getAmount() + click.getEvent().getCursor().getAmount() < movedItem.getMaxStackSize()) {
                            movedItem.setAmount(movedItem.getAmount() + click.getEvent().getCursor().getAmount());
                        } else {
                            movedItem.setAmount(movedItem.getMaxStackSize());
                        }
                    }
                    break;
                case PLACE_ONE:
                    if (click.getEvent().getCurrentItem() == null) {
                        movedItem = click.getEvent().getCursor().clone();
                        movedItem.setAmount(1);
                    } else {
                        movedItem = click.getEvent().getCursor().clone();
                        movedItem.setAmount(click.getEvent().getCurrentItem().getAmount() + 1);
                    }
                    break;
                case PLACE_ALL:
                    movedItem = click.getEvent().getCursor().clone();
                    if (click.getEvent().getCurrentItem() != null && click.getEvent().getCurrentItem().getAmount() > 0) {
                        movedItem.setAmount(click.getEvent().getCurrentItem().getAmount() + movedItem.getAmount());
                    }
                    break;
                case PICKUP_ALL:
                case SWAP_WITH_CURSOR:
                    movedItem = click.getEvent().getCursor();
                    break;
                case COLLECT_TO_CURSOR:
                    if (click.getEvent().getCursor() == null
                            || click.getEvent().getCurrentItem() != null && click.getEvent().getCurrentItem().getType() != Material.AIR) {
                        return true;
                    }
                    simulateCollectToCursor(click);
                    return false;
                default:
                    click.getEvent().getWhoClicked().sendMessage(ChatColor.RED + "The action " + click.getEvent().getAction() + " is not supported! Sorry about that :(");
                    return true;
            }

            boolean result = !setStorageItem(click.getWhoClicked(), click.getSlot(), movedItem);
            action.onClick(null);
            return result;
        });
        this.storage = storage;
    }

    @Override
    public ItemStack getItem(HumanEntity who, int slot) {
        int index = getSlotIndex(slot);
        if (index > -1 && index < storage.getSize()) {
            return storage.getItem(index);
        }
        return null;
    }

    /**
     * Get the {@link Inventory} that this element is linked to.
     * @return  The {@link Inventory} that this element is linked to.
     */
    public Inventory getStorage() {
        return storage;
    }

    /**
     * Get the storage slot index that corresponds to the InventoryGui slot
     * @param player    The player which is using the GUI view
     * @param slot      The slot in the GUI
     * @return      The index of the storage slot or <code>-1</code> if it's outside the storage
     */
    private int getStorageSlot(HumanEntity player, int slot) {
        int index = invSlot != -1 ? invSlot : getSlotIndex(slot, gui.getPageNumber(player));
        if (index < 0 || index >= storage.getSize()) {
            return -1;
        }
        return index;
    }

    /**
     * Get the item in the storage that corresponds to the InventoryGui slot
     * @param slot      The slot in the GUI
     * @return      The {@link ItemStack} or <code>null</code> if the slot is outside of the item's size
     * @deprecated Use {@link #getStorageItem(HumanEntity, int)}
     */
    @Deprecated
    public ItemStack getStorageItem(int slot) {
        return getStorageItem(null, slot);
    }

    /**
     * Get the item in the storage that corresponds to the InventoryGui slot
     * @param player    The player which is using the GUI view
     * @param slot      The slot in the GUI
     * @return      The {@link ItemStack} or <code>null</code> if the slot is outside of the item's size
     */
    public ItemStack getStorageItem(HumanEntity player, int slot) {
        int index = getStorageSlot(player, slot);
        if (index == -1) {
            return null;
        }
        return storage.getItem(index);
    }

    /**
     * Set the item in the storage that corresponds to the InventoryGui slot.
     * @param slot  The slot in the GUI
     * @param item  The {@link ItemStack} to set
     * @return      <code>true</code> if the item was set; <code>false</code> if the slot was outside of this storage
     * @deprecated Use {@link #setStorageItem(HumanEntity, int, ItemStack)}
     */
    @Deprecated
    public boolean setStorageItem(int slot, ItemStack item) {
        return setStorageItem(null, slot, item);
    }

    /**
     * Set the item in the storage that corresponds to the InventoryGui slot.
     * @param player    The player using the GUI view
     * @param slot      The slot in the GUI
     * @param item      The {@link ItemStack} to set
     * @return      <code>true</code> if the item was set; <code>false</code> if the slot was outside of this storage
     */
    public boolean setStorageItem(HumanEntity player, int slot, ItemStack item) {
        int index = getStorageSlot(player, slot);
        if (index == -1) {
            return false;
        }
        if (!validateItem(slot, item)) {
            return false;
        }
        storage.setItem(index, item);
        if (applyStorage != null) {
            applyStorage.run();
        }
        return true;
    }

    /**
     * Get the runnable that applies the storage
     * @return The storage applying runnable; might be null
     */
    public Runnable getApplyStorage() {
        return applyStorage;
    }

    /**
     * Set what should be done to apply the storage.
     * Not necessary if the storage is directly backed by a real inventory.
     * @param applyStorage  How to apply the storage; can be null if nothing should be done
     */
    public void setApplyStorage(Runnable applyStorage) {
        this.applyStorage = applyStorage;
    }

    /**
     * Get the item validator
     * @return  The item validator
     */
    public Function<ValidatorInfo, Boolean> getItemValidator() {
        return itemValidator;
    }

    /**
     * Set a function that can validate whether or not an item can fit in the slot
     * @param itemValidator The item validator that takes a {@link ValidatorInfo} and returns <code>true</code> for items that
     *                      should and <code>false</code> for items that should not work in that slot
     */
    public void setItemValidator(Function<ValidatorInfo, Boolean> itemValidator) {
        this.itemValidator = itemValidator;
    }

    /**
     * Validate whether or not an item can be put in a slot with the item validator set in {@link #setItemValidator(Function)}
     * @param slot  The slot the item should be tested for
     * @param item  The item to test
     * @return      <code>true</code> for items that should and <code>false</code> for items that should not work in that slot
     */
    public boolean validateItem(int slot, ItemStack item) {
        return itemValidator == null || itemValidator.apply(new ValidatorInfo(this, slot, item));
    }

    public static class ValidatorInfo {
        private final GuiElement element;
        private final int slot;
        private final ItemStack item;

        public ValidatorInfo(GuiElement element, int slot, ItemStack item) {
            this.item = item;
            this.slot = slot;
            this.element = element;
        }

        public GuiElement getElement() {
            return element;
        }

        public int getSlot() {
            return slot;
        }

        public ItemStack getItem() {
            return item;
        }
    }

    private void simulateCollectToCursor(GuiElement.Click click) {
        ItemStack newCursor = click.getEvent().getCursor().clone();

        boolean itemInGui = false;
        for (int i = 0; i < click.getEvent().getView().getTopInventory().getSize(); i++) {
            if (i != click.getEvent().getRawSlot()) {
                ItemStack viewItem = click.getEvent().getView().getTopInventory().getItem(i);
                if (newCursor.isSimilar(viewItem)) {
                    itemInGui = true;
                }
                GuiElement element = gui.getElement(i);
                if (element instanceof GuiStorageElement) {
                    GuiStorageElement storageElement = (GuiStorageElement) element;
                    ItemStack otherStorageItem = storageElement.getStorageItem(click.getWhoClicked(), i);
                    if (addToStack(newCursor, otherStorageItem)) {
                        if (otherStorageItem.getAmount() == 0) {
                            otherStorageItem = null;
                        }
                        storageElement.setStorageItem(i, otherStorageItem);
                        if (newCursor.getAmount() == newCursor.getMaxStackSize()) {
                            break;
                        }
                    }
                }
            }
        }

        if (itemInGui) {
            click.getEvent().setCurrentItem(null);
            click.getEvent().setCancelled(true);
            if (click.getEvent().getWhoClicked() instanceof Player) {
                ((Player) click.getEvent().getWhoClicked()).updateInventory();
            }

            if (click.getElement() instanceof GuiStorageElement) {
                ((GuiStorageElement) click.getElement()).setStorageItem(click.getWhoClicked(), click.getSlot(), null);
            }

            if (newCursor.getAmount() < newCursor.getMaxStackSize()) {
                Inventory bottomInventory = click.getEvent().getView().getBottomInventory();
                for (ItemStack bottomIem : bottomInventory) {
                    if (addToStack(newCursor, bottomIem)) {
                        if (newCursor.getAmount() == newCursor.getMaxStackSize()) {
                            break;
                        }
                    }
                }
            }
            click.getEvent().setCursor(newCursor);
            gui.draw();
        }
    }

    private static boolean addToStack(ItemStack item, ItemStack add) {
        if (item.isSimilar(add)) {
            int newAmount = item.getAmount() + add.getAmount();
            if (newAmount >= item.getMaxStackSize()) {
                item.setAmount(item.getMaxStackSize());
                add.setAmount(newAmount - item.getAmount());
            } else {
                item.setAmount(newAmount);
                add.setAmount(0);
            }
            return true;
        }
        return false;
    }
}