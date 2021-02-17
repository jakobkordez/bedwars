package cc.jkob.bedwars.listener;

import java.util.List;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;

import cc.jkob.bedwars.game.GameManager;
import cc.jkob.bedwars.game.PlayerData;

public class InvisibilityListener extends PacketAdapter {
    public InvisibilityListener(Plugin plugin) {
        super(plugin, PacketType.Play.Server.ENTITY_EQUIPMENT);
    }
    
    @Override
    public void onPacketSending(PacketEvent event) {
        int slot = event.getPacket().getIntegers().read(1);
        if (slot == 0) return;

        Player target = event.getPlayer();
        PlayerData targetD = GameManager.instance.getPlayer(target);
        if (!targetD.isInGame()) return;

        int eId = event.getPacket().getIntegers().read(0);
        PlayerData sourceD = getPlayerById(target, eId);
        if (!sourceD.getPlayer().hasPotionEffect(PotionEffectType.INVISIBILITY)) return;

        if (sourceD.getGamePlayer().getTeam() == targetD.getGamePlayer().getTeam()) return;

        event.setPacket(prepare(event.getPacket()));
    }

    private PlayerData getPlayerById(Player player, int id) {
        Entity entity = player.getWorld().getEntitiesByClass(Player.class).parallelStream()
            .filter(e -> e.getEntityId() == id)
            .findAny().orElse(null);
        return GameManager.instance.getPlayer(entity.getUniqueId());
    }

    private PacketContainer prepare(PacketContainer packet) {
        PacketContainer newP = new PacketContainer(packet.getType());
        List<Integer> ints = packet.getIntegers().getValues();
        newP.getIntegers()
            .write(0, ints.get(0))
            .write(1, ints.get(1));
        newP.getItemModifier()
            .write(0, null);
        return newP;
    }
}
