package water.of.cup.boardgames;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;

public class ImageManager {

	private HashMap<String, BufferedImage> images; //image name then image

	public boolean loadImages() {
		boolean customImages = BoardGames.getInstance().getConfig().getBoolean("settings.chessboard.customImages");
		images = new HashMap<>();

		// Make sure all images are places inside /ChessBoards/images on the server
//		for (ChessPiece piece : ChessPiece.values()) {
//			try {
//				if(!customImages) {
//					String filePath = "water/of/cup/chessboards/images/" + name + ".png";
//					InputStream is = BoardGames.getInstance().getResource(filePath).;
//
//					if(is == null) {
//						Bukkit.getLogger().warning("[ChessBoards] Error loading default images at path " + filePath);
//						return false;
//					}
//
//					BufferedImage image = ImageIO.read(is);
//					images.put(piece, image);
//				} else {
//					File file = new File(
//							BoardGames.getInstance().getDataFolder() + "/images/" + piece.toString() + ".png");
//					BufferedImage image = ImageIO.read(file);
//					images.put(piece, image);
//				}
//			} catch (IOException e) {
//				Bukkit.getLogger().warning("[BoardGames] Error loading images");
//				e.printStackTrace();
//				return false;
//			}
//		}

		return true;
	}

	public BufferedImage getImage(String name) {
		return images.get(name);
	}
}