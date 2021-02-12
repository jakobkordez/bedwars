package cc.jkob.bedwars.listener;

import cc.jkob.bedwars.BedWarsPlugin;
import cc.jkob.bedwars.game.Game;
import cc.jkob.bedwars.game.GameManager;
import cc.jkob.bedwars.game.Team;
import cc.jkob.bedwars.game.Game.State;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.world.StructureGrowEvent;

public final class BlockListener extends BaseListener {

    public BlockListener(BedWarsPlugin plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        Game game = GameManager.instance.getGameByWorld(block.getWorld());
        if (game == null) return;

        if (game.getState() == State.STOPPED) return;

        event.setCancelled(true);

        if (game.getState() != State.RUNNING) return;

        if (block.getState().getType() == Material.BED_BLOCK) {
            Team team = game.getTeamByBed(block.getLocation());
            if (team == null) return;

            if (!team.destroyBed(game.getPlayer(event.getPlayer())))
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

        if (game.getState() == State.STOPPED) return;

        if (game.getState() != State.RUNNING) {
            event.setCancelled(true);
            return;
        }

        // TODO: Check invalid placement (spawns, gens, shops, ...)
        // event.setCancelled(true);

        if (block.getType() == Material.TNT) {
            block.setType(Material.AIR);
            block.getWorld().spawn(block.getLocation().add(.5, .5, .5), TNTPrimed.class).setYield(3);
            return;
        }

        game.getPlacedBlocks().add(block.getLocation());
    }

    @EventHandler(ignoreCancelled = true)
    public void onExplode(EntityExplodeEvent event) {
        Game game = GameManager.instance.getGameByWorld(event.getEntity());
        if (game == null) return;

        event.blockList().removeIf(b -> !game.isPlacedBlock(b.getLocation()) || b.getType() == Material.STAINED_GLASS);
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
