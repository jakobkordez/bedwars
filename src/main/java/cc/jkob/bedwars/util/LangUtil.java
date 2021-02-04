package cc.jkob.bedwars.util;

import org.bukkit.ChatColor;

public class LangUtil {
    private static final String[] romans = new String[]{"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};

    public static String getRomanNumber(int num) {
        if (num < 1 || num > 10) return String.valueOf(num);

        return romans[num - 1];
    }

    public static String capitalize(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }

    public static String hideString(String string) {
        return string.replaceAll("(.)", ChatColor.COLOR_CHAR + "$1");
    }

    public static String revealString(String string) {
        return string.replace("" + ChatColor.COLOR_CHAR, "");
    }
}
