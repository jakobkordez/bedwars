package cc.jkob.bedwars.game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

public class Tool implements ConfigurationSerializable {
    public final String name;
    private final List<ToolStage> stages;

    public Tool(String name, List<ToolStage> stages) {
        this.name = name;
        this.stages = stages;
    }

    public int downgrade(int old) {
        if (old >= stages.size()) return stages.size() - 1;
        if (old <= 0) return 0;
        if (!stages.get(old).downgradable) return old;
        return old - 1;
    }

    public int upgrade(int old) {
        if (old < 0) return 0;
        if (old >= stages.size() - 1) return stages.size() - 1;
        return old + 1;
    }

    public ItemStack get(int index) {
        return stages.get(index).item;
    }

    public ToolStage getStage(int index) {
        return stages.get(index);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("stages", stages);
        return data;
    }

    @SuppressWarnings("unchecked")
    public static Tool deserialize(Map<String, Object> args) {
        return new Tool(
            (String) args.get("name"),
            (List<ToolStage>) args.get("stages"));
    }

    public static class ToolStage implements ConfigurationSerializable {
        private final ItemStack item;
        private final ItemStack price;
        private final boolean downgradable;

        public ToolStage(ItemStack item, ItemStack price, boolean downgradable) {
            this.item = item;
            this.price = price;
            this.downgradable = downgradable;
        }

        public ItemStack getItem() {
            return item;
        }

        public ItemStack getPrice() {
            return price;
        }

        @Override
        public Map<String, Object> serialize() {
            Map<String, Object> data = new HashMap<>();
            data.put("item", item);
            data.put("price", price);
            data.put("downgradable", downgradable);
            return data;
        }

        public static ToolStage deserialize(Map<String, Object> args) {
            return new ToolStage(
                (ItemStack) args.get("item"),
                (ItemStack) args.get("price"),
                (boolean) args.get("downgradable"));
        }
    }
}
