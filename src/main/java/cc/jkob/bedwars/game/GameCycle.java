package cc.jkob.bedwars.game;

import java.util.Iterator;

import com.google.common.collect.Lists;

import cc.jkob.bedwars.BedWarsPlugin;
import cc.jkob.bedwars.task.GameCycleTask;
import cc.jkob.bedwars.task.GeneratorUpgradeTask;

public class GameCycle {
    private Iterator<Stage> stageIt;
    private Stage currStage;

    public GameCycle(Game game) {
        stageIt = Lists.newArrayList(
            Stage.generatorUpgrade(game, this, GeneratorType.DIAMOND, 7200, "Diamond II"),
            Stage.generatorUpgrade(game, this, GeneratorType.EMERALD, 7200, "Emerald II"),
            Stage.generatorUpgrade(game, this, GeneratorType.DIAMOND, 7200, "Diamond III"),
            Stage.generatorUpgrade(game, this, GeneratorType.EMERALD, 7200, "Emerald III"),
            Stage.bedDestruction(game, this, 12000, "Bed Gone"),
            Stage.gameTie(game, this, 12000, "Tie")
        ).iterator();
    }

    public long getStageRemainingTicks() {
        return currStage.task.getRemainingTicks();
    }

    public String getStageTitle() {
        return currStage.title;
    }

    public boolean stop() {
        if (currStage == null) return false;

        currStage.task.cancel();
        currStage = null;
        return true;
    }

    public boolean triggerNext() {
        if (!stageIt.hasNext()) return false;

        currStage = stageIt.next();
        currStage.task.runTaskLater(BedWarsPlugin.getInstance(), currStage.delay);
        return true;
    }


    private static class Stage {
        public final GameCycleTask task;
        public final long delay;
        public final String title;

        private Stage(GameCycleTask task, long delay, String title) {
            this.task = task;
            this.delay = delay;
            this.title = title;
        }

        private static Stage generatorUpgrade(Game game, GameCycle cycle, GeneratorType type, long delay, String title) {
            return new Stage(new GeneratorUpgradeTask(game, cycle, type), delay, title);
        }
    
        private static Stage bedDestruction(Game game, GameCycle cycle, long delay, String title) {
            return new Stage(
                new GameCycleTask(game, cycle){
                    @Override
                    public void run() {
                        game.getTeams().forEach((k, t) -> t.destroyBed());
                        
                        super.run();
                    }
                }, delay, title);
        }
    
        private static Stage gameTie(Game game, GameCycle cycle, long delay, String title) {
            return new Stage(
                new GameCycleTask(game, cycle){
                    @Override
                    public void run() {
                        game.end(null);
                        
                        super.run();
                    }
                }, delay, title);
        }
    }
}
