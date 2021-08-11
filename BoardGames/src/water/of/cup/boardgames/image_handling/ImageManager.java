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

public class ImageManager {

	private static final HashMap<String, BufferedImage> images = new HashMap<>(); //image name then image

	@Nullable
	public static BufferedImage getImage(String name) {
		if(images.containsKey(name)) {
			return images.get(name);
		}

		// Try to find custom image
		InputStream is = getCustomImage(name);

		if(is == null) {
			String filePath = "water/of/cup/boardgames/images/" + name + ".png";
			is = BoardGames.getInstance().getResource(filePath);
		}

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
		BufferedImage customImage = null;
		if(getCustomImage(name) != null) {
			try {
				customImage = ImageIO.read(getCustomImage(name));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		images.put(name, customImage != null ? customImage : image);
	}

	private static InputStream getCustomImage(String name) {
		File customImages = new File(BoardGames.getInstance().getDataFolder() + "/custom_images");
		InputStream is = null;
		if(customImages.exists()) {
			File customImage = new File(customImages + "/" + name + ".png");
			if(customImage.exists() && customImage.isFile()) {
				try {
					is = new FileInputStream(customImage);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		return is;
	}
}