package water.of.cup.boardgames.extension;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.image_handling.ImageManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import static java.util.stream.Collectors.toList;

public class ExtensionUtil {

//    https://github.com/PlaceholderAPI/PlaceholderAPI/blob/master/src/main/java/me/clip/placeholderapi/util/FileUtil.java
    @Nullable
    public static <T> Class<? extends T> findClass(@NotNull final File file,
                                                   @NotNull final Class<T> clazz) throws IOException, ClassNotFoundException {
        if (!file.exists()) {
            return null;
        }

        final URL jar = file.toURI().toURL();
        final URLClassLoader loader = new URLClassLoader(new URL[]{jar}, clazz.getClassLoader());
        final List<String> matches = new ArrayList<>();
        final List<Class<? extends T>> classes = new ArrayList<>();

        try (final JarInputStream stream = new JarInputStream(jar.openStream())) {
            JarEntry entry;
            while ((entry = stream.getNextJarEntry()) != null) {
                final String name = entry.getName();
                if (name.isEmpty() || !name.endsWith(".class")) {
                    continue;
                }

                matches.add(name.substring(0, name.lastIndexOf('.')).replace('/', '.'));
            }

            for (final String match : matches) {
                try {
                    final Class<?> loaded = loader.loadClass(match);
                    if (clazz.isAssignableFrom(loaded)) {
                        classes.add(loaded.asSubclass(clazz));
                    }
                } catch (final NoClassDefFoundError ignored) {
                }
            }
        }
        if (classes.isEmpty()) {
            loader.close();
            return null;
        }
        return classes.get(0);
    }

    public static void loadExtensionImages() {
        File extensionFolder = new File(BoardGames.getInstance().getDataFolder(), "extensions");
        List<File> extensionJars = Arrays.stream(extensionFolder.listFiles((dir, name) -> name.endsWith(".jar"))).collect(toList());

        for (File file : extensionJars) {
            try (JarFile jarFile = new JarFile(file)) {
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry je = entries.nextElement();
                    if (je.getName().endsWith(".png")) {
                        JarEntry fileEntry = jarFile.getJarEntry(je.getName());
                        InputStream is = jarFile.getInputStream(fileEntry);
                        BufferedImage image = ImageIO.read(is);

                        String fileName = getFileName(je.getName());
                        ImageManager.addImage(fileName, image);

                        Bukkit.getLogger().info("[BoardGames] Loaded extension image: " + fileName);
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static String getFileName(String name) {
        String[] split = name.split("/");
        if(split.length == 0) return "";
        String newName = split[split.length - 1];
        return newName.substring(0, newName.indexOf('.'));
    }

}
