package com.notker.xp_storage;

import com.notker.xp_storage.blocks.StorageBlockEntityRenderer;
import com.notker.xp_storage.regestry.ModBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;

public class XpStorageClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.register(ModBlocks.STORAGE_BLOCK_ENTITY, StorageBlockEntityRenderer::new);
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BLOCK_XP_OBELISK, RenderLayer.getCutout());

    }
}
