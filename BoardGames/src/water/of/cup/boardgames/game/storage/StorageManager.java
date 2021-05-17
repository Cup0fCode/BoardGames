package water.of.cup.boardgames.game.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.game.Game;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class StorageManager {

    private final BoardGames instance = BoardGames.getInstance();
    private static final HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;
    private final ArrayList<GameStorage> gameStores;

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
        // TODO: Find out if this works; Might switch over to builder
        String tableName = gameStorage.getTableName();
        StorageType[] storageTypes = gameStorage.getGameStores();

        StringBuilder tableSql = new StringBuilder();

        tableSql.append("CREATE TABLE IF NOT EXISTS `").append(tableName).append("` (");
        tableSql.append("`id` int PRIMARY KEY AUTO_INCREMENT,");
        tableSql.append("`uuid` varchar(255) UNIQUE,");

        for(StorageType storageType : storageTypes) {
            tableSql.append("`")
                    .append(storageType.getKey())
                    .append("` ")
                    .append(storageType.getDataType().getName())
                    .append(",");
        }

        tableSql.append(");");

        Bukkit.getLogger().info("Game Storage Debug " + tableName + " debug:");
        Bukkit.getLogger().info(tableSql.toString());
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

    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public void closeConnection() {
        if(ds != null)
            ds.close();
    }
}
