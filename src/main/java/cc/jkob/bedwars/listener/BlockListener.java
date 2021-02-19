package cc.jkob.bedwars.listener;

import cc.jkob.bedwars.BedWarsPlugin;
import cc.jkob.bedwars.game.Game;
import cc.jkob.bedwars.game.GameManager;
import cc.jkob.bedwars.game.PlayerData;
import cc.jkob.bedwars.game.Team;
import cc.jkob.bedwars.game.Game.GameState;
import cc.jkob.bedwars.util.BlockUtil;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.util.Vector;

public final class BlockListener extends BaseListener {

    public BlockListener(BedWarsPlugin plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        PlayerData player = GameManager.instance.getPlayer(event.getPlayer());
        if (!player.isInGame()) return;

        event.setCancelled(true);

        Game game = player.getGamePlayer().game;
        if (game.getState() != GameState.RUNNING) return;

        if (block.getState().getType() == Material.BED_BLOCK) {
            Team team = game.getTeamByBed(block.getLocation());
            if (team == null) return;

            if (!team.destroyBed(player.getGamePlayer()))
                event.getPlayer().sendMessage(ChatColor.RED + "You cannot break your own bed");
            return;
        }

        if (game.isPlacedBlock(block.getLocation())) {
            event.setCancelled(false);
            game.getPlacedBlocks().remove(block.getLocation());
        } else event.getPlayer().sendMessage(ChatColor.RED + "You can only break placed blocks");
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();

        Game game = GameManager.instance.getGameByWorld(block.getWorld());
        if (game == null) return;

        if (game.getState() == GameState.STOPPED) return;

        if (game.getState() != GameState.RUNNING) {
            event.setCancelled(true);
            return;
        }

        Location blockLoc = block.getLocation().add(.5, .5, .5);
        if (game.getGenerators().parallelStream().anyMatch(g -> g.getPos().distanceSquared(blockLoc) < 16) ||
            game.getTeams().parallelStream().anyMatch(t -> t.getSpawn().distanceSquared(blockLoc) < 12 || t.getIronGen().getPos().distanceSquared(blockLoc) < 12) ||
            game.getShopkeepers().parallelStream().anyMatch(s -> s.getLoc().distanceSquared(blockLoc) < 9)) {

            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot place blocks here");
            return;
        }

        if (block.getType() == Material.TNT) {
            block.setType(Material.AIR);
            block.getWorld().spawn(blockLoc, TNTPrimed.class);
            return;
        }

        game.getPlacedBlocks().add(block.getLocation());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPrime(ExplosionPrimeEvent event) {
        if (GameManager.instance.getGameByWorld(event.getEntity()) == null) return;

        event.setFire(true);
        event.setRadius(2.5f);
    }

    @EventHandler(ignoreCancelled = true)
    public void onExplode(EntityExplodeEvent event) {
        Game game = GameManager.instance.getGameByWorld(event.getEntity());
        if (game == null) return;

        event.blockList().removeIf(b -> !game.isPlacedBlock(b.getLocation()));
        List<Block> glass = event.blockList().parallelStream()
            .filter(b -> b.getType() == Material.STAINED_GLASS)
            .collect(Collectors.toList());
        event.blockList().removeAll(glass);
        Vector v = new Vector(.5, .5, .5);
        event.blockList().removeIf(b ->
            glass.parallelStream().anyMatch(g ->
                BlockUtil.blocks(event.getLocation().toVector(), b.getLocation().toVector().add(v), g.getLocation().toVector().add(v))));
    }

    @EventHandler(ignoreCancelled = true)
    public void onIgnite(BlockIgniteEvent event) {
        if (!isEventInGame(event)) return;

        switch (event.getCause()) {
            case EXPLOSION:
            case FIREBALL:
                break;
            default:
                event.setCancelled(true);
                break;
        }
    }

    // Cancel all other block events in games
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBurn(BlockBurnEvent event) {
        if (isEventInGame(event))
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onFade(BlockFadeEvent event) {
        if (isEventInGame(event))
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onForm(BlockFormEvent event) {
        if (isEventInGame(event))
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onSpread(BlockSpreadEvent event) {
        if (isEventInGame(event))
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockGrow(BlockGrowEvent event) {
        if (isEventInGame(event))
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onStructureGrow(StructureGrowEvent event) {
        if (GameManager.instance.getGameByWorld(event.getLocation()) != null)
            event.setCancelled(true);
    }

    private boolean isEventInGame(BlockEvent event) {
        return GameManager.instance.getGameByWorld(event.getBlock().getWorld()) != null;
    }
}
