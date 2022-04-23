package com.notker.xp_storage.regestry;

import com.notker.xp_storage.XpStorage;
import com.notker.xp_storage.items.StorageBlockItem;
import com.notker.xp_storage.items.StorageItem;
import com.notker.xp_storage.items.Xp_dust;
import com.notker.xp_storage.items.Xp_removerItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;


public class ModItems {

    //Items
    public static final StorageItem XP_ROD = new StorageItem(new Item.Settings().group(XpStorage.ITEM_GROUP));
    public static final StorageItem SOUL_COPPER_BLEND = new StorageItem(new Item.Settings().group(XpStorage.ITEM_GROUP));
    public static final StorageItem SOUL_COPPER_INGOT = new StorageItem(new Item.Settings().group(XpStorage.ITEM_GROUP));
    public static final Xp_removerItem XP_REMOVER = new Xp_removerItem(new Item.Settings().group(XpStorage.ITEM_GROUP));
    public static final StorageItem INSPECTOR = new StorageItem(new Item.Settings().group(XpStorage.ITEM_GROUP).maxCount(1).rarity(Rarity.EPIC));
    public static final StorageItem LOCK = new StorageItem(new Item.Settings().group(XpStorage.ITEM_GROUP).maxCount(16).rarity(Rarity.UNCOMMON));
    public static final Xp_dust XP_DUST = new Xp_dust(new Item.Settings().group(XpStorage.ITEM_GROUP));
    public static final StorageItem KEY = new StorageItem(new Item.Settings().group(XpStorage.ITEM_GROUP).maxCount(16).rarity(Rarity.UNCOMMON));

    //Block Items
    public static final StorageBlockItem BLOCK_XP_OBELISK = new StorageBlockItem(ModBlocks.BLOCK_XP_OBELISK, new Item.Settings().group(XpStorage.ITEM_GROUP).maxCount(16).rarity(Rarity.RARE));
public static final BlockItem BLOCK_SOUL_COPPER = new BlockItem(ModBlocks.BLOCK_SOUL_COPPER, new Item.Settings().group(XpStorage.ITEM_GROUP).maxCount(64));

    public static void registerItems() {
        //Items
        Registry.register(Registry.ITEM, new Identifier(XpStorage.MOD_ID, "xp_rod"), XP_ROD);
        Registry.register(Registry.ITEM, new Identifier(XpStorage.MOD_ID, "soul_copper_blend"), SOUL_COPPER_BLEND);
        Registry.register(Registry.ITEM, new Identifier(XpStorage.MOD_ID, "soul_copper_ingot"), SOUL_COPPER_INGOT);
        Registry.register(Registry.ITEM, new Identifier(XpStorage.MOD_ID, "xp_remover"), XP_REMOVER);
        Registry.register(Registry.ITEM, new Identifier(XpStorage.MOD_ID, "inspector"), INSPECTOR);
        Registry.register(Registry.ITEM, new Identifier(XpStorage.MOD_ID, "lock"), LOCK);
        Registry.register(Registry.ITEM, new Identifier(XpStorage.MOD_ID, "xp_dust"), XP_DUST);
        Registry.register(Registry.ITEM, new Identifier(XpStorage.MOD_ID, "key"), KEY);

        //BlockItems
        Registry.register(Registry.ITEM, new Identifier(XpStorage.MOD_ID, "block_xp_obelisk"), BLOCK_XP_OBELISK);
        Registry.register(Registry.ITEM, new Identifier(XpStorage.MOD_ID, "block_soul_copper"), BLOCK_SOUL_COPPER);
    }
}
