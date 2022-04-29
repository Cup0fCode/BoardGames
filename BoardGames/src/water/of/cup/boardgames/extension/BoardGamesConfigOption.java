package water.of.cup.boardgames.extension;

import water.of.cup.boardgames.config.ConfigInterface;

public class BoardGamesConfigOption implements ConfigInterface {

    private final String path;
    private final String defaultValue;

    public BoardGamesConfigOption(String path, String defaultValue) {
        this.path = path;
        this.defaultValue = defaultValue;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public String getDefaultValue() {
        return this.defaultValue;
    }
}
