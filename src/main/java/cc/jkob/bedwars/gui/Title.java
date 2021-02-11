package cc.jkob.bedwars.gui;

import com.comphenix.protocol.events.PacketContainer;

import cc.jkob.bedwars.util.PacketUtil;

public class Title {
    private String title, subTitle = "";
    private int fadeIn = 0, fadeOut = 0, stay = 40;

    public Title(String title) {
        this.title = title;
    }

    public Title(String title, String subTitle) {
        this.title = title;
        this.subTitle = subTitle;
    }

    public Title(String title, int stay) {
        this.title = title;
        this.stay = stay;
    }

    public Title(String title, String subTitle, int stay) {
        this.title = title;
        this.subTitle = subTitle;
        this.stay = stay;
    }

    public Title(String title, int fadeIn, int stay, int fadeOut) {
        this.title = title;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
    }

    public Title(String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        this.title = title;
        this.subTitle = subTitle;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
    }

    public void setTitle(String title) {
        this.title = title;
        titlePacket = null;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
        subTitlePacket = null;
    }

    public void setTimes(int fadeIn, int stay, int fadeOut) {
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
        timesPacket = null;
    }

    // Packets //
    private static PacketContainer resetPacket; 
    private PacketContainer titlePacket, subTitlePacket, timesPacket;

    public static PacketContainer getResetPacket() {
        if (resetPacket != null) return resetPacket;

        return resetPacket = PacketUtil.createTitleResetPacket();
    }

    public PacketContainer getTitlePacket() {
        if (titlePacket != null) return titlePacket;

        return titlePacket = PacketUtil.createTitlePacket(title);
    }

    public PacketContainer getSubTitlePacket() {
        if (subTitlePacket != null) return subTitlePacket;

        return subTitlePacket = PacketUtil.createSubTitlePacket(subTitle);
    }

    public PacketContainer getTimesPacket() {
        if (timesPacket != null) return timesPacket;

        return timesPacket = PacketUtil.createTitleTimesPacket(fadeIn, stay, fadeOut);
    }
}
