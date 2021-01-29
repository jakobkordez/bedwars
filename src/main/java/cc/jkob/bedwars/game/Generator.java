package cc.jkob.bedwars.game;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

public class Generator implements ConfigurationSerializable {
    private Location pos;
    private GeneratorType type;
    private int interval;

    public Generator(Location pos, GeneratorType type) {
        this.pos = pos.getBlock().getLocation();
        this.type = type;
        this.interval = type.getInterval();
    }

    public Location getPos() {
        return pos;
    }

    public GeneratorType getType() {
        return type;
    }

    public int getInterval() {
        return interval;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("pos", pos);
        data.put("type", type);
        data.put("interval", interval);
        return data;
    }
}
