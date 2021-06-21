package water.of.cup.boardgames.game.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.config.ConfigUtil;
import water.of.cup.boardgames.game.Game;

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
        if(!hasGameStore(gameStorage)) {
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

        for(StorageType storageType : storageTypes) {
            tableSqlBuilder.append(",")
                    .append("`")
                    .append(storageType.getKey())
                    .append("` ")
                    .append(storageType.getQuery());
        }

        tableSqlBuilder.append(");");

        String tableSql = tableSqlBuilder.toString();

        Bukkit.getLogger().info("Game Storage Debug " + tableName + " debug:");
        Bukkit.getLogger().info(tableSql);

        try (Connection con = getConnection();
             Statement statement = con.createStatement();) {
            statement.execute(tableSql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void initialize() {
        if(ds == null) {
            String host = ConfigUtil.DB_HOST.toRawString();
            String port = ConfigUtil.DB_PORT.toRawString();
            String database = ConfigUtil.DB_NAME.toRawString();
            String username = ConfigUtil.DB_USERNAME.toRawString();
            String password = ConfigUtil.DB_PASS.toRawString();

            String connectionString = "jdbc:mysql://"
                    + host + ":" + port + "/";

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

            try(Connection con = checkDs.getConnection();
                Statement createSql = con.createStatement();
            ) {
                createSql.execute("CREATE DATABASE IF NOT EXISTS " + database);
            }

            checkDs.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void updateColumn(Player player, String tableName, StorageType storageType, Object updated, boolean replace) {
        executorService.submit(() -> {
            try {
                String playerName = player.getName();
                String playerUUID = player.getUniqueId().toString();
                String columnName = storageType.getKey();

                String updateSql = "INSERT INTO `" +
                        tableName +
                        "` (uuid,username," +
                        columnName +
                        ") VALUES (?,?,?) ON DUPLICATE KEY ";

                updateSql += (replace) ? "SET " + columnName + " = ?;"
                        : "UPDATE " + columnName + " = " + columnName + " + ?;";

                try (Connection con = getConnection();
                     PreparedStatement updateQuery = con.prepareStatement(updateSql)) {
                    updateQuery.setString(1, playerUUID);
                    updateQuery.setString(2, playerName);
                    updateQuery.setObject(3, updated, storageType.getDataType());
                    updateQuery.setObject(4, updated, storageType.getDataType());

                    updateQuery.execute();
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    public LinkedHashMap<StorageType, Object> fetchPlayerStats(OfflinePlayer player, GameStorage storage) {
        CompletableFuture<LinkedHashMap<StorageType, Object>> future = new CompletableFuture<>();

        String tableName = storage.getTableName();
        String playerUUID = player.getUniqueId().toString();

        String sql = "SELECT * FROM `" + tableName + "` WHERE uuid=?";

        executorService.submit(() -> {
            try {
                try (Connection con = getConnection();
                     PreparedStatement updateQuery = con.prepareStatement(sql)) {
                    updateQuery.setString(1, playerUUID);

                    try(ResultSet resultSet = updateQuery.executeQuery()) {
                        if(resultSet.next()) {
                            LinkedHashMap<StorageType, Object> playerStats = this.getStatsFromResult(storage, resultSet);

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

    public LinkedHashMap<OfflinePlayer, LinkedHashMap<StorageType, Object>> fetchTopPlayers(GameStorage gameStorage, StorageType orderBy, int page) {
        CompletableFuture<LinkedHashMap<OfflinePlayer, LinkedHashMap<StorageType, Object>>> future = new CompletableFuture<>();
        LinkedHashMap<OfflinePlayer, LinkedHashMap<StorageType, Object>> topPlayers = new LinkedHashMap<>();

        String tableName = gameStorage.getTableName();
        String sql = "SELECT * FROM `" + tableName + "` ORDER BY `" + orderBy.getKey() + "` DESC LIMIT " + (page * 10) + ", 10";

        executorService.submit(() -> {
            try {
                try (Connection con = getConnection();
                     PreparedStatement fetchQuery = con.prepareStatement(sql);
                     ResultSet resultSet = fetchQuery.executeQuery()) {
                        while (resultSet.next()) {
                            String playerUUID = resultSet.getString("uuid");
                            if(playerUUID == null) continue;

                            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID));
                            if(!player.hasPlayedBefore()) continue;

                            LinkedHashMap<StorageType, Object> playerStats = this.getStatsFromResult(gameStorage, resultSet);

                            topPlayers.put(player, playerStats);
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

    private LinkedHashMap<StorageType, Object> getStatsFromResult(GameStorage storage, ResultSet resultSet) throws SQLException {
        LinkedHashMap<StorageType, Object> playerStats = new LinkedHashMap<>();

        for(StorageType storageType : storage.getGameStores()) {
            if(!storage.canExecute(storageType)) continue;

            Object result = resultSet.getObject(storageType.getKey());

            if(result != null)
                playerStats.put(storageType, result);
        }

        return playerStats;
    }

    private boolean hasGameStore(GameStorage gameStorage) {
        for(GameStorage storage : this.gameStores) {
            if(storage.getTableName().equals(gameStorage.getTableName()))
                return true;
        }

        return false;
    }

    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public void closeConnection() {
        if(ds != null)
            ds.close();
    }
}