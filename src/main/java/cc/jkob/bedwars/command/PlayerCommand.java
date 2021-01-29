package cc.jkob.bedwars.command;

import cc.jkob.bedwars.BedWarsPlugin;

public abstract class PlayerCommand extends BaseCommand {
    public PlayerCommand(BedWarsPlugin plugin) {
        super(plugin);
    }

    @Override
    public final boolean requiresOp() {
        return false;
    }
}
