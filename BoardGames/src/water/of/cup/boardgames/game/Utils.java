package water.of.cup.boardgames.game;

public class Utils {
	public static int[] rotatePointAroundPoint90Degrees(int[] p1, int[] p2) {
		// p2 is being rotated around p1 90
		return new int[] { p1[0] - p2[1] - p1[1], p1[1] + p2[0] - p1[0] };
	}
}
