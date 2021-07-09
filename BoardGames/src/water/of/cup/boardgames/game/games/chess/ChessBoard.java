package water.of.cup.boardgames.game.games.chess;

import java.util.ArrayList;

public class ChessBoard {
	private ChessPiece[][] structure;
	private ArrayList<String> record;
	private boolean[][] movedPieces;
	private int fiftyMoveDrawCount;
	private ArrayList<String> boardStates;

	public ChessBoard() {
		fiftyMoveDrawCount = 0;
		structure = new ChessPiece[][] {
				{ ChessPiece.BLACK_ROOK, ChessPiece.BLACK_KNIGHT, ChessPiece.BLACK_BISHOP, ChessPiece.BLACK_QUEEN,
						ChessPiece.BLACK_KING, ChessPiece.BLACK_BISHOP, ChessPiece.BLACK_KNIGHT,
						ChessPiece.BLACK_ROOK },
				{ ChessPiece.BLACK_PAWN, ChessPiece.BLACK_PAWN, ChessPiece.BLACK_PAWN, ChessPiece.BLACK_PAWN,
						ChessPiece.BLACK_PAWN, ChessPiece.BLACK_PAWN, ChessPiece.BLACK_PAWN, ChessPiece.BLACK_PAWN },
				{ null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null },
				{ null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null },
				{ ChessPiece.WHITE_PAWN, ChessPiece.WHITE_PAWN, ChessPiece.WHITE_PAWN, ChessPiece.WHITE_PAWN,
						ChessPiece.WHITE_PAWN, ChessPiece.WHITE_PAWN, ChessPiece.WHITE_PAWN, ChessPiece.WHITE_PAWN },
				{ ChessPiece.WHITE_ROOK, ChessPiece.WHITE_KNIGHT, ChessPiece.WHITE_BISHOP, ChessPiece.WHITE_QUEEN,
						ChessPiece.WHITE_KING, ChessPiece.WHITE_BISHOP, ChessPiece.WHITE_KNIGHT,
						ChessPiece.WHITE_ROOK } };
						
		movedPieces = new boolean[8][8];
		record = new ArrayList<String>();
		record.add("start");
		boardStates = new ArrayList<String>();

	}

	protected ChessPiece[][] getStructure() {
		return structure;
	}

	protected boolean move(int[] selectedPiece, int[] loc, String turn) {
		ChessPiece piece = structure[selectedPiece[1]][selectedPiece[0]];
		if (piece == null) {
			return false; // piece doesn't exist
		}

		boolean[][] moves = piece.getMoves(structure, selectedPiece, movedPieces, record);
		if (!moves[loc[1]][loc[0]]) {
			return false; // move is not possible
		}

		// check if piece has correct color
		if (!piece.getColor().equals(turn))
			return false;

		String otherTurn = "WHITE".equals(turn) ? "BLACK" : "WHITE";

		// piece move logic:
		String notation = "";

		// reset fiftyMoveDrawCount if piece is pawn
		if (piece.toString().contains("PAWN")) {
			fiftyMoveDrawCount = 0;
		}

		// check if move is castle
		ChessPiece otherPiece = structure[loc[1]][loc[0]];
		if (otherPiece != null && piece.toString().contains("KING") && otherPiece.toString().contains("ROOK")
				&& piece.getColor().equals(otherPiece.getColor())) {

			if (loc[0] == 0) {
				// left rook castle
				structure[selectedPiece[1]][2] = piece;
				structure[selectedPiece[1]][selectedPiece[0]] = null;
				structure[loc[1]][3] = otherPiece;
				structure[loc[1]][loc[0]] = null;

				notation = "0-0-0";

			} else {
				// right rook castle
				structure[selectedPiece[1]][6] = piece;
				structure[selectedPiece[1]][selectedPiece[0]] = null;
				structure[loc[1]][5] = otherPiece;
				structure[loc[1]][loc[0]] = null;
				notation = "0-0";
			}

			movedPieces[loc[1]][loc[0]] = true;
			movedPieces[selectedPiece[1]][selectedPiece[0]] = true;
		} else {
			// check if move is en passent
			if (piece.toString().contains("PAWN") && otherPiece == null && loc[0] != selectedPiece[0]) {
				// take piece passed over
				structure[selectedPiece[1]][loc[0]] = null;
			}

			// non-castle move made
			notation = piece.getNotationCharacter()
					+ ChessUtils.getNotationPosition(selectedPiece[0], selectedPiece[1]);

			// add piece taken to notation and reset fiftyMoveDrawCount
			if (otherPiece != null) {
				notation += "x";
				fiftyMoveDrawCount = 0;
			}
			notation += ChessUtils.getNotationPosition(loc[0], loc[1]);

			structure[loc[1]][loc[0]] = piece;
			structure[selectedPiece[1]][selectedPiece[0]] = null;

			movedPieces[loc[1]][loc[0]] = true;
			movedPieces[selectedPiece[1]][selectedPiece[0]] = true;

		}

		// run only if no pawn promotion
		if (getPawnPromotion().equals("NONE")) {

			// Check if move creates check
			if (ChessUtils.locationThreatened(ChessUtils.locateKing(structure, otherTurn), structure)) {
				notation += "+";
				// check if move creates checkmate
				if (!ChessUtils.colorHasMoves(structure, otherTurn, record)) {
					notation += "+";
				}
			}

		}

		// log move
		record.add(notation);
		// Bukkit.getLogger().info(notation);

		// add boardState
		String boardString = ChessUtils.boardToString(structure);
		boardStates.add(boardString);

		return true;
	}

	protected String checkGameOver() {
		// CheckIfGameOver by fifty move draw count:
		if (fiftyMoveDrawCount >= 50) {
			return "DRAW";
		}

		// check if game over by stalemate:
		if (getBoardRepeats(boardStates.get(boardStates.size() - 1)) >= 3) {
			// draw
			return "DRAW";
		}

		// CheckIfGameOver by check:
		for (String turn : new String[] { "WHITE", "BLACK" })

			if (!ChessUtils.colorHasMoves(structure, turn, record)) {
				// Check if winner
				if (ChessUtils.locationThreatened(ChessUtils.locateKing(structure, turn), structure)) {
					// Other side won
					if (turn.equals("WHITE")) {
						return "BLACK";
					} else {
						return "WHITE";
					}
				} else {
					// tied game
					return "DRAW";
				}
			}

		return "";
	}

	private int getBoardRepeats(String boardString) {
		int count = 0;

		for (String pastBoard : boardStates) {
			if (pastBoard.equals(boardString))
				count++;
		}
		return count;
	}

	protected String getPawnPromotion() {
		for (int i = 0; i < 8; i++) {
			if ((structure[7][i] == ChessPiece.BLACK_PAWN)) {
				return "BLACK";
			} else if (structure[0][i] == ChessPiece.WHITE_PAWN) {
				return "WHITE";
			}
		}
		return "NONE";
	}

	protected void setPiece(int[] loc, ChessPiece piece) {
		structure[loc[1]][loc[0]] = piece;
	}

	protected boolean[][] getMoves(int[] selected) {
		return structure[selected[1]][selected[0]].getMoves(structure, selected, movedPieces, record);
	}

	public void promotePawn(String teamTurn, ChessPiece piece) {
		for (int i = 0; i < 8; i++) {
			if (structure[7][i] == ChessPiece.BLACK_PAWN && teamTurn.equals("BLACK")) {
				structure[7][i] = piece;
				return;
			} else if (structure[0][i] == ChessPiece.WHITE_PAWN && teamTurn.equals("WHITE")) {
				structure[0][i] = piece;
				return;
			}
		}
	}
}
