package com.notker.xp_storage.items;

import com.notker.xp_storage.XpFunctions;
import com.notker.xp_storage.XpStorage;
import com.notker.xp_storage.regestry.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public class StorageBlockItem extends BlockItem {
    public StorageBlockItem(Block block, Settings settings) { super(block, settings); }

    @Override
    public boolean hasGlint(ItemStack stack) {
        if (stack.hasNbt() && stack.getNbt() != null) {

            NbtCompound comp = stack.getNbt().getCompound(ModBlocks.TAG_ID);

            return !comp.isEmpty();
        }
        return false;
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        if (itemStack.hasNbt() && itemStack.getNbt() != null) {

            tooltip.add(Text.translatable("item.xps.more.info.tooltip"));
            if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), XpStorage.shiftKey)) {
                tooltip.remove(Text.translatable("item.xps.more.info.tooltip"));

                NbtCompound comp = itemStack.getNbt().getCompound(ModBlocks.TAG_ID);
                if (comp.isEmpty()) {
                    return;
                }

                UUID id = comp.getUuid("player_uuid");
                if (!id.equals(Util.NIL_UUID)) {
                    String playerName = comp.getString("playerName");
                    tooltip.add(Text.translatable("item.tooltip.owner", playerName));
                }
                int storedXP;

                if (comp.contains("containerExperience")) {
                    storedXP = comp.getInt("containerExperience");
                } else {
                    storedXP = (int) (comp.getLong("amount") / XpStorage.MB_PER_XP);
                }


                if (storedXP > 0) {
                    tooltip.add(XpFunctions.xp_to_text(storedXP));
                }

                if (comp.getBoolean("vacuum")) {
                    tooltip.add(Text.translatable("text.storageBlock.vacuum"));
                }

            }
        }

    }

}
