package com.notker.xp_storage.blocks;

import com.notker.xp_storage.regestry.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class StorageBlockEntity extends BlockEntity {

    public int containerExperience = 0;
    public UUID player_uuid = Util.NIL_UUID;
    public Text playerName = Text.of("");



    public StorageBlockEntity(BlockPos pos, BlockState state) {

        super(ModBlocks.STORAGE_BLOCK_ENTITY, pos, state);
    }



    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.putInt("containerExperience", this.containerExperience);
        tag.putUuid("player_uuid", this.player_uuid);
        tag.putString("playerName", this.playerName.asString());
        writeIdToNbt(tag, ModBlocks.STORAGE_BLOCK_ENTITY);

        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), net.minecraft.block.Block.NOTIFY_ALL);
        }


    }


    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        this.containerExperience = tag.getInt("containerExperience");
        this.player_uuid = tag.getUuid("player_uuid");
        this.playerName = Text.of(tag.getString("playerName"));
    }

    public NbtCompound getNbtData() {
        NbtCompound stackTag = new NbtCompound();
        stackTag.putInt("containerExperience", this.containerExperience);
        stackTag.putUuid("player_uuid", this.player_uuid);
        stackTag.putString("playerName", this.playerName.asString());
        return stackTag;
    }

    public int getContainerExperience() {
        return containerExperience;
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }
}
