package water.of.cup.boardgames.game;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import net.md_5.bungee.api.ChatColor;

public class BoardItem extends ItemStack {
	String name;

	public BoardItem(String name, ItemStack itemStack) {
		super(itemStack);
		this.name = name;
		assert itemStack.hasItemMeta();
		ItemMeta itemMeta = itemStack.getItemMeta();
		PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
		persistentDataContainer.set(Game.getGameNameKey(), PersistentDataType.STRING, name);
		itemMeta.setDisplayName(ChatColor.BLUE + name);
		setItemMeta(itemMeta);
	}

	public BoardItem(ItemStack itemStack) {
		super(itemStack);
		assert isBoardItem(itemStack);
		ItemMeta itemMeta = itemStack.getItemMeta();
		PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
		name = persistentDataContainer.get(Game.getGameNameKey(), PersistentDataType.STRING);
	}

	public static boolean isBoardItem(ItemStack itemStack) {
		if (!itemStack.hasItemMeta())
			return false;

		PersistentDataContainer persistentDataContainer = itemStack.getItemMeta().getPersistentDataContainer();

		// check that map has appropriate persistent data
		if (!persistentDataContainer.has(Game.getGameNameKey(), PersistentDataType.STRING))
			return false;
		return true;
	}

	public String getName() {
		return name;
	}

}
