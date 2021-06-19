package water.of.cup.boardgames.config;

import org.bukkit.Sound;

public class GameSound {

    private final String name;
    private final Sound sound;

    public GameSound(String name, Sound sound) {
        this.name = name;
        this.sound = sound;
    }

    public String getName() {
        return name;
    }

    public Sound getSound() {
        return sound;
    }
}
