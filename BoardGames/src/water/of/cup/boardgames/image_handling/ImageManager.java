package water.of.cup.boardgames.image_handling;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;

import org.bukkit.Bukkit;

import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.extension.BoardGamesExtension;

public class ImageManager {

	private static final HashMap<String, BufferedImage> images = new HashMap<>(); //image name then image

	@Nullable
	public static BufferedImage getImage(String name) {
		if(images.containsKey(name)) {
			return images.get(name);
		}

		String filePath = "water/of/cup/boardgames/images/" + name + ".png";
		InputStream is = BoardGames.getInstance().getResource(filePath);

		if(is == null) return null;

		try {
			BufferedImage image = ImageIO.read(is);
			images.put(name, image);
			return image;
		} catch (IOException e) {
			return null;
		}
	}

	public static void addImage(String name, BufferedImage image) {
		images.put(name, image);
	}
}