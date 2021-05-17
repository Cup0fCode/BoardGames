package water.of.cup.boardgames.game.storage;

import java.sql.JDBCType;

public enum StorageType {

    // General types
    WINS   ("wins", JDBCType.INTEGER),
    LOSSES ("losses", JDBCType.INTEGER),
    TIES   ("ties", JDBCType.INTEGER),

    // Specific type example
    CROSS_WINS("cross_wins", JDBCType.INTEGER);

    private final String key;
    private final JDBCType dataType;

    StorageType(String key, JDBCType dataType) {
        this.key = key;
        this.dataType = dataType;
    }

    public String getKey() {
        return key;
    }

    public JDBCType getDataType() {
        return dataType;
    }
}

