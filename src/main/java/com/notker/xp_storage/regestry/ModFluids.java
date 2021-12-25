package com.notker.xp_storage.regestry;

import com.notker.xp_storage.XpStorage;
import com.notker.xp_storage.fluids.Xp_fluid;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModFluids {

    public static FlowableFluid STILL_XP;
    public static FlowableFluid FLOWING_XP;
    public static Item XP_BUCKET;
    public static Block XP;

    public static void registerFluids() {
        STILL_XP = Registry.register(Registry.FLUID, new Identifier(XpStorage.MOD_ID, "xp"), new Xp_fluid.Still());
        FLOWING_XP = Registry.register(Registry.FLUID, new Identifier(XpStorage.MOD_ID, "flowing_xp"), new Xp_fluid.Flowing());
        XP_BUCKET = Registry.register(Registry.ITEM, new Identifier(XpStorage.MOD_ID, "xp_bucket"),
                new BucketItem(STILL_XP, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1)));
        XP = Registry.register(Registry.BLOCK, new Identifier(XpStorage.MOD_ID, "xp"), new FluidBlock(STILL_XP, FabricBlockSettings.copy(Blocks.LAVA)){});
    }
}
