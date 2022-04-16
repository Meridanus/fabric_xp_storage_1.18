package com.notker.xp_storage.regestry;

import com.notker.xp_storage.XpStorage;
import com.notker.xp_storage.fluids.LiquidXP;
import com.notker.xp_storage.fluids.LiquidXP;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.Material;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModFluids {

    public static FlowableFluid LIQUID_XP;
    public static FlowableFluid LIQUID_XP_FLOWING;
    public static Item XP_BUCKET;
    public static Block XP_FLUID;

    public static void registerFluids() {
        LIQUID_XP = Registry.register(Registry.FLUID, new Identifier(XpStorage.MOD_ID, "xp_fluid"), new LiquidXP.Still());
        LIQUID_XP_FLOWING = Registry.register(Registry.FLUID, new Identifier(XpStorage.MOD_ID, "flowing_xp"), new LiquidXP.Flowing());
        XP_BUCKET = Registry.register(Registry.ITEM, new Identifier(XpStorage.MOD_ID, "xp_bucket"),
                new BucketItem(LIQUID_XP, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1)));
        XP_FLUID = Registry.register(Registry.BLOCK, new Identifier(XpStorage.MOD_ID, "xp_fluid"), new FluidBlock(LIQUID_XP, FabricBlockSettings
                .of(Material.LAVA)
                .noCollision()
                .luminance(10)
        ){});
    }
}
