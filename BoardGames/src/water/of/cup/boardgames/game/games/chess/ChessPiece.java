package water.of.cup.boardgames.game.games.chess;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import water.of.cup.boardgames.image_handling.ImageManager;
import water.of.cup.boardgames.image_handling.ImageUtils;

public enum ChessPiece {
	BLACK_PAWN, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK, BLACK_QUEEN, BLACK_KING, WHITE_PAWN, WHITE_BISHOP, WHITE_KNIGHT,
	WHITE_ROOK, WHITE_QUEEN, WHITE_KING;

	public boolean[][] getMoves(ChessPiece[][] board, int[] pieceLoc, boolean[][] movedPieces,
			ArrayList<String> record) {
		return getMoves(board, pieceLoc, movedPieces, record, false);
	}

	public boolean[][] getMoves(ChessPiece[][] board, int[] pieceLoc, boolean[][] movedPieces, ArrayList<String> record,
			boolean canEndangerKing) {
		boolean[][] moves = new boolean[8][8];

		int x = pieceLoc[0];
		int y = pieceLoc[1];

		int[][] directions;

		// moves for each piece
		int row = 0;
		switch (this) {
		case BLACK_PAWN:
		case WHITE_PAWN:
			int forward = 0;
			if (this.getColor().equals("BLACK")) {
				forward = 1;
				row = 1;
			} else {
				forward = -1;
				row = 6;
			}
			// pawn move up
			if (checkMovePossible(pieceLoc, board, x, y + forward, true, false)) {
				if (checkMovePossible(pieceLoc, board, x, y + forward, canEndangerKing, false))
					moves[y + forward][x] = true;

				// pawn move second up
				if (y == row && checkMovePossible(pieceLoc, board, x, y + forward * 2, canEndangerKing, false)) {
					moves[y + forward * 2][x] = true;
				}
			}

			// pawn take sideways
			if (checkMovePossible(pieceLoc, board, x + 1, y + forward, canEndangerKing, true, true)) {
				moves[y + forward][x + 1] = true;
			}
			if (checkMovePossible(pieceLoc, board, x - 1, y + forward, canEndangerKing, true, true)) {
				moves[y + forward][x - 1] = true;

			}

			// En passant take
			int oppositeRow = 0;
			ChessPiece oppositePieceType = null;
			if (getColor().equals("WHITE")) {
				oppositePieceType = ChessPiece.BLACK_PAWN;
				oppositeRow = 1;
				row = 3;
			} else if (getColor().equals("BLACK")) {
				oppositePieceType = ChessPiece.WHITE_PAWN;
				row = 4;
				oppositeRow = 6;
			}
			int[] files = new int[] { x + 1, x - 1 };

			// check if pieces are on row
			if (y == row) {
				for (int file : files) {
					if (file >= 0 && file < 8 && oppositePieceType.equals(board[row][file])) {
						// check that last move was pawn in adjacent file moving up two squares
						if (record.size() > 0 && record.get(record.size() - 1).replaceAll("\\+", "")
								.equals(ChessUtils.getNotationPosition(file, oppositeRow)
										+ ChessUtils.getNotationPosition(file, row))) {
							// check that move is possible
							ChessPiece[][] testBoard = ChessUtils.cloneBoard(board);
							testBoard[row][file] = null;
							if (checkMovePossible(pieceLoc, testBoard, file, y + forward, canEndangerKing, false)) {
								moves[y + forward][file] = true;
							}
						}
					}
				}
			}

			break;
		case BLACK_BISHOP:
		case WHITE_BISHOP:
			directions = new int[][] { { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 } };

			// loop through directions to see what moves are available
			for (int[] direction : directions) {
				int cX = x;
				int cY = y;

				while (checkMovePossible(pieceLoc, board, cX + direction[0], cY + direction[1], true)) {

					cX += direction[0];
					cY += direction[1];
					if (checkMovePossible(pieceLoc, board, cX, cY, canEndangerKing))
						moves[cY][cX] = true;
					if (checkMovePossible(pieceLoc, board, cX, cY, true, true, true))
						break;
				}
			}
			break;
		case BLACK_KING:
		case WHITE_KING:
			// check if king can castle

			if (canEndangerKing == false && !record.get(record.size() - 1).contains("+")) {
				
				
				if (getColor().equals("WHITE")) {
					row = 7;
				} else if (getColor().equals("BLACK")) {
					row = 0;
				}
				// check if king has moved
				if (!movedPieces[row][4]) {
					// left rook
					if (!movedPieces[row][0]) {
						boolean movesWork = true;
						for (int i = 2; i < 4; i++) {
							// check if the two nearest tiles between king and rook are safe
							if (!checkMovePossible(pieceLoc, board, i, row, false, false, false)) {
								movesWork = false;
								break;
							}
						}
						// check if the b column tile is empty
						if (board[row][1] != null)
							movesWork = false;
						if (movesWork)
							moves[row][0] = true;
					}

					// right rook
					if (!movedPieces[row][7]) {
						boolean movesWork = true;
						for (int i = 5; i < 7; i++) {
							// check if all the tiles between king and rook are safe
							if (!checkMovePossible(pieceLoc, board, i, row, false, false, false)) {
								movesWork = false;
								break;
							}
						}
						if (movesWork)
							moves[row][7] = true;
					}
				}
			}
			directions = new int[][] { { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 }, { 1, 0 }, { -1, 0 }, { 0, 1 },
					{ 0, -1 } };
			// loop through directions to see what moves are available
			for (int[] direction : directions) {
				if (checkMovePossible(pieceLoc, board, x + direction[0], y + direction[1], canEndangerKing)) {
					moves[y + direction[1]][x + direction[0]] = true;
				}
			}

			break;
		case BLACK_KNIGHT:
		case WHITE_KNIGHT:
			directions = new int[][] { { 2, 1 }, { 2, -1 }, { -2, 1 }, { -2, -1 }, { 1, 2 }, { -1, 2 }, { 1, -2 },
					{ -1, -2 } };
			// loop through directions to see what moves are available
			for (int[] direction : directions) {
				if (checkMovePossible(pieceLoc, board, x + direction[0], y + direction[1], canEndangerKing)) {
					moves[y + direction[1]][x + direction[0]] = true;
				}
			}
			break;
		case BLACK_QUEEN:
		case WHITE_QUEEN:
			directions = new int[][] { { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 }, { 1, 0 }, { -1, 0 }, { 0, 1 },
					{ 0, -1 } };

			// loop through directions to see what moves are available
			for (int[] direction : directions) {
				int cX = x;
				int cY = y;

				while (checkMovePossible(pieceLoc, board, cX + direction[0], cY + direction[1], true)) {
					cX += direction[0];
					cY += direction[1];

					if (checkMovePossible(pieceLoc, board, cX, cY, canEndangerKing))
						moves[cY][cX] = true;
					if (checkMovePossible(pieceLoc, board, cX, cY, true, true, true))
						break;
				}
			}

			break;
		case BLACK_ROOK:
		case WHITE_ROOK:
			directions = new int[][] { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };

			// loop through directions to see what moves are available
			for (int[] direction : directions) {
				int cX = x;
				int cY = y;

				while (checkMovePossible(pieceLoc, board, cX + direction[0], cY + direction[1], true)) {
					cX += direction[0];
					cY += direction[1];
					if (checkMovePossible(pieceLoc, board, cX, cY, canEndangerKing))
						moves[cY][cX] = true;
					if (checkMovePossible(pieceLoc, board, cX, cY, true, true, true))
						break;
				}
			}
			break;
		default:
			break;

		}

		return moves;
	}

	public String getColor() {
		// return the color of the chess piece
		if (this.toString().contains("BLACK"))
			return "BLACK";
		if (this.toString().contains("WHITE"))
			return "WHITE";
		return "EMPTY";
	}

	public boolean checkMovePossible(int[] pieceLoc, ChessPiece[][] board, int x, int y, boolean canEndangerKing) {
		return checkMovePossible(pieceLoc, board, x, y, canEndangerKing, true, false);
	}

	public boolean checkMovePossible(int[] pieceLoc, ChessPiece[][] board, int x, int y, boolean canEndangerKing,
			boolean canTake) {
		return checkMovePossible(pieceLoc, board, x, y, canEndangerKing, canTake, false);
	}

	public boolean checkMovePossible(int[] pieceLoc, ChessPiece[][] board, int x, int y, boolean canEndangerKing,
			boolean canTake, boolean mustTake) {

		// check if newLoc is within the boards size
		if (!(x < 8 && y >= 0 && y < 8 && x >= 0))
			return false;

		// check if newLoc is empty
		if ((board[y][x] == null)) {
			// check if piece can only take
			if (mustTake)
				return false;
		} else
		// check if pieces are the same color
		if (this.getColor().equals(board[y][x].getColor())) {
			return false;
		} else
		// pieces are opposite colors
		if (canTake == false) {
			return false;
		}

		// check if move endangers king
		ChessPiece[][] testBoard = ChessUtils.cloneBoard(board);
		testBoard[y][x] = this;
		testBoard[pieceLoc[1]][pieceLoc[0]] = null;
		if (!canEndangerKing && !getColor().equals("EMPTY")
				&& ChessUtils.locationThreatened(ChessUtils.locateKing(testBoard, getColor()), testBoard))
			return false;

		// checks concluded
		return true;
	}
	
	public static ChessPiece getPieceByNotationCharacter(char c) {
		
		switch (c) {
		case 'P':
			return ChessPiece.WHITE_PAWN;
		case 'p':
			return ChessPiece.BLACK_PAWN;
		case 'B':
			return ChessPiece.WHITE_BISHOP;
		case 'b':
			return ChessPiece.BLACK_BISHOP;
		case 'R':
			return ChessPiece.WHITE_ROOK;
		case 'r':
			return ChessPiece.BLACK_ROOK;
		case 'N':
			return ChessPiece.WHITE_KNIGHT;
		case 'n':
			return ChessPiece.BLACK_KNIGHT;
		case 'K':
			return ChessPiece.WHITE_KING;
		case 'k':
			return ChessPiece.BLACK_KING;
		case 'Q':
			return ChessPiece.WHITE_QUEEN;
		case 'q':
			return ChessPiece.BLACK_QUEEN;
		}
		return null;
		
	}

	public String getNotationCharacter() {
		switch (this) {
		case BLACK_BISHOP:
		case WHITE_BISHOP:
			return "B";
		case BLACK_KING:
		case WHITE_KING:
			return "K";
		case BLACK_KNIGHT:
		case WHITE_KNIGHT:
			return "N";
		case BLACK_PAWN:
		case WHITE_PAWN:
			return "";
		case BLACK_QUEEN:
		case WHITE_QUEEN:
			return "Q";
		case BLACK_ROOK:
		case WHITE_ROOK:
			return "R";
		}
		return "";
	}
	
	public BufferedImage getImage() {
		BufferedImage image =  ImageManager.getImage(this.toString());
		if (this.getColor() == "BLACK")
			image = ImageUtils.rotateImage(image, 2);
		return image;
	}
}