package cc.jkob.bedwars.game;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game implements ConfigurationSerializable {
    private String name, world;
    private HashMap<String, Team> teams = new HashMap<String, Team>();
    private List<Generator> generators = new ArrayList<Generator>();
    private Location lobby;

    public Game(String name, String world) {
        this.name = name;
        this.world = world;
    }

    public void setLobby(Location lobby) {
        this.lobby = lobby;
    }

    public String getName() {
        return name;
    }

    public String getWorld() {
        return world;
    }

    public HashMap<String, Team> getTeams() {
        return teams;
    }

    public List<Generator> getGenerators() {
        return generators;
    }

    public Location getLobby() {
        return lobby;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("world", world);
        data.put("teams", teams.values().toArray());
        data.put("generators", generators);
        data.put("lobby", lobby);
        return data;
    }


}
