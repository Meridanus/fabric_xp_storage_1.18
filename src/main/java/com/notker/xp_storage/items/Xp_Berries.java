package com.notker.xp_storage.items;

import com.notker.xp_storage.XpStorage;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

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
            //world.spawnEntity(new ExperienceOrbEntity(world, user.getX(), user.getY(), user.getZ(), XpStorage.XP_PER_BERRIE));
            pe.addExperience(XpStorage.XP_PER_BERRIE);
        }

       return super.finishUsing(stack, world, user);
    }
}
