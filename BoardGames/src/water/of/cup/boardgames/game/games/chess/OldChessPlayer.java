package water.of.cup.boardgames.game.games.chess;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OldChessPlayer {

    private final int id;
    private final String uuid;
    private final int wins;
    private final int losses;
    private final int ties;
    private final double rating;
    private final double ratingDeviation;
    private final double volatility;
    private final int numberOfResults;

    public OldChessPlayer(ResultSet resultSet) throws SQLException {
        this.id = resultSet.getInt(1);
        this.uuid = resultSet.getString(2);
        this.wins = resultSet.getInt(3);
        this.losses = resultSet.getInt(4);
        this.ties = resultSet.getInt(5);
        this.rating = resultSet.getDouble(6);
        this.ratingDeviation = resultSet.getDouble(7);
        this.volatility = resultSet.getDouble(8);
        this.numberOfResults = resultSet.getInt(9);
    }

    public int getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public int getTies() {
        return ties;
    }

    public double getRating() {
        return rating;
    }

    public double getRatingDeviation() {
        return ratingDeviation;
    }

    public double getVolatility() {
        return volatility;
    }

    public int getNumberOfResults() {
        return numberOfResults;
    }
}