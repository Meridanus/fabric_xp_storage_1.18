package com.notker.xp_storage;

import com.notker.xp_storage.regestry.ModBlocks;
import com.notker.xp_storage.regestry.ModFluids;
import com.notker.xp_storage.regestry.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@SuppressWarnings("UnstableApiUsage")
public class XpStorage implements ModInitializer {

    public static final  String MOD_ID = "xps";
    public static final Long MB_PER_XP = FluidConstants.BUCKET / 100;
    public static final  int XP_PER_BERRIE = 3;
    public static final  Long MB_PER_BERRIE = XP_PER_BERRIE * MB_PER_XP;

    public static final int shiftKey = 340;

    public static final ItemGroup ITEM_GROUP = FabricItemGroup.builder(new Identifier(MOD_ID, "general"))
            .displayName(Text.literal("Test Group Name"))
            .icon(() -> new ItemStack(ModItems.BLOCK_XP_OBELISK))
            .entries((enabledFeatures, entries, operatorEnabled) -> {
                entries.add(ModItems.BLOCK_XP_OBELISK);
            })
            .build();


    @Override
    public void onInitialize() {
        ModItems.registerItems();
        ModBlocks.registerBlocks();
        ModFluids.registerFluids();
    }


}
