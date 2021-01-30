package cc.jkob.bedwars.game;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Team {
    private String name;
    private TeamColor color;
    private Generator ironGen, goldGen;
    private Location spawn, bedFeet, bedHead;

    public Team(String name, TeamColor color) {
        this.name = name;
        this.color = color;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }

    public void setGens(Location pos) {
        ironGen = new Generator(pos, GeneratorType.IRON);
        goldGen = new Generator(pos, GeneratorType.GOLD);
    }

    public void setBed(Location bedHead, Location bedFeet) {
        this.bedHead = bedHead.getBlock().getLocation();
        this.bedFeet = bedFeet.getBlock().getLocation();
    }

    public String getName() {
        return name;
    }

    public String getFormattedName() {
        return color.getChatColor() + name;
    }

    public TeamColor getColor() {
        return color;
    }

    public Generator getIronGen() {
        return ironGen;
    }

    public Generator getGoldGen() {
        return goldGen;
    }

    public Location getSpawn() {
        return spawn;
    }

    public boolean isTeamBed(Location p) {
        p = p.getBlock().getLocation();
        return bedFeet.equals(p) || bedHead.equals(p);
    }

    // transient    
    private transient List<Player> players;
}
