package cc.jkob.bedwars.game;

import java.util.UUID;
import java.util.Map.Entry;

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
import cc.jkob.bedwars.game.Game.GameState;
import cc.jkob.bedwars.game.PlayerInventory.Armor;
import cc.jkob.bedwars.gui.Title;
import cc.jkob.bedwars.shop.Shop;
import cc.jkob.bedwars.util.ChatUtil;
import cc.jkob.bedwars.util.PlayerUtil;

public class PlayerData {
    public final UUID id;
    public String name;
    private GamePlayer gd;
    private Player player;

    public PlayerData(UUID id) {
        this.id = id;
        getName();
    }

    public boolean isInGame() {
        return gd != null;
    }

    public String getName() {
        if (name != null) return name;

        Player player = getPlayer();
        if (player == null) return "?";
        return name = player.getDisplayName();
    }

    public void tpMainLobby() {
        Player player = getPlayer();
        if (player != null)
        resetPlayer(player);
        player.teleport(GameManager.instance.lobby);
    }

    public boolean joinGame(Game game) {
        if (isInGame()) gd.leaveGame();
        switch (game.getState()) {
            case RUNNING:
                gd = new GamePlayer(this, game);
                game.joinPlayer(gd);
                gd.setSpectator();
                gd.spectateMode(PlayerState.SPECTATING);
                break;
            case STOPPED:
                game.init();
            case WAITING:
                gd = new GamePlayer(this, game);
                game.joinPlayer(gd);
                gd.toLobby();
                break;
            default:
                break;
        }
        return gd != null;
    }

    public GamePlayer getGamePlayer() {
        return gd;
    }

    public Player getPlayer() {
        return player;
    }

    public void onDamage(PlayerData damager) {
        if (isInGame()) {
            gd.onDamage(damager.gd);
            return;
        }
    }

    public void onDeath(DamageCause cause) {
        if (isInGame()) {
            gd.onDeath(cause);
            return;
        }

        tpMainLobby();
    }

    public void onDisconnect() {
        if (isInGame()) gd.onDisconnent();
        GameManager.instance.broadcastLobby(ChatUtil.format(getName(), " left"));
    }

    public void onJoin(Player player) {
        this.player = player;
        GameManager.instance.broadcastLobby(ChatUtil.format(getName(), " joined"));

        if (isInGame() && gd.tryRejoin()) return;
        tpMainLobby();
    }

    public static void resetPlayer(Player player) {
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
        public final PlayerData player;
        public final Game game;
        private boolean spectator;
        private Team team;
        private PlayerState state;
        private GamePlayer lastDamage;
        private long lastDamageTime;
        private PlayerInventory inventory;

        private GamePlayer(PlayerData player, Game game) {
            this.player = player;
            this.game = game;
        }

        public String getFormattedName() {
            return team == null
                ? ChatColor.GRAY + getName()
                : team.getColor().getChatColor() + getName();
        }

        public Team getTeam() {
            return team;
        }

        public void setTeam(Team team) {
            spectator = false;
            this.team = team;
        }

        public boolean isSpectator() {
            return spectator;
        }

        public void setSpectator() {
            spectator = true;
            team = null;
        }

        public PlayerState getState() {
            return state;
        }

        public Armor getArmor() {
            return inventory.getArmor();
        }

        public void setArmor(Armor armor) {
            inventory.setArmor(armor);
            player.player.getEquipment().setArmorContents(inventory.buildArmor());
        }

        public void leaveGame() {
            PlayerUtil.clearScoreboard(player);
            game.leavePlayer(id);
            gd = null;
            tpMainLobby();
        }

        public boolean tryRejoin() {
            if (game.getState() != GameState.RUNNING) {
                leaveGame();
                return false;
            }

            onRejoin();
            return true;
        }

        private void onRejoin() {
            PlayerUtil.send(player, game.getScoreboard());
            if (isSpectator())
                spectateMode(PlayerState.SPECTATING);
            else if (gd.team.hasBed()) {
                respawnAfter(5);
                game.broadcast(ChatUtil.format(getFormattedName(), " rejoined"));
            }
            else spectateMode(PlayerState.DEAD);
        }

        public void onStart() {
            PlayerUtil.send(player, game.getScoreboard());
            if (isSpectator()) {
                spectateMode(PlayerState.SPECTATING);
                return;
            }
            inventory = new PlayerInventory(team.getColor());
            spawn();
        }

        private void onDamage(GamePlayer damager) {
            lastDamage = damager;
            lastDamageTime = System.currentTimeMillis();
        }
    
        private void onDeath(DamageCause cause) {
            if (isSpectator()) {
                state = PlayerState.SPECTATING;
                return;
            }

            inventory.downgradeTools();

            GamePlayer damager = getLastDamage();
            String killMsg;
            if (damager != null) {
                PlayerUtil.play(damager.player, Sound.ORB_PICKUP);
                killMsg = ChatUtil.getKillMessage(this, cause, damager);

                if (damager.getState() == PlayerState.ALIVE) {
                    Inventory inv = damager.player.getPlayer().getInventory();
                    for (Entry<Material, Integer> mat : Shop.getWallet(player.getPlayer()).entrySet())
                        inv.addItem(new ItemStack(mat.getKey(), mat.getValue()));
                }

            } else killMsg = ChatUtil.getKillMessage(this, cause, null);
            game.broadcast(killMsg);

            lastDamage = null;

            if (team.hasBed()) {
                respawnAfter(5);
                return;
            }

            spectateMode(PlayerState.DEAD);
            PlayerUtil.send(player, new Title(ChatColor.RED + "YOU DIED", 0, 20, 20));
            if (team.playersAlive() == 0)
                game.onTeamElim(team);
        }

        public GamePlayer getLastDamage() {
            if (System.currentTimeMillis() - lastDamageTime > 20000) return null;
            return lastDamage;
        }

        private void onDisconnent() {
            if (gd.state == PlayerState.ALIVE)
                onDeath(DamageCause.CUSTOM);
            state = PlayerState.DISCONNECTED;
            game.broadcast(ChatUtil.format(getFormattedName(), " disconnected"));
        }

        private void spectateMode(PlayerState state) {
            this.state = state;
            PlayerData.resetPlayer(player.player);
            player.player.setGameMode(GameMode.SPECTATOR);
            Location loc = game.getLobby().clone().add(0, -6, 0);
            loc.setPitch(90);
            player.player.teleport(loc);
        }
    
        private void toLobby() {
            PlayerData.resetPlayer(player.player);
            player.player.teleport(game.getLobby());
        }
    
        private void spawn() {
            state = PlayerState.ALIVE;
            PlayerData.resetPlayer(player.player);
            player.player.teleport(team.getSpawn());
            player.player.getInventory().setContents(inventory.buildInventory());
            player.player.getEquipment().setArmorContents(inventory.buildArmor());
        }
    
        private void respawnAfter(int afterSeconds) {
            spectateMode(PlayerState.RESPAWNING);
            new BukkitRunnable(){
                int i = afterSeconds;
                @Override
                public void run() {
                    if (game.getState() != GameState.RUNNING || state != PlayerState.RESPAWNING) {
                        cancel();
                    } else if (i > 0) {
                        PlayerUtil.send(player, new Title(ChatColor.RED + "YOU DIED",
                            ChatColor.YELLOW + "Respawning in " +
                            ChatColor.RED + i-- +
                            ChatColor.YELLOW + " seconds",
                            30));
                    } else {
                        cancel();
                        PlayerUtil.send(player, new Title(ChatColor.GREEN + "RESPAWNED", 0, 20, 20));
                        spawn();
                    }
                }
            }.runTaskTimer(BedWarsPlugin.getInstance(), 0, 20);
        }
    }

    public static enum PlayerState {
        SPECTATING, ALIVE, RESPAWNING, DISCONNECTED, DEAD
    }
}
