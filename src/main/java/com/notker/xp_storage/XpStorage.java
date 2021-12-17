package com.notker.xp_storage;

import com.notker.xp_storage.blocks.StorageBlockEntityRenderer;
import com.notker.xp_storage.regestry.ModBlocks;
import com.notker.xp_storage.regestry.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;


public class XpStorage implements ModInitializer {

    public static final  String MOD_ID = "xps";

    public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(
            new Identifier(MOD_ID, "general"),
            () -> new ItemStack(ModItems.BLOCK_XP_OBELISK)
    );


    @Override
    public void onInitialize() {
        ModItems.registerItems();
        ModBlocks.registerBlocks();

    }


}
