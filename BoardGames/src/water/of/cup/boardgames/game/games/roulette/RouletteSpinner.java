package water.of.cup.boardgames.game.games.roulette;

import water.of.cup.boardgames.game.GameImage;

public class RouletteSpinner {
	private int rotation; // 0 - 37
	private static int[] values = {0, 28, 9, 26,30,11,7,20,32,17,5,22,34,15,3,24,36,13,1,100,27,10,25,29,12,8,19,31,18,6,21,33,16,4,23,35,14,2}; 
	private int ballPos; // 0 - 37
	private Roulette game;
	private GameImage ballImage;
	
	public RouletteSpinner(Roulette game) {
		rotation = 0;
		ballPos = 0;
		this.game = game;
		ballImage = new GameImage("ROULETTE_BALL");
	}
	
	public void spin() {
		rotation += 1;
		rotation %= 38;
		moveBall(2);
	}
	
	public GameImage getGameImage() {
		GameImage image = new GameImage("ROULETTE_SPINNER");
		image.rotateSquareImage((double) (38 - rotation ) / 38 * 360); 
		int[] ballLoc = {128 - 4, 128 - 4};
		double angle = (double) (ballPos + .5) / 38 * Math.PI * 2; 
		
		ballLoc[0] += Math.cos(angle) * 66;
		ballLoc[1] += Math.sin(angle) * 66;
		image.addGameImage(ballImage, ballLoc);
		
		return image;
	}
	
	public void moveBall(int amt) {
		ballPos += amt;
		ballPos %= 38;
	}
	
	public int getValue() {
		//return values[rotation];
		return values[(rotation + ballPos + 10) % 38];
	}
	
	
}
