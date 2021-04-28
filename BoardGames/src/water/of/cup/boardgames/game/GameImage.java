package water.of.cup.boardgames.game;

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
		return new int[] {image.getWidth(), image.getHeight()};
	}
	
}
