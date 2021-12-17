package com.notker.xp_storage.blocks;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

import java.util.Objects;

public class StorageBlockEntityRenderer implements BlockEntityRenderer<StorageBlockEntity> {

    public StorageBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    private static final ItemStack displayItem = new ItemStack(Items.ENCHANTED_BOOK, 1);


    @Override
    public void render(StorageBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        MinecraftClient mc = MinecraftClient.getInstance();
        HitResult rtr = mc.crosshairTarget;

     
        //System.out.println(entity);
        if(entity != null && entity.getPos() != null && rtr != null && rtr.getType() == HitResult.Type.BLOCK && ((BlockHitResult)rtr).getBlockPos() != null && ((BlockHitResult)rtr).getBlockPos().equals(entity.getPos()))
        {
            TranslatableText levelsString = XpFunctions.xp_to_text(entity);
            float opacity = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25f);
            int j = (int)(opacity * 255.0F) << 24;
            float halfWidth = -mc.textRenderer.getWidth(levelsString) >> 1;
            Matrix4f positionMatrix;

            matrices.push();
            matrices.translate(0.5D, 1.2D, 0.5D);
            matrices.multiply(MinecraftClient.getInstance().getEntityRenderDispatcher().camera.getRotation());
            matrices.scale(-0.0125F, -0.0125F, 0.0125F);
            //positionMatrix = matrices.peek().getModel();
            positionMatrix = matrices.peek().getPositionMatrix();
            mc.textRenderer.draw(levelsString, halfWidth, 0, 553648127, false, positionMatrix, vertexConsumers, true, j, light); //renderString
            mc.textRenderer.draw(levelsString, halfWidth, 0, -1, false, positionMatrix, vertexConsumers, false, 0, light);
            matrices.pop();
        }

        try {
            //if (entity.containerExperience != 0) {
            if (entity != null && entity.getCachedState().get(StorageBlock.CHARGED)) {
                matrices.push();

                long time = Objects.requireNonNull(entity.getWorld()).getTime();

                double offset = Math.sin((time + tickDelta) / 20.0) / 8.0;
                matrices.translate(0.5, 0.37 + offset, 0.5);


                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((time + tickDelta) * 4));

                int lightAbove = WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().up());
                MinecraftClient.getInstance().getItemRenderer().renderItem(displayItem, ModelTransformation.Mode.GROUND, lightAbove, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, 0);

                matrices.pop();
            }
        } catch (Exception e) {
           // System.out.println(e);
        }



    }


}
