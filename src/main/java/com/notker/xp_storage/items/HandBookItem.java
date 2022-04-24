package com.notker.xp_storage.items;

import com.notker.xp_storage.XpStorage;
import com.notker.xp_storage.regestry.ModItems;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.List;

public class HandBookItem extends Item {
    private final boolean isPatchouliLoaded = FabricLoader.getInstance().isModLoaded("patchouli");

    public HandBookItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient() && isPatchouliLoaded) {
            PatchouliAPI.get().openBookGUI((ServerPlayerEntity) user, new Identifier(XpStorage.MOD_ID, "xps_lexica"));
            return TypedActionResult.success(user.getStackInHand(hand));
        }
        return TypedActionResult.fail(user.getStackInHand(hand));
    }


    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        if (!isPatchouliLoaded)
            tooltip.add(new TranslatableText("item.xps.patchouli_book.tooltip"));
    }
}

