package water.of.cup.boardgames.game;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Button {
	private ArrayList<GamePlayer> visiblePlayers; // only these players can see the button
	private Game game;
	private String name;
	private boolean clickAble;
	private int[] location;
	private int rotation;
	private BufferedImage image;
	private Boolean turnBased; // if true, button is only clickable when it is a player's turn
	private Boolean renderTurnBased; // if true, button is only rendered when it is a player's turn

	public boolean clicked(GamePlayer gamePlayer, int[] loc) {
		if (!visiblePlayers.contains(gamePlayer))
			return false;
		
		if (turnBased && !game.getTurn().equals(gamePlayer))
			return false;
		
		
		int[] p1 = location.clone();
		int[] p2 = new int[] {location[0], location[1]};
		
		// rotate p2
		int i = 0;
		while (i < rotation) {
			p2 = Utils.rotatePointAroundPoint90Degrees(p1, p2);
			i++;
		}
		
		// check if clicked loc is between p1 & p2
		if (loc[0] < p1[0] == loc[0] < p2[0] || loc[1] < p1[1] == loc[1] < p2[1])
			return false;
		
		return true;
	}
}
