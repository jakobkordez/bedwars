package cc.jkob.bedwars.util;

import java.util.Comparator;

import cc.jkob.bedwars.game.Team;

public class SortByPlayers implements Comparator<Team> {
    @Override
    public int compare(Team o1, Team o2) {
        int i = o2.getPlayerCount() - o1.getPlayerCount();
        if (i != 0)
            return i;
        return o1.getColor().compareTo(o2.getColor());
    }
}
