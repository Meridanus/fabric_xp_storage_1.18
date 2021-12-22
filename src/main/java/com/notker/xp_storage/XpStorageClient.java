package com.notker.xp_storage;

import com.notker.xp_storage.blocks.StorageBlockEntityRenderer;
import com.notker.xp_storage.regestry.ModBlocks;
import com.notker.xp_storage.regestry.ModItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

public class XpStorageClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.register(ModBlocks.STORAGE_BLOCK_ENTITY, StorageBlockEntityRenderer::new);
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BLOCK_XP_OBELISK, RenderLayer.getCutout());

        FabricModelPredicateProviderRegistry.register(ModItems.XP_REMOVER, new Identifier("active"), (itemStack, clientWorld, livingEntity, count) -> {
            if (livingEntity == null) {
                return 0.0F;
            }
            return itemStack.hasGlint() ? 0.0F : 1.0F;
        });
    }
}
