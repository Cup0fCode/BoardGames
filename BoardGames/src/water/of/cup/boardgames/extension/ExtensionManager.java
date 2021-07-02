package water.of.cup.boardgames.extension;

import org.bukkit.Bukkit;
import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.image_handling.ImageManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ExtensionManager {

    // Inspired by https://github.com/PlaceholderAPI/PlaceholderAPI
    private static final Set<MethodSignature> ABSTRACT_EXPANSION_METHODS = Arrays.stream(BoardGamesExtension.class.getDeclaredMethods())
            .filter(method -> Modifier.isAbstract(method.getModifiers()))
            .map(method -> new MethodSignature(method.getName(), method.getParameterTypes()))
            .collect(Collectors.toSet());

    private final HashMap<String, BoardGamesExtension> extensions = new HashMap<>();

    private final File folder;

    public ExtensionManager() {
        this.folder = new File(BoardGames.getInstance().getDataFolder(), "extensions");

        if (!this.folder.exists() && !folder.mkdirs()) {
            Bukkit.getLogger().warning("[BoardGames] Error while trying to create extensions folder");
        }
    }

    public void loadExtensions() {
        boolean didLoad = this.registerExtensions();

        if(!didLoad)
            return;

        this.loadExtensionImages();

        for(BoardGamesExtension boardGamesExtension : extensions.values()) {
            for(Class<? extends Game> boardGame : boardGamesExtension.getGames()) {
                BoardGames.getInstance().getGameManager().registerGames(boardGame);
            }
        }
    }

    private void loadExtensionImages() {
        for(BoardGamesExtension boardGamesExtension : extensions.values()) {
            File boardFile = getFileFromURL(boardGamesExtension);
            if(boardFile == null) {
                Bukkit.getLogger().warning("[BoardGames] Error while loading images.");
                continue;
            }

            File[] listOfFiles = boardFile.listFiles();

            if(listOfFiles == null) {
                Bukkit.getLogger().warning("[BoardGames] Error while loading images.");
                continue;
            }

            for(File file : listOfFiles) {
                try {
                    BufferedImage image = ImageIO.read(file);
                    ImageManager.addImage(file.getName(), image);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
//        getFileFromURL
    }

//    private void loadExtensionImages() {
//        File[] listOfFiles = this.imageFolder.listFiles();
//
//        if(listOfFiles == null) return;
//
//        for (File file : listOfFiles) {
//            if(file.exists()) {
//                try {
//                    BufferedImage image = ImageIO.read(file);
//                    ImageManager.addImage(file.getName(), image);
//                } catch (IOException e) {
//                    Bukkit.getLogger().warning("[BoardGames] Error while reading extension image " + file.getName());
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    private List<Class<? extends BoardGamesExtension>> findExtensions() {
        return Arrays.stream(folder.listFiles((dir, name) -> name.endsWith(".jar")))
                .map(this::getExtension).collect(Collectors.toList());
    }

    private Class<? extends BoardGamesExtension> getExtension(File file) {
        try {
            Class<? extends BoardGamesExtension> extensionClass = ExtensionUtil.findClass(file, BoardGamesExtension.class);

            if(extensionClass == null) {
                Bukkit.getLogger().warning("[BoardGames] Error while loading the extension " + file.getName() + " (Invalid jar)");
                return null;
            }

            Set<MethodSignature> expansionMethods = Arrays.stream(extensionClass.getDeclaredMethods())
                    .map(method -> new MethodSignature(method.getName(), method.getParameterTypes()))
                    .collect(Collectors.toSet());

            if (!expansionMethods.containsAll(ABSTRACT_EXPANSION_METHODS)) {
                Bukkit.getLogger().warning("[BoardGames] Error while loading the extension " + file.getName() + " (Invalid methods)");
                return null;
            }

            return extensionClass;
        } catch (Exception e) {
            Bukkit.getLogger().warning("[BoardGames] Error while loading the extension " + file.getName() + " (Exception)");
            e.printStackTrace();
            return null;
        }
    }

    private boolean registerExtensions() {
        List<Class<? extends BoardGamesExtension>> classes = findExtensions();

        final long registered = classes.stream()
                .filter(Objects::nonNull)
                .map(this::register)
                .filter(Optional::isPresent)
                .count();

        if(registered == 0) {
            Bukkit.getLogger().info("[BoardGames] No extensions were loaded.");
            return false;
        } else {
            Bukkit.getLogger().info("[BoardGames] Loaded extensions.");
            return true;
        }
    }

    private Optional<BoardGamesExtension> register(final Class<? extends BoardGamesExtension> clazz) {
        try {
            BoardGamesExtension boardGamesExtension = createExtensionInstance(clazz);
            extensions.put("test", boardGamesExtension);
            return Optional.of(boardGamesExtension);
        } catch (LinkageError | NullPointerException ex) {
            Bukkit.getLogger().warning("[BoardGames] Error while loading extension");
            ex.printStackTrace();
        }

        return Optional.empty();
    }

    private BoardGamesExtension createExtensionInstance(final Class<? extends BoardGamesExtension> clazz) throws LinkageError {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (final Exception ex) {
            if (ex.getCause() instanceof LinkageError) {
                throw ((LinkageError) ex.getCause());
            }

            Bukkit.getLogger().warning("[BoardGames] Error while loading the extensions.");
            return null;
        }
    }

    private File getFileFromURL(BoardGamesExtension boardGamesExtension) {
        URL url = this.getClass().getClassLoader().getResource(boardGamesExtension.getResourcePath());
        File file = null;
        try {
            file = new File(url.toURI());
        } catch (URISyntaxException e) {
            file = new File(url.getPath());
        } finally {
            return file;
        }
    }
}
