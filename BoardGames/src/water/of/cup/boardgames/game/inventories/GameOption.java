package water.of.cup.boardgames.game.inventories;

import org.bukkit.Material;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class GameOption {

    private final String key;
    private final Material material;
    private final GameOptionType optionType;
    private final String defaultValue;
    private final List<String> customValues;

    public GameOption(String key, Material material, GameOptionType optionType, String defaultValue) {
        this.key = key;
        this.material = material;
        this.optionType = optionType;
        this.defaultValue = defaultValue;
        this.customValues = null;
    }

    public GameOption(String key, Material material, GameOptionType optionType, String defaultValue, List<String> customValues) {
        this.key = key;
        this.material = material;
        this.optionType = optionType;
        this.defaultValue = defaultValue;
        this.customValues = customValues;
    }

    public String getKey() {
        return key;
    }

    public GameOptionType getOptionType() {
        return optionType;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    @Nullable
    public List<String> getCustomValues() {
        return customValues;
    }

    public Material getMaterial() {
        return material;
    }
}
