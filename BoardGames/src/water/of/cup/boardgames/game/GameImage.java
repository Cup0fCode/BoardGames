package water.of.cup.boardgames.game;

import java.awt.Font;
import java.awt.image.BufferedImage;

import water.of.cup.boardgames.image_handling.ImageManager;
import water.of.cup.boardgames.image_handling.ImageUtils;

public class GameImage {
	BufferedImage image;
	int rotation;
	
	public GameImage(String imageName) {
		setImage(imageName);
	}
	
	public GameImage(String imageName, int rotation) {
		this(imageName);
		this.rotation = rotation;
	}
	
	public GameImage(BufferedImage image, int rotation) {
		assert image != null;
		this.image = ImageUtils.copyImage(image);
		this.rotation = rotation;
	}

	public GameImage combine(GameImage otherGameImage, int[] location) {
		GameImage newGameImage = this.clone();
		newGameImage.addGameImage(otherGameImage, location);
		return newGameImage;
	}
	
	public void setImage(String imageName) {
		BufferedImage image = ImageManager.getImage(imageName);
		assert image != null;
		this.image = ImageUtils.copyImage(image);
	}
	
	public void addGameImage(GameImage otherGameImage, int[] location) {
		image = ImageUtils.combineImages(image, otherGameImage.getImage(rotation), location);
	}
	
	public BufferedImage getImage() {
		return getImage(0);
	}
	
	public BufferedImage getImage(int addedRotation) {
		int rot = (rotation + addedRotation) % 4;
		return ImageUtils.rotateImage(image, rot);
		//return image;
	}
	
	public GameImage clone() {
		return new GameImage(image, rotation);
	}

	public int[] getDimensions() {
		// TODO Auto-generated method stub
//		int[] dim = new int[] {image.getWidth(), image.getHeight()};
//		int i = 0;
//		while (i < rotation) {
//			dim = MathUtils.rotatePointAroundPoint90Degrees(new double[] {0,0}, dim);
//			i++;
//		}
		
		return (rotation % 2 == 0) ? new int[] {image.getWidth(), image.getHeight()} : new int[] {image.getHeight(), image.getWidth()};
	}

	public void cropMap(int[] loc) { //crops image to map location
		image = image.getSubimage(loc[0] * 128, loc[1] * 128, 128, 128);
	}
	
	// write a string to a button
//	public void writeText(String text, int[] loc1, int[] loc2, int size) {
//		int size = Math.min(loc2[1] - loc1[1], (loc2[0] - loc1[0]) / text.length());
//		writeText(text, loc1, size);
//	}
	
	public void writeText(String text, int[] loc, int size) {
		Font font = new Font("Serif", 0, size);
		image = ImageUtils.writeCenterText(image, loc, text, font);
	}
	
	public void writeText(String text, int[] loc, Font font) {
		image = ImageUtils.writeCenterText(image, loc, text, font);
	}
	
	public void setRotation(int r) {
		rotation = r;
	}
}
