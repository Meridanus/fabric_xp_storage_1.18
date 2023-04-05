package com.notker.xp_storage.blocks;

import com.notker.xp_storage.XpFunctions;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

import java.util.Objects;

public class StorageBlockEntityRenderer implements BlockEntityRenderer<StorageBlockEntity> {

    public StorageBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }


    @Override
    public void render(StorageBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        MinecraftClient mc = MinecraftClient.getInstance();
        HitResult rtr = mc.crosshairTarget;

     
        //System.out.println(entity);
        if(entity != null && entity.getPos() != null && rtr != null && rtr.getType() == HitResult.Type.BLOCK && ((BlockHitResult)rtr).getBlockPos() != null && ((BlockHitResult)rtr).getBlockPos().equals(entity.getPos()))
        {
            Text levelsString = XpFunctions.xp_to_text(entity.getContainerExperience());
            //TranslatableText levelsString = XpFunctions.xp_to_text(Integer.MAX_VALUE);
            float opacity = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25f);
            int j = (int)(opacity * 255.0F) << 24;
            float halfWidth = -mc.textRenderer.getWidth(levelsString) >> 1;
            Matrix4f positionMatrix;

            matrices.push();
            matrices.translate(0.5D, 1.2D, 0.5D);
            matrices.multiply(MinecraftClient.getInstance().getEntityRenderDispatcher().camera.getRotation());
            matrices.scale(-0.0125F, -0.0125F, 0.0125F);
            positionMatrix = matrices.peek().getPositionMatrix();

            mc.textRenderer.draw(levelsString, halfWidth, 0, 553648127, false, positionMatrix, vertexConsumers, TextRenderer.TextLayerType.SEE_THROUGH, j, light); //Shadow
            mc.textRenderer.draw(levelsString, halfWidth, 0, -1, false, positionMatrix, vertexConsumers, TextRenderer.TextLayerType.NORMAL , 0, light); //String
            matrices.pop();
        }

        try {
            //if (entity.containerExperience != 0) {
            ItemStack displayItem;
            if (entity != null && entity.vacuum) {

                displayItem = new ItemStack(Items.HOPPER, 1);
            } else {
                displayItem = new ItemStack(Items.ENCHANTED_BOOK, 1);
            }


            if (entity != null && (entity.getCachedState().get(StorageBlock.CHARGED) || entity.vacuum)) {
                matrices.push();
                long time = Objects.requireNonNull(entity.getWorld()).getTime();

                double offset = Math.sin((time + tickDelta) / 20.0) / 10.0;
                matrices.translate(0.5, 0.40 + offset, 0.5);

                //Quaternionf quaternionf = (new Quaternionf()).rotateY((time + tickDelta) * 3);
                //matrices.multiply(quaternionf);

                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((time + tickDelta) * 3));

                int lightAbove = WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().up());
                MinecraftClient.getInstance().getItemRenderer().renderItem(displayItem, ModelTransformationMode.GROUND, lightAbove, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getWorld(), 0);
                matrices.pop();
            }
        } catch (Exception e) {
           // System.out.println(e);
        }



    }


}
