package cc.jkob.bedwars.game;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

public class Team implements ConfigurationSerializable {
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

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("color", color);
        data.put("ironGen", ironGen);
        data.put("goldGen", goldGen);
        data.put("spawn", spawn);
        data.put("bedFeet", bedFeet);
        data.put("bedHead", bedHead);
        return data;
    }
}
