package water.of.cup.boardgames.game.storage;

import java.sql.JDBCType;

public interface StorageType {

    String getKey();

    JDBCType getDataType();

    boolean isOrderByDescending();

    String getQuery();

}