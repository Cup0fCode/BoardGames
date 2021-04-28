package water.of.cup.boardgames.game;

import java.util.Arrays;

public class MathUtils {
	public static int[] rotatePointAroundPoint90Degrees(double[] p1, int[] p2) {
		// p2 is being rotated around p1 90
		return new int[] { (int) (- p2[1] + p1[0] + p1[1]), (int) (p2[0] - p1[0] + p1[1]) };
	}

	public static int[][] rotateMatrix(int mat[][]) {
		// rotates Matrix 90 degrees
		int lX = mat[0].length;
		int lY = mat.length;
		
		int[][] newMat = new int[lX][lY];
		// Consider all squares one by one
		for (int x = 0; x < lX; x++) {
			for (int y = 0; y < lY; y++) {
				newMat[lX - x - 1][y] = mat[y][x];
			}
		}
		return newMat;
	}
	
	public static int[][] cloneIntMatrix(int[][] matrix) {
		return Arrays.stream(matrix).map(int[]::clone).toArray(int[][]::new);
	}
}
