package com.notker.xp_storage.items;

import com.notker.xp_storage.XpStorage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

public class Xp_Berries extends Item {
    public Xp_Berries(Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }



    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ServerWorld sw = world instanceof ServerWorld ? (ServerWorld)world : null;
        PlayerEntity pe = user instanceof PlayerEntity ? (PlayerEntity) user : null;
        if (sw != null && pe != null) {
            //ExperienceOrbEntity.spawn(sw, user.getPos(), XpStorage.XP_PER_BERRIE);
            if (pe.isSneaking()) {
                world.spawnEntity(new ExperienceOrbEntity(world, user.getX(), user.getY(), user.getZ(), XpStorage.XP_PER_BERRIE));
            } else {
                pe.addExperience(XpStorage.XP_PER_BERRIE);
            }
        }

       return super.finishUsing(stack, world, user);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        super.appendTooltip(stack,world,tooltip,tooltipContext);

        tooltip.add(new TranslatableText("item.xps.more.info.tooltip"));
        if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), XpStorage.shiftKey)) {
            tooltip.remove(new TranslatableText("item.xps.more.info.tooltip"));

            tooltip.add(new TranslatableText("item.tooltip.xp_berrie", XpStorage.XP_PER_BERRIE).formatted(Formatting.WHITE));
            tooltip.add(new TranslatableText("item.tooltip.xp_berrie_sneak").formatted(Formatting.AQUA));
        }


    }

}
