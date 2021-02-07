package cc.jkob.bedwars.game;

import java.util.Objects;
import java.util.stream.Stream;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import cc.jkob.bedwars.game.Game.PlayerData;
import cc.jkob.bedwars.game.Game.PlayerData.PlayerState;
import cc.jkob.bedwars.gui.Title;
import cc.jkob.bedwars.util.BlockUtil;
import cc.jkob.bedwars.util.PlayerUtil;

public class Team {
    private String name;
    private TeamColor color;
    private Generator ironGen, goldGen;
    private Location spawn, bedFeet, bedHead;

    public Team(String name, TeamColor color) {
        this.name = name;
        this.color = color;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }

    public void setGens(Location pos) {
        ironGen = new Generator(pos, GeneratorType.IRON);
        goldGen = new Generator(pos, GeneratorType.GOLD);
    }

    public void setBed(Location bedHead, Location bedFeet) {
        this.bedHead = bedHead.getBlock().getLocation();
        this.bedFeet = bedFeet.getBlock().getLocation();
    }

    public String getName() {
        return name;
    }

    public String getFormattedName() {
        return color.getChatColor() + name;
    }

    public TeamColor getColor() {
        return color;
    }

    public Generator getIronGen() {
        return ironGen;
    }

    public Generator getGoldGen() {
        return goldGen;
    }

    public Location getSpawn() {
        return spawn;
    }

    public boolean isTeamBed(Location p) {
        p = p.getBlock().getLocation();
        return bedFeet.equals(p) || bedHead.equals(p);
    }

    // transient
    private transient Game game;
    private transient boolean bedAlive;

    public void init(Game game) {
        this.game = game;
        bedAlive = true;

        if (bedFeet.getBlock().isEmpty() || bedHead.getBlock().isEmpty())
            BlockUtil.placeBed(bedFeet, bedHead);
    }

    public void startGens() {
        ironGen.start();
        goldGen.start();
    }

    public void stopGens() {
        ironGen.stop();
        goldGen.stop();
    }

    public void destroyBed() {
        bedFeet.getBlock().setType(Material.AIR);
        bedHead.getBlock().setType(Material.AIR);
        bedAlive = false;

        PlayerUtil.playSound(getPlayerStream(), Sound.ENDERDRAGON_GROWL);
        PlayerUtil.sendTitle(getPlayerStream(), new Title(
            "" + ChatColor.RED + ChatColor.BOLD + "Bed Destroyed",
            "You will no longer respawn", 0, 40, 20));
    }

    public boolean hasBed() {
        return bedAlive;
    }

    public int playersAlive() {
        return (int) game.getPlayers().values().parallelStream()
            .filter(p -> p.getTeam() == this)
            .filter(p -> p.getState() == PlayerState.ALIVE)
            .count();
    }

    public int getPlayerCount() {
        return (int) game.getPlayers().values().parallelStream()
            .filter(p -> p.getTeam() == this)
            .count();
    }

    public Stream<Player> getPlayerStream() {
        return game.getPlayers().values().parallelStream()
            .filter(p -> p.getTeam() == this)
            .map(PlayerData::getPlayer)
            .filter(Objects::nonNull);
    }
}
