package cc.jkob.bedwars.util;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Location;

import java.lang.reflect.Type;
import java.util.Map;

public class LocationAdapter implements JsonSerializer<Location>, JsonDeserializer<Location> {
    @Override
    public JsonElement serialize(Location location, Type type, JsonSerializationContext context) {
        JsonObject o = new JsonObject();

        Map<String, Object> loc = location.serialize();
        for (String k : loc.keySet())
            o.add(k, context.serialize(loc.get(k)));

        return o;
    }

    @Override
    public Location deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        return Location.deserialize(context.deserialize(jsonElement, mapType));
    }
}
