package water.of.cup.boardgames.game.storage;

import java.sql.JDBCType;

public enum StorageType {

    // General types
    WINS   ("wins", "int default 0", JDBCType.INTEGER),
    LOSSES ("losses", "int default 0", JDBCType.INTEGER),
    TIES   ("ties", "int default 0", JDBCType.INTEGER),

    // Specific type example
    CROSS_WINS("cross_wins", "int default 0", JDBCType.INTEGER);

    private final String key;
    private final JDBCType dataType;
    private final String query;

    StorageType(String key, String query, JDBCType dataType) {
        this.key = key;
        this.dataType = dataType;
        this.query = query;
    }

    public String getKey() {
        return key;
    }

    public JDBCType getDataType() {
        return dataType;
    }

    public String getQuery() {
        return this.query;
    }
}

