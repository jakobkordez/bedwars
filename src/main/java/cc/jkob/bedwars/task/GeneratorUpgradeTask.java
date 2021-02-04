package cc.jkob.bedwars.task;

import cc.jkob.bedwars.game.Game;
import cc.jkob.bedwars.game.GameCycle;
import cc.jkob.bedwars.game.Generator;
import cc.jkob.bedwars.game.GeneratorType;

public class GeneratorUpgradeTask extends GameCycleTask {
    private final GeneratorType genType;

    public GeneratorUpgradeTask(Game game, GameCycle gameCycle, GeneratorType genType) {
        super(game, gameCycle);
        this.genType = genType;
    }

    @Override
    public void run() {
        game.getGenerators().stream()
            .filter(g -> g.getType() == genType)
            .forEach(Generator::upgrade);
        
        super.run();
    }
}
