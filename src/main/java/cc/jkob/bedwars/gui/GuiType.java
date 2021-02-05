package cc.jkob.bedwars.gui;

import cc.jkob.bedwars.shop.Shop;

public enum GuiType {
    GAME_ITEM_SHOP,
    GAME_UPGRADE_SHOP;

    public InventoryGui getGui() {
        switch (this) {
            case GAME_ITEM_SHOP:
                return Shop.getItemShop();
            case GAME_UPGRADE_SHOP:
                return Shop.getUpgradeShop();
        }

        return null;
    }
}
