package water.of.cup.boardgames.game.maps;

import org.bukkit.block.BlockFace;

public class MapData {
	int mapVal;
	BlockFace blockFace;
	
	public MapData(int mapVal, BlockFace blockFace) {
		this.mapVal = mapVal;
		this.blockFace = blockFace;
	}
	
	public int getMapVal() {
		return mapVal;
	}
	
	public BlockFace getBlockFace() {
		return blockFace;
	}
}
