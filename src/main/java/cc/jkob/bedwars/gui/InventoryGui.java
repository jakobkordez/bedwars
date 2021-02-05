package cc.jkob.bedwars.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;

public interface InventoryGui {
    public void open(Player player);
    public void click(Player player, String id, InventoryAction action);
}
