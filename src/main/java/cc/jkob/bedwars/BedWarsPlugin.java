package cc.jkob.bedwars;

import cc.jkob.bedwars.game.GameManager;
import cc.jkob.bedwars.listener.BlockListener;
import cc.jkob.bedwars.listener.InteractPacketListener;
import cc.jkob.bedwars.listener.PlayerListener;
import cc.jkob.bedwars.listener.WorldListener;
import cc.jkob.bedwars.shop.DyableShopItem;
import cc.jkob.bedwars.shop.ItemShop;
import cc.jkob.bedwars.shop.Shop;
import cc.jkob.bedwars.shop.ShopCategory;
import cc.jkob.bedwars.shop.ShopItem;
import cc.jkob.bedwars.util.BedWarsCommandExecutor;

import com.comphenix.protocol.ProtocolLibrary;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

public class BedWarsPlugin extends JavaPlugin {
    private static BedWarsPlugin instance;
    public static BedWarsPlugin getInstance() {
        return instance;
    }

    private GameManager gameManager;

    @Override
    public void onEnable() {
        instance = this;
        gameManager = new GameManager();

        ConfigurationSerialization.registerClass(ShopItem.class);
        ConfigurationSerialization.registerClass(DyableShopItem.class);
        ConfigurationSerialization.registerClass(ShopCategory.class);
        ConfigurationSerialization.registerClass(ItemShop.class);

        new BlockListener(this);
        new PlayerListener(this);
        new WorldListener(this);

        ProtocolLibrary.getProtocolManager().addPacketListener(new InteractPacketListener(this));

        Shop.getItemShop();

        getCommand("bw").setExecutor(new BedWarsCommandExecutor(this));
    }

    @Override
    public void onDisable() {
    }

    public GameManager getGameManager() {
        return gameManager;
    }
}
