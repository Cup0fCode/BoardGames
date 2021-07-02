package water.of.cup.boardgames.game.games.chess;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.persistence.PersistentDataType;
import water.of.cup.boardgames.BoardGames;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class ChessBoardsUtil {

    public static final ArrayList<Integer> OLD_MAP_IDS = new ArrayList<>();

    public static void loadGames() {
        File folder = new File(BoardGames.getInstance().getDataFolder() + "/saved_games");
        File[] listOfFiles = folder.listFiles();

        if(listOfFiles == null) return;

        for (File file : listOfFiles) {
            if (file.isFile()) {
                int gameId = Integer.parseInt(file.getName().split("_")[1].split(Pattern.quote("."))[0]);
                OLD_MAP_IDS.add(gameId);
            }
        }
    }

    public static boolean isChessBoardsMap(ItemStack itemStack) {
        if(itemStack.getItemMeta() == null || !(itemStack.getItemMeta() instanceof MapMeta)) return false;
        MapMeta mapMeta = (MapMeta) itemStack.getItemMeta();

        if(mapMeta.getMapView() == null) return false;

        int gameId = mapMeta.getMapView().getId();
        return OLD_MAP_IDS.contains(gameId);
    }

    public static boolean isChessBoardsItem(ItemStack itemStack) {
        if(itemStack.getItemMeta() == null) return false;
        NamespacedKey key = new NamespacedKey(BoardGames.getInstance(), "chess_board");
        return itemStack.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.DOUBLE);
    }

    public static void removeChessBoard(ItemFrame itemFrame) {
        Location location = itemFrame.getLocation();
        itemFrame.remove();
        location.getBlock().setType(Material.AIR);

        if(!isChessBoardsMap(itemFrame.getItem())) return;

        ItemStack item = itemFrame.getItem();
        MapMeta mapMeta = (MapMeta) item.getItemMeta();

        if(mapMeta == null || mapMeta.getMapView() == null) return;

        int mapId = mapMeta.getMapView().getId();

        OLD_MAP_IDS.remove(Integer.valueOf(mapId));

        deleteChessBoardsGame(mapId);
    }

    private static void deleteChessBoardsGame(int mapId) {
        File file = new File(BoardGames.getInstance().getDataFolder(), "saved_games/game_" + mapId + ".txt");
        if (!file.exists())
            return;

        file.delete();
    }
}
