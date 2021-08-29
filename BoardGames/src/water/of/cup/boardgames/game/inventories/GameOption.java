package water.of.cup.boardgames.game.inventories;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import water.of.cup.boardgames.config.ConfigUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class GameOption {

    private final String key;
    private final Material material;
    private final GameOptionType optionType;
    private final String defaultValue;
    private final List<String> customValues;
    private final String label;
    private final boolean requiresEconomy;
    private final int minIntValue;
    private final int maxIntValue;

    public GameOption(String key, Material material, GameOptionType optionType, String label, String defaultValue, boolean requiresEconomy, int minIntValue, int maxIntValue) {
        this.key = key;
        this.material = material;
        this.optionType = optionType;
        this.label = label;
        this.defaultValue = defaultValue;
        this.customValues = null;
        this.requiresEconomy = requiresEconomy;
        this.minIntValue = minIntValue;
        this.maxIntValue = maxIntValue;
    }

    public GameOption(String key, Material material, GameOptionType optionType, String label, String defaultValue, boolean requiresEconomy) {
        this.key = key;
        this.material = material;
        this.optionType = optionType;
        this.label = label;
        this.defaultValue = defaultValue;
        this.customValues = null;
        this.requiresEconomy = requiresEconomy;
        this.minIntValue = 0;
        this.maxIntValue = Integer.MAX_VALUE;
    }

    public GameOption(String key, Material material, GameOptionType optionType, String label, String defaultValue, List<String> customValues) {
        this.key = key;
        this.material = material;
        this.optionType = optionType;
        this.label = label;
        this.defaultValue = defaultValue;
        this.customValues = customValues;
        this.requiresEconomy = false;
        this.minIntValue = 0;
        this.maxIntValue = 0;
    }

    public String getKey() {
        return key;
    }

    public GameOptionType getOptionType() {
        return optionType;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    @Nullable
    public List<String> getCustomValues() {
        return customValues;
    }

    public Material getMaterial() {
        return material;
    }

    public String getLabel() {
        return this.label;
    }

    public boolean requiresEconomy() {
        return requiresEconomy;
    }

    public int getMinIntValue() {
        return minIntValue;
    }

    public int getMaxIntValue() {
        return maxIntValue;
    }

    public static GameOption getWagerGameOption() {
        return new GameOption("wager", Material.GOLD_INGOT, GameOptionType.COUNT, ConfigUtil.GUI_WAGER_LABEL.toString(), "0", true);
    }

    public static GameOption getTeamSelectGameOption(ArrayList<String> teams) {
        return new GameOption("team", Material.PAPER, GameOptionType.COUNT, ConfigUtil.GUI_TEAM_LABEL.toString(), teams.get(0), teams);
    }

    public static GameOption getTradeItemsOption() {
        List<String> options = new ArrayList<>();
        options.add(ConfigUtil.GUI_WAGERITEMS_ENABLED_LABEL.toString());
        options.add(ConfigUtil.GUI_WAGERITEMS_DISABLED_LABEL.toString());
        return new GameOption("trade", Material.CHEST, GameOptionType.TOGGLE, ConfigUtil.GUI_WAGERITEMS_LABEL.toString(), options.get(1), options);
    }
}
