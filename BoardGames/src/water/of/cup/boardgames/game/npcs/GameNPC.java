package water.of.cup.boardgames.game.npcs;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.game.MathUtils;

public abstract class GameNPC {

    private NPC gameNPC;
    private Location npcLocation;
    private double[] loc;
    protected abstract String getName();
    protected abstract NPCSkin getSkin();

    public static final NPCRegistry REGISTRY = CitizensAPI.createAnonymousNPCRegistry(new GameNPCRegistry());

    public GameNPC(double[] loc) {
       this.loc = loc;
    }

    public void spawnNPC() {
        if(!BoardGames.hasCitizens() || npcLocation == null) return;

        if(gameNPC != null)
            return;

        gameNPC = REGISTRY.createNPC(EntityType.PLAYER, getName());

        if(getSkin() != null)
            gameNPC.getOrAddTrait(SkinTrait.class).setSkinPersistent(getName(), getSkin().getSkinSig(), getSkin().getSkinData());

        if(!gameNPC.isSpawned())
            gameNPC.spawn(npcLocation);
    }

    public void setMapValLoc(Location mapValLoc, int rot) {
        double[] xz = {loc[0], loc[2]};
        for (int i = 0; i < rot; i++)
            xz =  MathUtils.rotatePointAroundPoint90Degrees(new double[] {0 , 0} , xz);

        npcLocation = mapValLoc.clone().add(xz[0], loc[1], xz[1]);
    }

    public void lookAt(Player player) {
        if(hasSpawned())
            gameNPC.faceLocation(player.getLocation());
    }

    public void removeNPC() {
        if(gameNPC == null)
            return;

        if(gameNPC.isSpawned())
            gameNPC.despawn();

        gameNPC.destroy();
        gameNPC = null;
    }

    private boolean hasSpawned() {
        return gameNPC != null && gameNPC.isSpawned();
    }
}
