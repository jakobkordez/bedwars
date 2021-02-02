package cc.jkob.bedwars.shop;

import com.comphenix.protocol.events.PacketContainer;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import cc.jkob.bedwars.BedWarsPlugin;
import cc.jkob.bedwars.shop.Shop.ShopType;
import cc.jkob.bedwars.util.PacketUtil;

public class Shopkeeper {
    private static int entityId = 200;

    private Location loc;
    private ShopType type;

    public Shopkeeper(Location loc, ShopType type) {
        this.loc = loc;
        this.type = type;
    }

    // transient
    private transient int eId;
    private transient Hologram hologram;
    private transient ArmorStand armorStand;

    public void spwan() {
        hologram = HologramsAPI.createHologram(BedWarsPlugin.getInstance(), loc.clone().add(0, 2.5, 0));
        hologram.appendTextLine(type.getName());

        armorStand = loc.getWorld().spawn(loc, ArmorStand.class);
        armorStand.setVisible(false);

        eId = entityId++;
        loc.getWorld().getPlayers().forEach(p -> sendSpawnPacket(p));
    }

    public void remove() {
        hologram.delete();
        armorStand.remove();

        loc.getWorld().getPlayers().forEach(p -> sendDestroyPacket(p));
    }

    // Packets //
    private transient PacketContainer spawnPacket, destroyPacket;

    public void sendSpawnPacket(Player player) {
        if (spawnPacket == null) spawnPacket = PacketUtil.createSpawnPacket(eId, loc, EntityType.VILLAGER);

        PacketUtil.sendPacket(player, spawnPacket);
    }

    public void sendDestroyPacket(Player player) {
        if (destroyPacket == null) destroyPacket = PacketUtil.createDestroyPacket(eId);

        PacketUtil.sendPacket(player, destroyPacket);
    }
}
