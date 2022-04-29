package water.of.cup.boardgames.game.games.chess;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Rotation;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

public class ChessUtils {

	private static String[] xPositionLetters = new String[] { "a", "b", "c", "d", "e", "f", "g", "h" };

	public static boolean locationThreatened(int[] location, ChessPiece[][] originalBoard) {
		// returns true if a piece could be taken at this location
		ChessPiece[][] board = cloneBoard(originalBoard);

		// check if piece in danger
		for (int y = 0; y < board.length; y++) {
			for (int x = 0; x < board[0].length; x++) {
				ChessPiece attackingPiece = board[y][x];
				if (attackingPiece != null
						&& !attackingPiece.getColor().equals(board[location[1]][location[0]].getColor())
						&& attackingPiece.getMoves(board, new int[] { x, y }, new boolean[8][8],
								new ArrayList<String>(), true)[location[1]][location[0]])
					return true;
			}
		}
		return false;
	}

	public static int[] locateKing(ChessPiece[][] board, String color) {
		for (int y = 0; y < board.length; y++) {
			for (int x = 0; x < board[0].length; x++) {
				if (board[y][x] != null && board[y][x].equals(ChessPiece.valueOf(color + "_KING")))
					return new int[] { x, y };
			}
		}
		return new int[] { -1, -1 };
	}

	public static int[] locatePromotedPawn(ChessPiece[][] board, String color) {
		int y = 7;
		ChessPiece pieceType = ChessPiece.BLACK_PAWN;
		if (color.equals("WHITE")) {
			y = 0;
			pieceType = ChessPiece.WHITE_PAWN;
		}

		for (int x = 0; x < 8; x++) {
			if (board[y][x] != null && board[y][x].equals(pieceType))
				return new int[] { x, y };
		}

		return new int[] { -1, -1 };
	}

	public static ChessPiece[][] cloneBoard(ChessPiece[][] originalBoard) {
		ChessPiece[][] board = new ChessPiece[originalBoard.length][originalBoard[0].length];

		// clone board
		int i = 0;
		for (ChessPiece[] line : originalBoard) {
			board[i] = line.clone();
			i++;
		}
		return board;
	}

	public static boolean[][] allMovesForColor(ChessPiece[][] board, String color, boolean[][] movedPieces,
			ArrayList<String> record) {
		boolean[][] moves = new boolean[board.length][board[0].length];

		for (int y = 0; y < board.length; y++) {
			for (int x = 0; x < board[0].length; x++) {
				ChessPiece attackingPiece = board[y][x];
				if (attackingPiece != null && attackingPiece.getColor().equals(color)) {
					moves = combineMoves(moves,
							attackingPiece.getMoves(board, new int[] { x, y }, movedPieces, record));
				} // board[selectedLocation[1]][selectedLocation[0]].getMoves(board,
					// selectedLocation,
					// game.getMovedPieces(), game.getRecord());
			}
		}
		return moves;

	}

	public static boolean[][] combineMoves(boolean[][] moves1, boolean[][] moves2) {
		boolean[][] moves = new boolean[moves1.length][moves1[0].length];
		for (int y = 0; y < moves1.length; y++) {
			for (int x = 0; x < moves1[0].length; x++) {
				if (moves1[y][x] || moves2[y][x]) {
					moves[y][x] = true;
				}
			}
		}
		return moves;
	}

	public static boolean colorHasMoves(ChessPiece[][] board, String color, ArrayList<String> record) {
		boolean[][] moves = allMovesForColor(board, color, new boolean[8][8], record);
		for (boolean[] line : moves) {
			for (boolean move : line) {
				if (move)
					return true;
			}
		}
		return false;
	}

	public static String getNotationPosition(int x, int y) {
		return xPositionLetters[x] + (8 - y);
	}

	public static String boardToString(ChessPiece[][] board) {
		String stringBoard = "";
		for (ChessPiece[] row : board) {
			for (ChessPiece piece : row) {
				if (piece == null) {
					stringBoard += " ";
					continue;
				}

				String pieceChar = piece.getNotationCharacter();

				if (piece.toString().contains("PAWN"))
					pieceChar = "P";

				if (piece.getColor().equals("BLACK"))
					pieceChar = pieceChar.toLowerCase();

				stringBoard += pieceChar;

			}
		}

		return stringBoard;
	}

	public static ChessPiece[][] boardFromString(String boardString) {
		ChessPiece[][] board = new ChessPiece[8][8];

		for (int i = 0; i < 63; i++) {
			board[i / 8][i % 8] = ChessPiece.getPieceByNotationCharacter(boardString.charAt(i));
		}

		return board;
	}
}
