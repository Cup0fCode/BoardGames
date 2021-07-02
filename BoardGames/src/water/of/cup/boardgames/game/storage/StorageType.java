package water.of.cup.boardgames.game.storage;

import java.sql.JDBCType;

public enum StorageType {

    // General types
    WINS   ("wins", "int default 0", JDBCType.INTEGER, true),
    LOSSES ("losses", "int default 0", JDBCType.INTEGER, true),
    TIES   ("ties", "int default 0", JDBCType.INTEGER, true),

    // Specific type example
    CROSS_WINS("cross_wins", "int default 0", JDBCType.INTEGER, true),
    BEST_TIME("best_time", "double default 0", JDBCType.DOUBLE, false),
    
    // Glicko2 Ratings
    Rating("rating", "double default 0", JDBCType.DOUBLE, true),
    RatingDeviation("rating_deviation", "double default 0", JDBCType.DOUBLE, true),
    RatingVolatility("rating_volatility", "double default 0", JDBCType.DOUBLE, true);

    private final String key;
    private final JDBCType dataType;
    private final String query;
    private final boolean orderByDescending;

    StorageType(String key, String query, JDBCType dataType, boolean orderByDescending) {
        this.key = key;
        this.dataType = dataType;
        this.query = query;
        this.orderByDescending = orderByDescending;
    }

    public String getKey() {
        return key;
    }

    public JDBCType getDataType() {
        return dataType;
    }

    public boolean isOrderByDescending() {
        return orderByDescending;
    }

    public String getQuery() {
        return this.query;
    }

    public static boolean isValidColumn(String columnName) {
        for(StorageType storageType : StorageType.values()) {
            if(storageType.getKey().equals(columnName)) return true;
        }

        return false;
    }
}

