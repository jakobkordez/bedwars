package cc.jkob.bedwars.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import cc.jkob.bedwars.BedWarsPlugin;
import cc.jkob.bedwars.event.PlayerUseEntityEvent;

public class InteractPacketListener extends PacketAdapter {
    public InteractPacketListener(BedWarsPlugin plugin) {
        super(plugin, ListenerPriority.MONITOR, PacketType.Play.Client.USE_ENTITY);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        int eId = event.getPacket().getIntegers().read(0);
        plugin.getServer().getPluginManager().callEvent(new PlayerUseEntityEvent(event.getPlayer(), eId));
    }
}
