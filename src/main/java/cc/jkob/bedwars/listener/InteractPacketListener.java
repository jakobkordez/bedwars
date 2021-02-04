package cc.jkob.bedwars.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;

import cc.jkob.bedwars.BedWarsPlugin;
import cc.jkob.bedwars.event.PlayerUseEntityEvent;

public class InteractPacketListener extends PacketAdapter {
    public InteractPacketListener(BedWarsPlugin plugin) {
        super(plugin, ListenerPriority.MONITOR, PacketType.Play.Client.USE_ENTITY);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        if (event.getPacket().getEntityUseActions().read(0) != EntityUseAction.INTERACT) return;
        
        int eId = event.getPacket().getIntegers().read(0);
        plugin.getServer().getPluginManager().callEvent(new PlayerUseEntityEvent(event.getPlayer(), eId));
    }
}
