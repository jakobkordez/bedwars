package cc.jkob.bedwars.game;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import cc.jkob.bedwars.BedWarsPlugin;
import cc.jkob.bedwars.game.Game.State;
import cc.jkob.bedwars.gui.Title;
import cc.jkob.bedwars.util.ChatUtil;
import cc.jkob.bedwars.util.PlayerUtil;

public class PlayerData {
    public final UUID id;
    public String name;
    private Game game;
    private boolean spectator;
    private Team team;
    private PlayerState state;
    private PlayerData lastDamage;
    private long lastDamageTime;

    public PlayerData(UUID id) {
        this.id = id;
        getName();
    }

    public String getName() {
        if (name != null) return name;

        Player player = getPlayer();
        if (player == null) return "?";
        return name = player.getDisplayName();
    }

    public boolean joinGame(Game game) {
        leaveGame();
        this.game = game;
        if (!game.joinPlayer(this))
            return false;
        lobby();
        return true;
    }

    public boolean rejoin() {
        // TODO: Rejoin game
        return false;
    }

    public void leaveGame() {
        if (game == null) return;
        if (game.getState() == State.STOPPED) return;
        if (!game.getPlayers().containsKey(id)) return;
        game.leavePlayer(id);
        // TODO: TP out
    }

    public Game getGame() {
        return game;
    }

    public String getFormattedName() {
        if (team == null) return ChatColor.GRAY + getName();

        return team.getColor().getChatColor() + getName();
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(id);
    }

    public boolean isSpectator() {
        return spectator;
    }

    public void setSpectator() {
        spectator = true;
        team = null;
    }

    public void setTeam(Team team) {
        spectator = false;
        this.team = team;
    }

    public Team getTeam() {
        return team;
    }

    public PlayerState getState() {
        return state;
    }

    public void onStart() {
        if (isSpectator())
            setState(PlayerState.SPECTATING);
        else
            setState(PlayerState.ALIVE);
    }

    public void onDamage(PlayerData damager) {
        lastDamage = damager;
        lastDamageTime = System.currentTimeMillis();
    }

    public void onDeath(DamageCause cause) {
        if (isSpectator()) {
            setState(PlayerState.SPECTATING);
            return;
        }

        // TODO: Downgrade tools

        String killMsg;
        if (System.currentTimeMillis() - lastDamageTime < 60000) {
            PlayerUtil.play(lastDamage, Sound.ORB_PICKUP);
            killMsg = ChatUtil.getKillMessage(this, cause, lastDamage);
            // TODO: Give wallet
        } else killMsg = ChatUtil.getKillMessage(this, cause, null);
        PlayerUtil.send(game.getPlayerStream(true), killMsg);

        lastDamage = null;
        lastDamageTime = 0;

        if (team.hasBed())
            setState(PlayerState.RESPAWNING);
        else
            setState(PlayerState.DEAD);
    }

    private void setState(PlayerState state) {
        // TODO: Implement
        switch (state) {
            case ALIVE:
                spawn();
                break;
            case DISCONNECTED:
                break;
            case RESPAWNING:
                new BukkitRunnable(){
                    int i = 5;
                    @Override
                    public void run() {
                        sendTitle(new Title(ChatColor.RED + "YOU DIED!",
                            ChatColor.YELLOW + "Respawning in " +
                            ChatColor.RED + i-- +
                            ChatColor.YELLOW + " seconds",
                            30));
                        if (i <= 0) cancel();
                    }
                }.runTaskTimer(BedWarsPlugin.getInstance(), 0, 20);
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        sendTitle(new Title(ChatColor.GREEN + "RESPAWNED", 0, 20, 20));
                        setState(PlayerState.ALIVE);
                    }
                }.runTaskLater(BedWarsPlugin.getInstance(), 100);
            case DEAD:
            case SPECTATING:
                spectateMode();
                break;
        }
        this.state = state;
    }

    private void spectateMode() {
        Player player = getPlayer();
        if (player == null) return;
        resetPlayer(player);

        player.setGameMode(GameMode.SPECTATOR);
        Location loc = game.getLobby().clone().add(0, -4, 0);
        loc.setPitch(90);
        player.teleport(loc);
    }

    private void lobby() {
        Player player = getPlayer();
        if (player == null) return;
        resetPlayer(player);
        player.teleport(game.getLobby());
    }

    private void spawn() {
        Player player = getPlayer();
        if (player == null) return;
        resetPlayer(player);
        player.teleport(team.getSpawn());
        // TODO: Give inventory
    }

    private void sendTitle(Title title) {
        PlayerUtil.send(this, title);
    }

    private static void resetPlayer(Player player) {
        // Gamemode
        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(player.getMaxHealth());
        
        // Potion effects
        player.getActivePotionEffects()
            .forEach(e -> player.removePotionEffect(e.getType()));

        // Inventory
        player.getInventory().clear();
        ItemStack[] armor = player.getEquipment().getArmorContents();
        for (int i = 0; i < armor.length; ++i) armor[i] = null;
        player.getEquipment().setArmorContents(armor);
    }

    public static enum PlayerState {
        SPECTATING, ALIVE, RESPAWNING, DISCONNECTED, DEAD
    }
}
