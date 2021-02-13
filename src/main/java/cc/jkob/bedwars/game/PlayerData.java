package cc.jkob.bedwars.game;

import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import cc.jkob.bedwars.BedWarsPlugin;
import cc.jkob.bedwars.gui.Title;
import cc.jkob.bedwars.shop.Shop;
import cc.jkob.bedwars.util.ChatUtil;
import cc.jkob.bedwars.util.PlayerUtil;

public class PlayerData {
    public final UUID id;
    public String name;
    private GamePlayer gd = new GamePlayer(null);

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
        gd.game = game;
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
        if (gd.game == null) return;
        gd.game.leavePlayer(id);
        gd = new GamePlayer(null);
        // TODO: TP out
    }

    public Game getGame() {
        return gd.game;
    }

    public String getFormattedName() {
        if (gd.team == null) return ChatColor.GRAY + getName();

        return gd.team.getColor().getChatColor() + getName();
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(id);
    }

    public boolean isSpectator() {
        return gd.spectator;
    }

    public void setSpectator() {
        gd.spectator = true;
        gd.team = null;
    }

    public void setTeam(Team team) {
        gd.spectator = false;
        gd.team = team;
    }

    public Team getTeam() {
        return gd.team;
    }

    public PlayerState getState() {
        return gd.state;
    }

    public void onStart() {
        if (isSpectator()) {
            setState(PlayerState.SPECTATING);
            return;
        }
        gd.inventory = new PlayerInventory(gd.team.getColor());
        setState(PlayerState.ALIVE);
    }

    public void onDamage(PlayerData damager) {
        gd.lastDamage = damager;
        gd.lastDamageTime = System.currentTimeMillis();
    }

    public void onDeath(DamageCause cause) {
        if (isSpectator()) {
            setState(PlayerState.SPECTATING);
            return;
        }

        gd.inventory.downgradeTools();

        String killMsg;
        if (System.currentTimeMillis() - gd.lastDamageTime < 60000) {
            PlayerUtil.play(gd.lastDamage, Sound.ORB_PICKUP);
            killMsg = ChatUtil.getKillMessage(this, cause, gd.lastDamage);

            if (gd.lastDamage.getState() == PlayerState.ALIVE) {
                Inventory inv = gd.lastDamage.getPlayer().getInventory();
                for (Entry<Material, Integer> mat : Shop.getWallet(getPlayer()).entrySet())
                    inv.addItem(new ItemStack(mat.getKey(), mat.getValue()));
            }

        } else killMsg = ChatUtil.getKillMessage(this, cause, null);
        PlayerUtil.send(gd.game.getPlayerStream(true), killMsg);

        gd.lastDamage = null;
        gd.lastDamageTime = 0;

        if (gd.team.hasBed())
            setState(PlayerState.RESPAWNING);
        else
            setState(PlayerState.DEAD);
    }

    private void setState(PlayerState state) {
        if (gd.game == null) return;
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
                        if (gd.game == null) {
                            cancel();
                            return;
                        }
                        sendTitle(new Title(ChatColor.RED + "YOU DIED",
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
                sendTitle(new Title(ChatColor.RED + "YOU DIED", 0, 20, 20));
            case SPECTATING:
                spectateMode();
                break;
        }
        gd.state = state;
    }

    private void spectateMode() {
        Player player = getPlayer();
        if (player == null) return;
        resetPlayer(player);

        player.setGameMode(GameMode.SPECTATOR);
        Location loc = gd.game.getLobby().clone().add(0, -4, 0);
        loc.setPitch(90);
        player.teleport(loc);
    }

    private void lobby() {
        Player player = getPlayer();
        if (player == null) return;
        resetPlayer(player);
        player.teleport(gd.game.getLobby());
    }

    private void spawn() {
        Player player = getPlayer();
        if (player == null) return;
        resetPlayer(player);
        player.teleport(gd.team.getSpawn());
        player.getInventory().setContents(gd.inventory.buildInventory());
        player.getEquipment().setArmorContents(gd.inventory.buildArmor());
    }

    private void sendTitle(Title title) {
        PlayerUtil.send(this, title);
    }

    private static void resetPlayer(Player player) {
        // Gamemode
        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        
        // Potion effects
        player.getActivePotionEffects()
            .forEach(e -> player.removePotionEffect(e.getType()));

        // Inventory
        player.getInventory().clear();
        ItemStack[] armor = player.getEquipment().getArmorContents();
        for (int i = 0; i < armor.length; ++i) armor[i] = null;
        player.getEquipment().setArmorContents(armor);
    }

    public class GamePlayer {
        private Game game;
        private boolean spectator;
        private Team team;
        private PlayerState state;
        private PlayerData lastDamage;
        private long lastDamageTime;
        private PlayerInventory inventory;

        private GamePlayer(Game game) {
            this.game = game;
        }
    }

    public static enum PlayerState {
        SPECTATING, ALIVE, RESPAWNING, DISCONNECTED, DEAD
    }
}
