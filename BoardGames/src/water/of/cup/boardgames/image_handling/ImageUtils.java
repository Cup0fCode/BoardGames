package water.of.cup.boardgames.image_handling;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import water.of.cup.boardgames.game.MathUtils;

public class ImageUtils {
	public static BufferedImage rotateImage(BufferedImage image, int rotation) {
		double w = image.getWidth();
		double h = image.getHeight();

		if (rotation % 2 == 1) {
			w = image.getHeight();
			h = image.getWidth();
		}

		AffineTransform scaleTransform = new AffineTransform();
		// last-in-first-applied: rotate, scale

		if (rotation == 1)
			scaleTransform.translate((w - h) / 2, (w - h) / 2);
		if (rotation == 3)
			scaleTransform.translate((h - w) / 2, (h - w) / 2);

		scaleTransform.rotate(Math.PI / 2 * rotation, w / 2, h / 2);
		AffineTransformOp scaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);
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
		// assert image1.getWidth() > image2.getWidth() + location[0];
		// assert image1.getHeight() > image2.getHeight() + location[1];

		// create the new image
		BufferedImage combined = new BufferedImage(image1.getWidth(), image1.getHeight(), image1.getType());

		// paint both images, preserving the alpha channels
		Graphics g = combined.getGraphics();
		g.drawImage(image1, 0, 0, null);
		g.drawImage(image2, location[0], location[1], null);

		g.dispose();

		return combined;
	}

	public static BufferedImage writeCenterText(BufferedImage image, int[] location, String string, Font font) {
		return writeCenterText(image, location, string, font, Color.BLACK);
	}

	public static BufferedImage writeCenterText(BufferedImage image, int[] location, String string, Font font,
			Color color) {
		BufferedImage clone = copyImage(image);
		Graphics2D g2 = (Graphics2D) clone.getGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setFont(font);

		// get metrics from the graphics
		FontMetrics metrics = g2.getFontMetrics(font);
		// get the height of a line of text in this
		// font and render context
		int hgt = metrics.getLeading() + metrics.getAscent();
		// get the advance of my text in this font
		// and render context
		int adv = metrics.stringWidth(string);
		// calculate the size of a box to hold the
		// text with some padding.
		int[] pos = new int[] { location[0] - adv / 2, location[1] + hgt / 2 };

		g2.setColor(color);
		g2.drawString(string, pos[0], pos[1]);
		g2.dispose();
		return clone;
	}
	
	public static BufferedImage resize(BufferedImage img, double change) { 
		int newW = (int) (img.getWidth() * change);
		int newH = (int) (img.getHeight() * change);
		
	    Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_REPLICATE);
	    BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

	    Graphics2D g2d = dimg.createGraphics();
	    g2d.drawImage(tmp, 0, 0, null);
	    g2d.dispose();

	    return dimg;
	}  
}
