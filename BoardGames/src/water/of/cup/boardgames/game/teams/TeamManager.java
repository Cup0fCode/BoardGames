package water.of.cup.boardgames.game.teams;

import org.bukkit.entity.Player;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.GamePlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class TeamManager {

    private final Game game;
    private final LinkedHashMap<String, GamePlayer> teams;
    private final ArrayList<String> teamNames;
    private String currentTurn;

    public TeamManager(Game game) {
        this.game = game;
        this.teamNames = game.getTeamNames();
        this.teams = new LinkedHashMap<>();
        this.currentTurn = null;
    }

    public void addTeam(GamePlayer gamePlayer, String team) {
        if(team != null) {
            teams.put(team, gamePlayer);
        } else if (teamNames == null) {
            // If the game does not have "teams", set their team to their name
            String teamName = gamePlayer.getPlayer().getName();
            teams.put(teamName, gamePlayer);
        } else {
            // Find available team
            String availableTeam = teamNames.get(0);
            if(teams.containsKey(availableTeam)) {
                for (String teamName : teamNames) {
                    if (!teams.containsKey(teamName)) {
                        availableTeam = teamName;
                        break;
                    }
                }
            }

            teams.put(availableTeam, gamePlayer);
        }

        if(currentTurn == null) {
            currentTurn = (String) teams.keySet().toArray()[0];
        }
    }

    public void addTeam(GamePlayer gamePlayer) {
        addTeam(gamePlayer, null);
    }

    public void removeTeamByPlayer(Player player) {
        for(String team : teams.keySet()) {
            if(teams.get(team).getPlayer().equals(player)) {
                teams.remove(team);
                return;
            }
        }
    }

    public void resetTeams() {
        teams.clear();
        currentTurn = null;
    }

    public GamePlayer getTurnPlayer() {
        return teams.get(currentTurn);
    }

    public String getTurnTeam() {
        return currentTurn;
    }

    public void setTurn(String team) {
        this.currentTurn = team;
    }

    public void setTurn(GamePlayer gamePlayer) {
        for(String team : teams.keySet()) {
            if(teams.get(team).equals(gamePlayer)) {
                currentTurn = team;
                return;
            }
        }
    }

    public void nextTurn() {
        ArrayList<String> teamList = new ArrayList<>(teams.keySet());
        int nextTurnIndex = teamList.indexOf(currentTurn) + 1;
        if(nextTurnIndex >= teamList.size()) {
            currentTurn = teamList.get(0);
        } else {
            currentTurn = teamList.get(nextTurnIndex);
        }
    }

    public GamePlayer getGamePlayer(Player player) {
        for(String team : teams.keySet()) {
            if(teams.get(team).getPlayer().equals(player)) {
                return teams.get(team);
            }
        }

        return null;
    }

    public GamePlayer getGamePlayerByTeam(String teamName) {
        for(String team : teams.keySet()) {
            if(team.equals(teamName)) {
                return teams.get(team);
            }
        }

        return null;
    }

    public ArrayList<GamePlayer> getGamePlayers() {
        return new ArrayList<>(teams.values());
    }
}
