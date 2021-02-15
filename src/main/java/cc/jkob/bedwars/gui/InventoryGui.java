package cc.jkob.bedwars.gui;

import org.bukkit.event.inventory.InventoryAction;

import cc.jkob.bedwars.game.PlayerData;

public interface InventoryGui {
    public void open(PlayerData player);
    public void click(PlayerData player, String id, InventoryAction action);
}
