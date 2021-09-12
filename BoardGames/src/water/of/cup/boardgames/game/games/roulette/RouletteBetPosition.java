package water.of.cup.boardgames.game.games.roulette;

public class RouletteBetPosition {
	private int[] loc;
	private int[] dimensions;
	private String type;
	private int position;
	
	public RouletteBetPosition(int[] dimensions, int[] loc, int position, String type) {
		this.type = type;
		this.position = position;
		this.dimensions = dimensions;
		this.loc = loc;
	}

	public int[] getLoc() {
		return loc;
	}
	public void setLoc(int[] loc) {
		this.loc = loc;
	}
	public int[] getDimensions() {
		return dimensions;
	}
	public void setDimensions(int[] dimensions) {
		this.dimensions = dimensions;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getPosition() {
		return position + 1;
	}
	public void setPosition(int position) {
		this.position = position;
	}

	public boolean clicked(int[] clickLoc) {
		int[] p1 = loc.clone();
		int[] p2 = new int[] { loc[0] + dimensions[0], loc[1] + dimensions[1]};

		// check if clicked loc not between p1 & p2
		if (clickLoc[0] < p1[0] == clickLoc[0] < p2[0] || clickLoc[1] < p1[1] == clickLoc[1] < p2[1])
			return false;
		
		return true;
	}
	

}
