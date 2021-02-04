package cc.jkob.bedwars.task;

import cc.jkob.bedwars.game.Game;
import cc.jkob.bedwars.game.GameCycle;

public abstract class GameCycleTask extends RunnableWithStatus {
    protected final Game game;
    private final GameCycle gameCycle;

    public GameCycleTask(Game game, GameCycle gameCycle) {
        this.game = game;
        this.gameCycle = gameCycle;
    }

    @Override
    public void run() {
        gameCycle.triggerNext();
        
        super.run();
    }
}
