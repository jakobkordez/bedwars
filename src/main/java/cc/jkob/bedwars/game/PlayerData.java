package cc.jkob.bedwars.game;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import cc.jkob.bedwars.BedWarsPlugin;
import cc.jkob.bedwars.game.Game.State;

public class PlayerData {
    public final UUID id;
    private Game game;
    private boolean spectator;
    private Team team;
    private PlayerState state;
    private PlayerData lastDamage;
    private long lastDamageTime;

    public PlayerData(UUID id) {
        this.id = id;
    }

    public boolean joinGame(Game game) {
        leaveGame();
        this.game = game;
        if (!game.joinPlayer(this))
            return false;
        getPlayer().teleport(game.getLobby());
        return true;
    }

    public void leaveGame() {
        if (game == null) return;
        if (game.getState() == State.STOPPED) return;
        if (!game.getPlayers().containsKey(id)) return;
        game.leavePlayer(id);
    }

    public Game getGame() {
        return game;
    }

    public String getFormattedName() {
        if (team == null) return ChatColor.GRAY + getPlayer().getName();

        return team.getColor().getChatColor() + getPlayer().getName();
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

    public void onDamage(PlayerData player) {
        lastDamage = player;
        lastDamageTime = System.currentTimeMillis();
    }

    public void onDeath() {
        if (isSpectator()) {
            setState(PlayerState.SPECTATING);
            return;
        }

        // TODO: Downgrade tools
        
        Player lastD = lastDamage.getPlayer();
        if (System.currentTimeMillis() - lastDamageTime < 60000 && lastD != null) {
            // TODO: Kill message
        }

        if (team.hasBed())
            setState(PlayerState.RESPAWNING);
        else
            setState(PlayerState.DEAD);
    }

    public void setState(PlayerState state) {
        // TODO: Implement
        switch (state) {
            case ALIVE:
                spawn();
                break;
            case DISCONNECTED:
                break;
            case RESPAWNING:
                new BukkitRunnable(){
                    @Override
                    public void run() {
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
        Location loc = game.getLobby().clone().add(0, -4, 0);
        loc.setPitch(90);
        player.teleport(loc);
        player.setGameMode(GameMode.SPECTATOR);
        clearInventory(player);
    }

    private void spawn() {
        Player player = getPlayer();
        if (player == null) return;
        player.setHealth(player.getMaxHealth());
        player.teleport(team.getSpawn());
        player.setGameMode(GameMode.SURVIVAL);
        resetPotionEffects(player);
        clearInventory(player);
        // TODO: Give inventory
    }

    private static void resetPotionEffects(Player player) {
        player.getActivePotionEffects()
            .forEach(e -> player.removePotionEffect(e.getType()));
    }

    private static void clearInventory(Player player) {
        player.getInventory().clear();
    }

    public static enum PlayerState {
        SPECTATING, ALIVE, RESPAWNING, DISCONNECTED, DEAD
    }
}
