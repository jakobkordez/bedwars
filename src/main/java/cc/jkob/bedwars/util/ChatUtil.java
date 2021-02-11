package cc.jkob.bedwars.util;

import org.bukkit.ChatColor;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import cc.jkob.bedwars.game.PlayerData;

public class ChatUtil {
    public static String format(String ...msgs) {
        return ChatColor.GRAY + String.join("" + ChatColor.GRAY, msgs);
    }

    public static String getKillMessage(PlayerData playerD, DamageCause cause, PlayerData damager) {
        String msg, damagerName = null, playerName = playerD.getFormattedName();
        if (damager != null) damagerName = damager.getFormattedName();

        switch (cause) {
            case BLOCK_EXPLOSION:
            case ENTITY_EXPLOSION:
                msg = damager == null
                    ? ChatUtil.format(playerName, " exploded")
                    : ChatUtil.format(playerName, " got blown up by ", damagerName);
                break;
            case FALL:
                msg = damager == null
                    ? ChatUtil.format(playerName, " fell to his death")
                    : ChatUtil.format(playerName, " was pushed off a clif by ", damagerName);
                break;
            case FIRE:
            case FIRE_TICK:
                msg = damager == null
                    ? ChatUtil.format(playerName, " burned to death")
                    : ChatUtil.format(playerName, " was burned by ", damagerName);
                break;
            case PROJECTILE:
                msg = damager == null
                    ? ChatUtil.format(playerName, " was shot to death")
                    : ChatUtil.format(playerName, " was shot by ", damagerName);
                break;
            case VOID:
                msg = damager == null
                    ? ChatUtil.format(playerName, " fell in the void")
                    : ChatUtil.format(playerName, " was pushed in the void by ", damagerName);
                break;
            default:
                msg = damager == null
                    ? ChatUtil.format(playerName, " died")
                    : ChatUtil.format(playerName, " was killed by ", damagerName);
                break;
        }

        if (!playerD.getTeam().hasBed())
            msg += "" + ChatColor.AQUA + ChatColor.ITALIC + "  FINAL KILL!";

        return msg;
    }
}
