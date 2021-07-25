package water.of.cup.boardgames.game.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.config.ConfigUtil;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.games.chess.OldChessPlayer;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

public class StorageManager {

	private final BoardGames instance = BoardGames.getInstance();
	private static final HikariConfig config = new HikariConfig();
	private static HikariDataSource ds;
	private final ArrayList<GameStorage> gameStores;
	private final ExecutorService executorService = new ForkJoinPool(Runtime.getRuntime().availableProcessors());

	public StorageManager() {
		this.gameStores = new ArrayList<>();

		initialize();
	}

	public void addGameStorage(GameStorage gameStorage) {
		if (!hasGameStore(gameStorage)) {
			this.gameStores.add(gameStorage);

			gameStorage.initializeConfig();

			// init table
			initializeGameStorage(gameStorage);
		}
	}

	private void initializeGameStorage(GameStorage gameStorage) {
		String tableName = gameStorage.getTableName();
		StorageType[] storageTypes = gameStorage.getGameStores();

		StringBuilder tableSqlBuilder = new StringBuilder();

		tableSqlBuilder.append("CREATE TABLE IF NOT EXISTS `").append(tableName).append("` (");
		tableSqlBuilder.append("`id` int PRIMARY KEY AUTO_INCREMENT,");
		tableSqlBuilder.append("`uuid` varchar(255) UNIQUE,");
		tableSqlBuilder.append("`username` varchar(255)");

		for (StorageType storageType : storageTypes) {
			tableSqlBuilder.append(",").append("`").append(storageType.getKey()).append("` ")
					.append(storageType.getQuery());
		}

		tableSqlBuilder.append(");");

		String tableSql = tableSqlBuilder.toString();

//		Bukkit.getLogger().info("Game Storage Debug " + tableName + " debug:");
//		Bukkit.getLogger().info(tableSql);

		try (Connection con = getConnection(); Statement statement = con.createStatement();) {
			statement.execute(tableSql);
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}

		this.refreshGameStorageColumns(gameStorage);

		if(gameStorage.getTableName().equals("chess") && !ConfigUtil.DB_TRANSFERRED.toBoolean()) {
			this.transferChessBoardsData();
		}
	}

	private void refreshGameStorageColumns(GameStorage gameStorage) {
		StorageType[] storageTypes = gameStorage.getGameStores();
		String tableName = gameStorage.getTableName();

		if(!tableExists(tableName)) return;

		// Add in newly added columns
		for(StorageType storageType : storageTypes) {
			if(!columnExists(tableName, storageType.getKey())) {
				addStorageType(tableName, storageType);
			}
		}

		// Remove old columns
		ArrayList<String> colNames = getColumns(tableName);
		for(String col : colNames) {
			if(!gameStorage.hasStorageType(col)) {
				removeStorageType(tableName, col);
			}
		}
	}

	private boolean tableExists(String table) {
		try(Connection con = getConnection(); ) {
			DatabaseMetaData md = con.getMetaData();
			try (ResultSet resultSet = md.getTables(null, null, table, null);) {
				return resultSet.next();
			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
			return false;
		}
	}

	private boolean columnExists(String table, String column) {
		try(Connection con = getConnection(); ) {
			DatabaseMetaData md = con.getMetaData();
			try (ResultSet resultSet = md.getColumns(null, null, table, column);) {
				return resultSet.next();
			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
			return false;
		}
	}

	private ArrayList<String> getColumns(String tableName) {
		String query = "SELECT * FROM " + tableName;

		try(Connection con = getConnection(); Statement statement = con.createStatement();) {
			try (ResultSet resultSet = statement.executeQuery(query);) {
				int colCount = resultSet.getMetaData().getColumnCount();
				ArrayList<String> colNames = new ArrayList<>();

				// Start at 4 so id, uuid, and username are not removed
				for(int i = 4; i <= colCount; i++) {
					colNames.add(resultSet.getMetaData().getColumnName(i));
				}

				return colNames;
			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
			return new ArrayList<>();
		}
	}

	private void addStorageType(String tableName, StorageType storageType) {
		String alterSql = "ALTER TABLE " + tableName + " ADD COLUMN " + storageType.getKey() + " " + storageType.getQuery();

		executorService.submit(() -> {
			try (Connection con = getConnection(); Statement statement = con.createStatement();) {
				statement.execute(alterSql);
			} catch (SQLException throwables) {
				throwables.printStackTrace();
			}
		});
	}

	private void removeStorageType(String tableName, String columnName) {
		String alterSql = "ALTER TABLE " + tableName + " DROP COLUMN " + columnName;

		executorService.submit(() -> {
			try (Connection con = getConnection(); Statement statement = con.createStatement();) {
				statement.execute(alterSql);
			} catch (SQLException throwables) {
				throwables.printStackTrace();
			}
		});
	}

	private void initialize() {
		if (ds == null) {
			String host = ConfigUtil.DB_HOST.toRawString();
			String port = ConfigUtil.DB_PORT.toRawString();
			String database = ConfigUtil.DB_NAME.toRawString();
			String username = ConfigUtil.DB_USERNAME.toRawString();
			String password = ConfigUtil.DB_PASS.toRawString();

			String connectionString = "jdbc:mysql://" + host + ":" + port + "/";

			createDatabaseIfNotExists(connectionString, username, password, database);

			config.setJdbcUrl(connectionString + database);
			config.setUsername(username);
			config.setPassword(password);
			config.addDataSourceProperty("cachePrepStmts", "true");
			config.addDataSourceProperty("prepStmtCacheSize", "250");
			config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
			config.addDataSourceProperty("useSSL", "false");

			ds = new HikariDataSource(config);

			// TODO: Check if connection was valid
		}
	}

	private void createDatabaseIfNotExists(String connString, String username, String password, String database) {
		try {
			HikariConfig checkConfig = new HikariConfig();
			checkConfig.setJdbcUrl(connString);
			checkConfig.setUsername(username);
			checkConfig.setPassword(password);
			checkConfig.addDataSourceProperty("useSSL", "false");

			HikariDataSource checkDs = new HikariDataSource(checkConfig);

			try (Connection con = checkDs.getConnection(); Statement createSql = con.createStatement();) {
				createSql.execute("CREATE DATABASE IF NOT EXISTS " + database);
			}

			checkDs.close();
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
	}

	public void updateColumn(Player player, String tableName, StorageType storageType, Object updated,
			boolean replace) {
		executorService.submit(() -> {
			try {
				String playerName = player.getName();
				String playerUUID = player.getUniqueId().toString();
				String columnName = storageType.getKey();

				String updateSql = "INSERT INTO `" + tableName + "` (uuid,username," + columnName
						+ ") VALUES (?,?,?) ON DUPLICATE KEY UPDATE username = ?,";

				updateSql += (replace) ? columnName + " = ?;"
						: columnName + " = " + columnName + " + ?;";

				try (Connection con = getConnection();
						PreparedStatement updateQuery = con.prepareStatement(updateSql)) {
					updateQuery.setString(1, playerUUID);
					updateQuery.setString(2, playerName);
					updateQuery.setObject(3, updated, storageType.getDataType());
					updateQuery.setString(4, playerName);
					updateQuery.setObject(5, updated, storageType.getDataType());

					updateQuery.execute();
				}
			} catch (SQLException throwables) {
				throwables.printStackTrace();
			}
		});
	}

	public LinkedHashMap<StorageType, Object> fetchPlayerStats(OfflinePlayer player, GameStorage storage,
			boolean format) {
		CompletableFuture<LinkedHashMap<StorageType, Object>> future = new CompletableFuture<>();

		String tableName = storage.getTableName();
		String playerUUID = player.getUniqueId().toString();

		String sql = "SELECT * FROM `" + tableName + "` WHERE uuid=?";

		executorService.submit(() -> {
			try {
				try (Connection con = getConnection(); PreparedStatement updateQuery = con.prepareStatement(sql)) {
					updateQuery.setString(1, playerUUID);

					try (ResultSet resultSet = updateQuery.executeQuery()) {
						if (resultSet.next()) {
							LinkedHashMap<StorageType, Object> playerStats;
							if (format)
								playerStats = this.getStringStatsFromResult(storage, resultSet);
							else
								playerStats = this.getStatsFromResult(storage, resultSet);
							future.complete(playerStats);
							return;
						}
					}
				}

				future.complete(null);
			} catch (SQLException throwables) {
				throwables.printStackTrace();
				future.complete(null);
			}
		});

		return future.join();
	}

	public LinkedHashMap<OfflinePlayer, LinkedHashMap<StorageType, Object>> fetchTopPlayers(GameStorage gameStorage,
			StorageType orderBy, int page) {
		CompletableFuture<LinkedHashMap<OfflinePlayer, LinkedHashMap<StorageType, Object>>> future = new CompletableFuture<>();
		LinkedHashMap<OfflinePlayer, LinkedHashMap<StorageType, Object>> topPlayers = new LinkedHashMap<>();

		String tableName = gameStorage.getTableName();
		String isDesc = orderBy.isOrderByDescending() ? "DESC" : "ASC";
		String sql = "SELECT * FROM `" + tableName + "` ORDER BY `" + orderBy.getKey() + "` " + isDesc + " LIMIT " + (page * 10)
				+ ", 10";

		executorService.submit(() -> {
			try {
				try (Connection con = getConnection();
						PreparedStatement fetchQuery = con.prepareStatement(sql);
						ResultSet resultSet = fetchQuery.executeQuery()) {
					LinkedHashMap<OfflinePlayer, LinkedHashMap<StorageType, Object>> bottomPlayers = new LinkedHashMap<>();
					while (resultSet.next()) {
						String playerUUID = resultSet.getString("uuid");
						if (playerUUID == null)
							continue;

						OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID));
						if (!player.hasPlayedBefore())
							continue;

						LinkedHashMap<StorageType, Object> playerStats = this.getStringStatsFromResult(gameStorage,
								resultSet);

						if(!orderBy.isOrderByDescending()) {
							if((playerStats.get(orderBy) + "").equals("0:0")) {
								bottomPlayers.put(player, playerStats);
								continue;
							}
						}

						topPlayers.put(player, playerStats);
					}

					for(OfflinePlayer bottomPlayer : bottomPlayers.keySet()) {
						topPlayers.put(bottomPlayer, bottomPlayers.get(bottomPlayer));
					}

					future.complete(topPlayers);
				}
			} catch (SQLException throwables) {
				throwables.printStackTrace();
				future.complete(null);
			}
		});

		return future.join();
	}

	public int getGamePlayerTotal(GameStorage gameStorage) {
		CompletableFuture<Integer> future = new CompletableFuture<>();

		String sqlString = "SELECT * FROM " + gameStorage.getTableName();

		executorService.submit(() -> {
			try {
				int num = 0;
				try (Connection con = getConnection();
					 PreparedStatement sql = con.prepareStatement(sqlString);
					 ResultSet playerData = sql.executeQuery();) {
					while (playerData.next()) {
						num++;
					}
				}

				future.complete(num);
			} catch (SQLException throwables) {
				throwables.printStackTrace();
				future.complete(0);
			}
		});

		return future.join();
	}

	private LinkedHashMap<StorageType, Object> getStatsFromResult(GameStorage storage, ResultSet resultSet)
			throws SQLException {
		LinkedHashMap<StorageType, Object> playerStats = new LinkedHashMap<>();

		for (StorageType storageType : storage.getGameStores()) {
			if (!storage.canExecute(storageType))
				continue;

			Object result = resultSet.getObject(storageType.getKey());

			if (result != null) {
				playerStats.put(storageType, result);
			} else {
				playerStats.put(storageType, 0);
			}
		}

		return playerStats;
	}

	private LinkedHashMap<StorageType, Object> getStringStatsFromResult(GameStorage storage, ResultSet resultSet)
			throws SQLException {
		LinkedHashMap<StorageType, Object> playerStats = new LinkedHashMap<>();

		for (StorageType storageType : storage.getGameStores()) {
			if (!storage.canExecute(storageType))
				continue;

			Object result = resultSet.getObject(storageType.getKey());

			if (result != null) {
				if (storageType == StorageType.BEST_TIME) {
					result = (int) ((double) result / 60) + ":" + (int) ((double) result % 60);
				}
				playerStats.put(storageType, result.toString());
			} else {
				playerStats.put(storageType, 0);
			}
		}

		return playerStats;
	}

	private boolean hasGameStore(GameStorage gameStorage) {
		for (GameStorage storage : this.gameStores) {
			if (storage.getTableName().equals(gameStorage.getTableName()))
				return true;
		}

		return false;
	}

	public Connection getConnection() throws SQLException {
		return ds.getConnection();
	}

	public void closeConnection() {
		if (ds != null)
			ds.close();
	}

	// ChessBoards Only
	private ArrayList<OldChessPlayer> getOldChessPlayers() {
		CompletableFuture<ArrayList<OldChessPlayer>> future = new CompletableFuture<>();
		ArrayList<OldChessPlayer> players = new ArrayList<>();

		executorService.submit(() -> {
			try {
				try (Connection con = getConnection();
					 PreparedStatement sql = con.prepareStatement("SELECT * FROM chess_players;");
					 ResultSet playerData = sql.executeQuery();) {

					while (playerData.next()) {
						OldChessPlayer newPlayer = new OldChessPlayer(playerData);
						players.add(newPlayer);
					}
				}

				future.complete(players);
			} catch (SQLException throwables) {
				throwables.printStackTrace();
				future.complete(null);
			}
		});

		return future.join();
	}

	private void insertOldChessPlayer(OldChessPlayer player) {
		String createPlayerSql = "INSERT INTO `chess` (uuid, wins, losses, ties, rating, rating_deviation, rating_volatility) VALUES "
				+ "(?,?,?,?,?,?,?);";

		executorService.submit(() -> {
			try {
				try (Connection con = getConnection();
					 PreparedStatement sql = con.prepareStatement(createPlayerSql);) {

					sql.setString(1, player.getUuid());
					sql.setInt(2, player.getWins());
					sql.setInt(3, player.getLosses());
					sql.setInt(4, player.getTies());
					sql.setDouble(5, player.getRating());
					sql.setDouble(6, player.getRatingDeviation());
					sql.setDouble(7, player.getVolatility());

					sql.execute();
				}
			} catch (SQLException throwables) {
				throwables.printStackTrace();
			}
		});
	}

	private void transferChessBoardsData() {
		if(!tableExists("chess_players")) return;
		if(!tableExists("chess")) return;

		ArrayList<OldChessPlayer> oldChessPlayers = getOldChessPlayers();
		if(oldChessPlayers == null) return;

		for(OldChessPlayer oldChessPlayer : oldChessPlayers) {
			insertOldChessPlayer(oldChessPlayer);
		}

		ConfigUtil.DB_TRANSFERRED.setValue("true");
	}
}
