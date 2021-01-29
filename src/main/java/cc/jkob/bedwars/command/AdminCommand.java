package cc.jkob.bedwars.command;

import cc.jkob.bedwars.BedWarsPlugin;

public abstract class AdminCommand extends BaseCommand {
    public AdminCommand(BedWarsPlugin plugin) {
        super(plugin);
    }

    @Override
    public final boolean requiresOp() {
        return true;
    }
}
