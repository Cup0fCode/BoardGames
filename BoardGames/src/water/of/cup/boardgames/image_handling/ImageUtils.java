package water.of.cup.boardgames.image_handling;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import water.of.cup.boardgames.game.MathUtils;

public class ImageUtils {
	public static BufferedImage rotateImage(BufferedImage image, int rotation) {
		int w = image.getWidth();
		int h = image.getHeight();
		
		AffineTransform scaleTransform = new AffineTransform();
		// last-in-first-applied: rotate, scale
		//scaleTransform.scale(scaleX, scaleY);
		scaleTransform.rotate(Math.PI / 2 * rotation, w / 2, h / 2);
		AffineTransformOp scaleOp = new AffineTransformOp(
		        scaleTransform, AffineTransformOp.TYPE_BILINEAR);
		return scaleOp.filter(image, null);
	}

	// from
	// https://stackoverflow.com/questions/3514158/how-do-you-clone-a-bufferedimage
	public static BufferedImage copyImage(BufferedImage source) {
		BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
		Graphics g = b.getGraphics();
		g.drawImage(source, 0, 0, null);
		g.dispose();
		return b;
	}

	// referenced:
	public static BufferedImage combineImages(BufferedImage image1, BufferedImage image2, int[] location) {
		assert image1.getWidth() > image2.getWidth() + location[0];
		assert image1.getHeight() > image2.getHeight() + location[1];
		
		// create the new image
		BufferedImage combined = new BufferedImage(image1.getWidth(), image1.getHeight(), image1.getType());

		// paint both images, preserving the alpha channels
		Graphics g = combined.getGraphics();
		g.drawImage(image1, 0, 0, null);
		g.drawImage(image2, location[0], location[1], null);

		g.dispose();
		
		return combined;
	}
}
