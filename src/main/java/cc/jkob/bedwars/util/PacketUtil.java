package cc.jkob.bedwars.util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.stream.Collectors;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.EnumWrappers.TitleAction;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import cc.jkob.bedwars.BedWarsPlugin;

public class PacketUtil {
    // Packets //
    @SuppressWarnings("deprecation")
    public static PacketContainer createSpawnPacket(int id, Location loc, EntityType type) {
        PacketContainer packet = getManager().createPacket(PacketType.Play.Server.SPAWN_ENTITY_LIVING);

        packet.getIntegers()
            .write(0, id)
            .write(1, (int) type.getTypeId())       //Id
            .write(2, (int) (loc.getX() * 32))      //X
            .write(3, (int) (loc.getY() * 32))      //Y
            .write(4, (int) (loc.getZ() * 32));     //Z

        packet.getBytes()
            .write(0, (byte)(int)(loc.getYaw() * 256.0F / 360.0F))      //Yaw
            .write(1, (byte)(int)(loc.getPitch() * 256.0F / 360.0F))    //Pitch
            .write(2, (byte)(int)(loc.getYaw() * 256.0F / 360.0F));     //Head

        packet.getDataWatcherModifier().write(0, getDefaulWatcher(loc.getWorld(), type));

        return packet;
    }

    public static PacketContainer createDestroyPacket(int ...ids) {
        PacketContainer packet = getManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY);

        packet.getIntegerArrays().write(0, ids);

        return packet;
    }

    public static PacketContainer createTitlePacket(String title) {
        PacketContainer packet = getManager().createPacket(PacketType.Play.Server.TITLE);

        packet.getTitleActions()
            .write(0, TitleAction.TITLE);

        packet.getChatComponents()
            .write(0, WrappedChatComponent.fromJson("{\"text\": \"" + title + "\"}"));

        return packet;
    }

    public static PacketContainer createSubTitlePacket(String subTitle) {
        PacketContainer packet = getManager().createPacket(PacketType.Play.Server.TITLE);

        packet.getTitleActions().write(0, TitleAction.SUBTITLE);

        packet.getChatComponents().write(0, WrappedChatComponent.fromText(subTitle));

        return packet;
    }

    public static PacketContainer createTitleTimesPacket(int fadeIn, int stay, int fadeOut) {
        PacketContainer packet = getManager().createPacket(PacketType.Play.Server.TITLE);

        packet.getTitleActions().write(0, TitleAction.TIMES);

        packet.getIntegers()
            .write(0, fadeIn)
            .write(1, stay)
            .write(2, fadeOut);

        return packet;
    }

    public static PacketContainer createTitleResetPacket() {
        PacketContainer packet = getManager().createPacket(PacketType.Play.Server.TITLE);

        packet.getTitleActions().write(0, TitleAction.RESET);

        return packet;
    }

    
    // Protocol Manager //
    private static ProtocolManager _protocolManager;
    private static ProtocolManager getManager() {
        if (_protocolManager != null)
            return _protocolManager;
        return _protocolManager = ProtocolLibrary.getProtocolManager();
    }

    // Helpers //
    public static void updateEntity(Entity entity) {
        getManager().updateEntity(entity, new ArrayList<>(entity.getWorld().getEntitiesByClass(Player.class)));
    }

    private static WrappedDataWatcher getDefaulWatcher(World world, EntityType type) {
        Entity entity = world.spawnEntity(new Location(world, 0, 256, 0), type);
        WrappedDataWatcher dataWatcher = WrappedDataWatcher.getEntityWatcher(entity).deepClone();

        entity.remove();
        return dataWatcher;
    }

    public static boolean sendPacket(Player player, PacketContainer packet) {
        try {
            getManager().sendServerPacket(player, packet);
            return true;
        } catch (InvocationTargetException e) {
            BedWarsPlugin.getInstance().getLogger().log(Level.WARNING, "Could not send packet", e);
            return false;
        }
    }
}