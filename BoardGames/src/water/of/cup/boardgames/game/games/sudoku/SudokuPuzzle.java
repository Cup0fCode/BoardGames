package water.of.cup.boardgames.game.games.sudoku;

import java.util.Random;

public class SudokuPuzzle {
	private int[][] structure;
	private int[][] filledStructure;

	public SudokuPuzzle() {
		
		structure = new int[9][9];
		filledStructure = new int[9][9];

		int counter = 1, k1, k2;
		generate();
		random_gen(1);
		random_gen(0);

		Random rand = new Random();
		int n[] = { 0, 3, 6 };
		for (int i = 0; i < 2; i++) {
			k1 = n[rand.nextInt(n.length)];
			do {
				k2 = n[rand.nextInt(n.length)];
			} while (k1 == k2);
			if (counter == 1)
				row_change(k1, k2);
			else
				col_change(k1, k2);
			counter++;
		}
		
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				filledStructure[i][j] = structure[i][j];
			}
		}

		// Striking out
		for (k1 = 0; k1 < 9; k1++) {
			for (k2 = 0; k2 < 9; k2++)
				strike_out(k1, k2);
		}
//		System.out.println();
//		for (int i = 0; i < 9; i++) {
//			for (int j = 0; j < 9; j++) {
//				System.out.print(filledStructure[i][j] + "\t");
//			}
//			System.out.println("");
//		}

	}
	
	public boolean check(int[] loc, int n) {
		if (loc[0] > 8 || loc[1] > 8)
			return false;
		
		if (n == structure[loc[1]][loc[0]]) {
			filledStructure[loc[1]][loc[0]] = structure[loc[1]][loc[0]];	
			return true;
		} else {
			filledStructure[loc[1]][loc[0]] = structure[loc[1]][loc[0]];	
			return false;
		}
	}
	
	public int[][] getKnownStructure() {
		return filledStructure;
	}

	private void generate() {
		int k = 1, n = 1;
		for (int i = 0; i < 9; i++) {
			k = n;
			for (int j = 0; j < 9; j++) {
				if (k <= 9) {
					structure[i][j] = k;
					k++;
				} else {
					k = 1;
					structure[i][j] = k;
					k++;
				}
			}
			n = k + 3;
			if (k == 10)
				n = 4;
			if (n > 9)
				n = (n % 9) + 1;
		}
	}

	private void random_gen(int check) {
		int k1, k2, max = 2, min = 0;
		Random r = new Random();
		for (int i = 0; i < 3; i++) {
			// There are three groups.So we are using for loop three times.
			k1 = r.nextInt(max - min + 1) + min;
			// This while is just to ensure k1 is not equal to k2.
			do {
				k2 = r.nextInt(max - min + 1) + min;
			} while (k1 == k2);
			max += 3;
			min += 3;
			// check is global variable.
			// We are calling random_gen two time from the main func.
			// Once it will be called for columns and once for rows.
			if (check == 1)
				// calling a function to interchange the selected rows.
				permutation_row(k1, k2);
			else if (check == 0)
				permutation_col(k1, k2);
		}
	}

	// For row
	private void permutation_row(int k1, int k2) {
		int temp;// k1 and k2 are two rows that we are selecting to interchange.
		for (int j = 0; j < 9; j++) {
			temp = structure[k1][j];
			structure[k1][j] = structure[k2][j];
			structure[k2][j] = temp;
		}
	}

	private void permutation_col(int k1, int k2) {
		int temp;
		for (int j = 0; j < 9; j++) {
			temp = structure[j][k1];
			structure[j][k1] = structure[j][k2];
			structure[j][k2] = temp;
		}
	}

	private void row_change(int k1, int k2) {
		int temp;
		for (int n = 1; n <= 3; n++) {
			for (int i = 0; i < 9; i++) {
				temp = structure[k1][i];
				structure[k1][i] = structure[k2][i];
				structure[k2][i] = temp;
			}
			k1++;
			k2++;
		}
	}

	private void col_change(int k1, int k2) {
		int temp;
		for (int n = 1; n <= 3; n++) {
			for (int i = 0; i < 9; i++) {
				temp = structure[i][k1];
				structure[i][k1] = structure[i][k2];
				structure[i][k2] = temp;
			}
			k1++;
			k2++;
		}
	}

	private void strike_out(int k1, int k2) {
		int row_from;
		int row_to;
		int col_from;
		int col_to;
		int i, j, b, c;
		int rem1, rem2;
		int flag;
		int temp = filledStructure[k1][k2];
		int count = 9;
		for (i = 1; i <= 9; i++) {
			flag = 1;
			for (j = 0; j < 9; j++) {
				if (j != k2) {
					if (i != filledStructure[k1][j]) {
						continue;
					} else {
						flag = 0;
						break;
					}
				}
			}
			if (flag == 1) {
				for (c = 0; c < 9; c++) {
					if (c != k1) {
						if (i != filledStructure[c][k2]) {
							continue;
						} else {
							flag = 0;
							break;
						}
					}
				}
			}
			if (flag == 1) {
				rem1 = k1 % 3;
				rem2 = k2 % 3;
				row_from = k1 - rem1;
				row_to = k1 + (2 - rem1);
				col_from = k2 - rem2;
				col_to = k2 + (2 - rem2);
				for (c = row_from; c <= row_to; c++) {
					for (b = col_from; b <= col_to; b++) {
						if (c != k1 && b != k2) {
							if (i != filledStructure[c][b])
								continue;
							else {
								flag = 0;
								break;
							}
						}
					}
				}
			}
			if (flag == 0)
				count--;
		}
		if (count == 1) {
			filledStructure[k1][k2] = 0;
		}
	}
}