package com.notker.xp_storage.items;

import com.notker.xp_storage.XpStorage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;

public class Xp_removerItem extends Item {
    public Xp_removerItem(Settings settings) {
        super(settings);
    }

public static String tagId = "Mode";

    @Override
    public boolean hasGlint(ItemStack stack) {
        if (stack.hasNbt() && Objects.requireNonNull(stack.getNbt()).contains(tagId)) {
            return stack.getNbt().getBoolean(tagId);
        }
        return false;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = (user.getMainHandStack().getItem() instanceof Xp_removerItem) ? user.getMainHandStack() : user.getOffHandStack();

        NbtCompound nbt = getNbtCompound(stack);



            // Play sound when switching
            if (user.isSneaking()) {
                if (world.isClient) {
                    user.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
                }
                nbt.putBoolean(tagId, !nbt.getBoolean(tagId));
        }




        if (nbt.getBoolean(tagId)) {
            stack.setNbt(nbt);
        } else {
            stack.removeSubNbt(tagId);
        }



        return new TypedActionResult<>(ActionResult.SUCCESS, user.getStackInHand(hand));
    }

    private NbtCompound getNbtCompound(ItemStack stack) {
        NbtCompound nbt;
        if (stack.hasNbt())
        {
            nbt = stack.getNbt();
        }
        else
        {
            nbt = new NbtCompound();
        }

        if (nbt != null && !nbt.contains(tagId)) {
            nbt.putBoolean(tagId, false);
        }

        return nbt;
    }


    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        super.appendTooltip(stack,world,tooltip,tooltipContext);

        tooltip.add(new TranslatableText("item.xps.more.info.tooltip"));
        if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), XpStorage.shiftKey)) {
            tooltip.remove(new TranslatableText("item.xps.more.info.tooltip"));

            tooltip.add(new TranslatableText("item.tooltip.xp_remover_sneak").formatted(Formatting.RED));

            NbtCompound nbt = getNbtCompound(stack);
            if (nbt.getBoolean(tagId)) {
                tooltip.add(new TranslatableText("item.tooltip.xp_rod").formatted(Formatting.AQUA));
            } else {
                tooltip.add(new TranslatableText("item.tooltip.xp_remover").formatted(Formatting.AQUA));
            }
            tooltip.add(new TranslatableText("item.tooltip.stack_size"));
        }


    }
}
