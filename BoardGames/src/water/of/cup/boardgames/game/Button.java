package water.of.cup.boardgames.game;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import water.of.cup.boardgames.game.maps.Screen;
import water.of.cup.boardgames.image_handling.ImageManager;

public class Button {
	private ArrayList<GamePlayer> visiblePlayers; // only these players can see the button
	private boolean visibleForAll;
	private Game game;
	private String name;
	private boolean clickAble;
	private int[] location;
	private int rotation;
	private GameImage image;
	private Boolean turnBased; // if true, button is only clickable when it is a player's turn
	private Boolean renderTurnBased; // if true, button is only rendered when it is a player's turn
	
	private Boolean visible;

	private Screen screen;

	// public Button(Game game, BufferedImage image, int[] loc, String name, )

	public Button(Game game, String imageName, int[] location, int rotation, String name) {
		image = new GameImage(ImageManager.getImage(imageName), rotation);
		visiblePlayers = new ArrayList<GamePlayer>();
		visibleForAll = true;
		this.game = game;
		this.name = name;
		this.clickAble = false;
		this.location = location;
		this.rotation = rotation;
		image = new GameImage(ImageManager.getImage(imageName), rotation);
		turnBased = false;
		renderTurnBased = false;
		visible = true;
	}

	public Button(Game game, GameImage gameImage, int[] location, int rotation, String name) {
		visiblePlayers = new ArrayList<GamePlayer>();
		visibleForAll = true;
		this.game = game;
		this.name = name;
		this.clickAble = false;
		this.location = location;
		this.rotation = rotation;
		image = gameImage.clone();
		image.setRotation(rotation);
		turnBased = false;
		renderTurnBased = false;
		visible = true;
	}

	public void changeLocationByRotation() { // 14 / 2 + 14 - (14 + 8) / 2
		int[] dim = image.getDimensions();
		if (rotation >= 2) {
			location[1] -= dim[1] - 1;
		}
		if (rotation == 1 || rotation == 2) {
			location[0] -= dim[0] - 1;
		}

	}

	public boolean clicked(GamePlayer gamePlayer, int[] loc) {
		if (!clickAble)
			return false;

		if (!visibleForPlayer(gamePlayer))
				return false;

//		if (turnBased && !game.getTurn().equals(gamePlayer))
//			return false;
		
		if (image.equals(null))
			return false;

		int[] p1 = location.clone();
		int[] p2 = new int[] { location[0] + image.getDimensions()[0], location[1] + image.getDimensions()[1]};

		// check if clicked loc not between p1 & p2
		if (loc[0] < p1[0] == loc[0] < p2[0] || loc[1] < p1[1] == loc[1] < p2[1])
			return false;
		
		return true;
	}

	public boolean visibleForPlayer(GamePlayer player) {
		if (!visible)
			return false;
		
		if (visibleForAll)
			return true;

		if (!visiblePlayers.contains(player))
			return false;

		if (!game.getTurn().equals(player) && renderTurnBased)
			return false;

		return true;
	}

	public Boolean getRenderTurnBased() {
		return renderTurnBased;
	}

	public void setRenderTurnBased(Boolean renderTurnBased) {
		this.renderTurnBased = renderTurnBased;
	}

	public GameImage getImage() {
		return image;
	}

	public void setClickable(boolean c) {
		clickAble = c;
	}

	public void setImage(GameImage image) {
		this.image = image;
	}

	public void setImage(String imageName) {
		image = new GameImage(ImageManager.getImage(imageName), rotation);
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isVisibleForAll() {
		return visibleForAll;
	}

	public void setVisibleForAll(boolean visibleForAll) {
		this.visibleForAll = visibleForAll;
	}

	public int[] getLocation() {
		return location;
	}

	public void addVisiblePlayer(GamePlayer player) {
		visiblePlayers.add(player);
	}
	
	public ArrayList<GamePlayer> getVisablePlayers() {
		return visiblePlayers;
	}

	public String getName() {
		return "" + name;
	}

	public void setName(String string) {
		name = string;
	}

	public void setScreen(Screen screen) {
		this.screen = screen;
	}

	public Screen getScreen() {
		return screen;
	}

	public void setImage(BufferedImage image) {
		this.image = new GameImage(image, 0);
		
	}

	public int getRotation() {
		// TODO Auto-generated method stub
		return rotation;
	}
}
