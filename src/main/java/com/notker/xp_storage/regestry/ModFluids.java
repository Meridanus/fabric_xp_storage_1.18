package com.notker.xp_storage.regestry;

import com.notker.xp_storage.XpStorage;
import com.notker.xp_storage.fluids.LiquidXP;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;

public class ModFluids {

    public static FlowableFluid LIQUID_XP;
    public static FlowableFluid LIQUID_XP_FLOWING;
    public static Item XP_BUCKET;
    public static Block XP_FLUID;

    public static void registerFluids() {
        LIQUID_XP = Registry.register(Registries.FLUID, new Identifier(XpStorage.MOD_ID, "xp_fluid"), new LiquidXP.Still());
        LIQUID_XP_FLOWING = Registry.register(Registries.FLUID, new Identifier(XpStorage.MOD_ID, "flowing_xp"), new LiquidXP.Flowing());
        XP_BUCKET = Registry.register(Registries.ITEM, new Identifier(XpStorage.MOD_ID, "xp_bucket"),
                new BucketItem(LIQUID_XP, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1)));
        XP_FLUID = Registry.register(Registries.BLOCK, new Identifier(XpStorage.MOD_ID, "xp_fluid"), new FluidBlock(LIQUID_XP, FabricBlockSettings.create()
                .liquid()
                .noCollision()
                .luminance(10)
        ){});
    }
}
