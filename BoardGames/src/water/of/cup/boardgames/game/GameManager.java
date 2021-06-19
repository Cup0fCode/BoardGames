package water.of.cup.boardgames.game;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.util.Vector;

import water.of.cup.boardgames.BoardGames;

public class GameManager {
	private HashMap<String, Class<? extends Game>> nameToGameTypes;

	private BoardGames instance = BoardGames.getInstance();

	private ArrayList<Game> games = new ArrayList<>();
	private int lastGameId;

	public GameManager() {
		lastGameId = 0;
		nameToGameTypes = new HashMap<String, Class<? extends Game>>();
	}

	@SuppressWarnings("unchecked")
	public void registerGames(Class<? extends Game>... gameTypes) {
		for (Class<? extends Game> gameType : gameTypes) {
			Constructor<? extends Game> cons;
			try {
				cons = gameType.getConstructor(int.class);
				Game game = cons.newInstance(0);
				nameToGameTypes.put(game.getName(), gameType);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public Game newGame(String name, int rotation) {
		try {
			for (String ckey : nameToGameTypes.keySet()) {
				if (!ckey.equals(name))
					continue;
				Constructor<? extends Game> cons = nameToGameTypes.get(ckey).getConstructor(int.class);
				return cons.newInstance(rotation);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public Game newGame(BoardItem item, int rotation) {
		return newGame(item.getName(), rotation);
	}
	
	public String[] getGameNames() {
		return nameToGameTypes.keySet().toArray(new String[nameToGameTypes.keySet().size()]);
	}

	public boolean isValidGame(String name) {
		for(String gameName : getGameNames()) {
			if(gameName.equals(name)) return true;
		}

		return false;
	}

	public void addGame(Game game) {
		if (!games.contains(game)) {
			games.add(game);
		}
	}

	public boolean removeGame(Game game) {
		if (!games.contains(game)) {
			return false;
		}

		games.remove(game);
		return true;
	}

//	public boolean hasGame(ItemStack item) {
//		for (Game game : games) {
//			if (game.getItem().equals(item)) {
//				return true;
//			}
//		}
//		return false;
//	}

//	public Game getGame(ItemStack item) {
//		for (Game game : games) {
//			if (game.getItem().equals(item)) {
//				return game;
//			}
//		}
//		return null;
//	}

//	public void loadGames() {
//		File folder = new File(BoardGames.getInstance().getDataFolder() + "/saved_games");
//		File[] listOfFiles = folder.listFiles();
//
//		for (File file : listOfFiles) {
//			if (file.isFile()) {
//				try {
//					int gameId = Integer.parseInt(file.getName().split("_")[1].split(Pattern.quote("."))[0]);
//					BufferedReader br = new BufferedReader(new FileReader(file));
//					String encodedData = br.readLine();
//
//					ItemStack chessBoardItem = new ItemStack(Material.FILLED_MAP, 1);
//					MapMeta mapMeta = (MapMeta) chessBoardItem.getItemMeta();
//					MapView mapView = Bukkit.getMap(gameId);
//					mapMeta.setMapView(mapView);
//					chessBoardItem.setItemMeta(mapMeta);
//
//					ChessGame newChessGame = new ChessGame(chessBoardItem, encodedData, gameId);
//					newChessGame.renderBoardForPlayers();
//
//					games.add(newChessGame);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}

	public Game getGameByPlayer(Player player) {
		for (Game game : games) {
			if (game.hasPlayer(player))
				return game;
		}
		return null;
	}

	public Game getGameByQueuePlayer(Player player) {
		for (Game game : games) {
			if (game.getPlayerQueue().contains(player))
				return game;
		}

		return null;
	}

	public Game getGameByDecisionQueuePlayer(Player player) {
		for (Game game : games) {
			if (game.getPlayerDecideQueue().contains(player))
				return game;
		}

		return null;
	}

	public Game getGameByGameId(int id) {
		for (Game game : games) {
			if (game.getGameId() == id)
				return game;
		}
		return null;
	}

	public ArrayList<Game> getGamesInRegion(World world, Vector p1, Vector p2) {
		ArrayList<Game> games = new ArrayList<Game>();
		for (Entity entity : world.getEntities()) {
			if (!(entity instanceof ItemFrame))
				continue;
			ItemFrame frame = (ItemFrame) entity;
			ItemStack item = frame.getItem();
			if (item != null && item.getType() == Material.FILLED_MAP && ((MapMeta) item.getItemMeta()).hasMapId()) {
				Game game = getGameByGameId(((MapMeta) item.getItemMeta()).getMapId());
				if (game != null && entity.getLocation().toVector().isInAABB(p1, p2))
					games.add(game);
			}
		}
		return games;
	}

	public void saveGames() {
		for (Game game : this.games) {
			Bukkit.getLogger().info("[BoardGames] storing game: " + game.getName());
			this.storeGame(game);
		}
	}

	private void storeGame(Game game) {
		String gameData = game.toString();
		String gameId = game.getGameId() + "";
		File file = new File(instance.getDataFolder(), "saved_games/game_" + gameId + ".txt");

		if (!file.exists()) {
			try {
				file.createNewFile();
				Bukkit.getLogger().severe("[BoardGames] Created game file for gameId: " + gameId);
			} catch (IOException e1) {
				Bukkit.getLogger().severe("[BoardGames] Error creating game file for gameId: " + gameId);
				e1.printStackTrace();
			}
		}

		try {
			Bukkit.getLogger().severe("[BoardGames] Writing game data to gameId: " + gameId);
			Files.write(Paths.get(file.getPath()), gameData.getBytes());
		} catch (IOException e) {
			Bukkit.getLogger().severe("[BoardGames] Error writing to gameId: " + gameId);
			e.printStackTrace();
		}
	}

	protected int nextGameId() {
		// TODO Auto-generated method stub
		return ++lastGameId;
	}
}
