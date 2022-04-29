package water.of.cup.boardgames.game.games.uno;

import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.game.GameImage;
import water.of.cup.boardgames.image_handling.ImageManager;

public class UnoCard {
	private String color; // {RED, YELLOW, GREEN, BLUE, ALL}
	private String type;
	
	public UnoCard(String type, String color) {
		this.color = color;
		this.type = type;
	}
	
	public String getColor() {
		return color;
	}
	
	public String getType() {
		return type;
	}
	
	public void setColor(String color) {
		this.color = color;
	}
	
	public GameImage getGameImage() {
		GameImage image = new GameImage(BoardGames.getImageManager().getImage("UNOCARD_" + color), 0);
		image.addGameImage(new GameImage(BoardGames.getImageManager().getImage("UNOCARD_" + type), 0), new int[] {0,0});
		return image;
	}
	
	public boolean matches(UnoCard otherCard) {
		if (color.equals("ALL") || otherCard.getColor().equals(color))
			return true;
		if (type.equals(otherCard.getType()))
			return true;
		return false;
	}
	
	public String[] getActions() {
		// DRAW2
		// DRAW4
		// SKIP
		// REVERSE
		// WILD
		if (type.equals("DRAW2")) {
			return new String[] {"DRAW2", "SKIP"};
		}
		if (type.equals("REVERSE")) {
			return new String[] {"REVERSE"};
		}
		if (type.equals("SKIP")) {
			return new String[] {"SKIP"};
		}
		if (type.equals("WILD")) {
			return new String[] {"WILD"};
		}
		if (type.equals("WILDDRAW4")) {
			return new String[] {"WILD", "DRAW4", "SKIP"};
		}
		
		return new String[0];
	}
	
}
