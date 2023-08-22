package com.notker.xp_storage;

import com.notker.xp_storage.blocks.StorageBlockEntityRenderer;
import com.notker.xp_storage.regestry.ModBlocks;
import com.notker.xp_storage.regestry.ModFluids;
import com.notker.xp_storage.regestry.ModItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.util.Identifier;

public class XpStorageClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererFactories.register(ModBlocks.STORAGE_BLOCK_ENTITY, StorageBlockEntityRenderer::new);
        //BlockEntityRendererRegistry.register(ModBlocks.STORAGE_BLOCK_ENTITY, StorageBlockEntityRenderer::new); old
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BLOCK_XP_OBELISK, RenderLayer.getCutout());

        ModelPredicateProviderRegistry.register(ModItems.XP_REMOVER, new Identifier("active"), (itemStack, clientWorld, livingEntity, count) -> {
            if (livingEntity == null) {
                return 0.0F;
            }
            return itemStack.hasGlint() ? 0.0F : 1.0F;
        });

        //FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.STILL_XP, ModFluids.FLOWING_XP, new SimpleFluidRenderHandler(
        //        new Identifier("minecraft:block/water_still"),
        //        new Identifier("minecraft:block/water_flow"),
        //        0x6dff00
        //));

        //ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
        //    registry.register(new Identifier("xps:blocks/xp_still"));
        //    registry.register(new Identifier("xps:blocks/xp_flow"));
        //});

        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.LIQUID_XP, ModFluids.LIQUID_XP_FLOWING, new SimpleFluidRenderHandler(
                new Identifier("xps:block/xp_still"),
                new Identifier("xps:block/xp_flow"),
                0xCCFF00
        ));

        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), ModFluids.LIQUID_XP, ModFluids.LIQUID_XP_FLOWING);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), ModBlocks.XP_BERRIE_BUSH_BLOCK);
    }
}
