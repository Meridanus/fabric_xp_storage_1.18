package com.notker.xp_storage.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class Xp_dust extends Item {
    public Xp_dust(Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
}
