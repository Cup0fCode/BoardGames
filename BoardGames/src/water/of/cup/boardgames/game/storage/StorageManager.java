package water.of.cup.boardgames.game.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.game.Game;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
        this.gameStores.add(gameStorage);

        // init table
        initializeGameStorage(gameStorage);
    }

    private void initializeGameStorage(GameStorage gameStorage) {
        String tableName = gameStorage.getTableName();
        StorageType[] storageTypes = gameStorage.getGameStores();

        StringBuilder tableSqlBuilder = new StringBuilder();

        tableSqlBuilder.append("CREATE TABLE IF NOT EXISTS `").append(tableName).append("` (");
        tableSqlBuilder.append("`id` int PRIMARY KEY AUTO_INCREMENT,");
        tableSqlBuilder.append("`uuid` varchar(255) UNIQUE");

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
            String host = instance.getConfig().getString("settings.database.host");
            String port = instance.getConfig().getString("settings.database.port");
            String database = instance.getConfig().getString("settings.database.database");
            String username = instance.getConfig().getString("settings.database.username");
            String password = instance.getConfig().getString("settings.database.password");

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
                String playerUUID = player.getUniqueId().toString();
                String columnName = storageType.getKey();

                String updateSql = "INSERT INTO `" +
                        tableName +
                        "` (uuid," +
                        columnName +
                        ") VALUES (?,?) ON DUPLICATE KEY ";

                updateSql += (replace) ? "SET " + columnName + " = ?;"
                        : "UPDATE " + columnName + " = " + columnName + " + ?;";

                try (Connection con = getConnection();
                     PreparedStatement updateQuery = con.prepareStatement(updateSql)) {
                    updateQuery.setString(1, playerUUID);
                    updateQuery.setObject(2, updated, storageType.getDataType());
                    updateQuery.setObject(3, updated, storageType.getDataType());

                    updateQuery.execute();
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public void closeConnection() {
        if(ds != null)
            ds.close();
    }
}
