package com.notker.xp_storage.items;

import com.notker.xp_storage.XpStorage;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
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


    //TODO: Why is this called sometimes 2 Times ????????
    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ServerWorld sw = world instanceof ServerWorld ? (ServerWorld)world : null;
        if (sw != null) {
            ExperienceOrbEntity.spawn(sw, user.getPos(), XpStorage.XP_PER_BERRIE);
        }

       return super.finishUsing(stack, world, user);
    }
}
